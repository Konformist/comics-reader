package com.konformist.comicsreader.comicarchive

import androidx.core.net.toUri
import com.konformist.comicsreader.data.chapter.ChapterWithPages
import com.konformist.comicsreader.data.comic.ComicController
import com.konformist.comicsreader.data.comic.ComicLite
import com.konformist.comicsreader.exceptions.FilesException
import com.konformist.comicsreader.utils.FileManager
import com.konformist.comicsreader.utils.archive.ArchiveFormat
import com.konformist.comicsreader.utils.archive.ArchiveUtils
import java.io.File

class ComicArchive() {
  private val dirTemp = File(FileManager.cacheDir, "comic-temp")
  private val metaXml = "meta.acbf"

  private fun deleteTempDirectory() {
    if (dirTemp.exists()) dirTemp.deleteRecursively()
  }

  fun recycle() = deleteTempDirectory()

  private fun createTempDirectory() {
    deleteTempDirectory()
    dirTemp.mkdirs()
  }

  private fun getArchiveFormat(extension: String): ArchiveFormat {
    return when (extension) {
      ComicController.FORMAT_CBZ,
      ComicController.FORMAT_ZIP -> ArchiveFormat.ZIP

      ComicController.FORMAT_CBT,
      ComicController.FORMAT_TAR -> ArchiveFormat.TAR

      else -> throw FilesException("Unknown extension $extension")
    }
  }

  private fun readWithoutMeta(tree: Sequence<File>): MetaXmlReader.Result {
    val pages: MutableList<String> = tree
      .map { it.path }
      .toMutableList()

    return MetaXmlReader.Result(
      title = "New Comic",
      cover = tree.first().path,
      chapters = mutableListOf(
        MetaXmlReader.ResultChapter(pages = pages)
      )
    )
  }

  private fun readWithMeta(meta: File): MetaXmlReader.Result {
    val xmlReader = MetaXmlReader()
    val data = xmlReader.read(meta)

    if (data.cover.isNotBlank())
      data.cover = File(dirTemp, data.cover).path

    data.chapters.forEach { item ->
      item.pages = item.pages
        .map { page -> File(dirTemp, page).path }
        .toMutableList()
    }

    return data
  }

  fun comicFromArchive(uriStr: String): MetaXmlReader.Result? {
    val extension = FileManager.getExtension(uriStr)
    val archiveFormat = getArchiveFormat(extension)

    return FileManager.getInputByUri(uriStr.toUri()) { file ->
      createTempDirectory()
      ArchiveUtils
        .extractFactory()
        .extract(file, archiveFormat, dirTemp)

      val tree = dirTemp.walk().filter { file -> file.isFile }
      val meta = tree.find { file -> file.name == metaXml }

      meta?.let { readWithMeta(it) } ?: readWithoutMeta(tree)
    }
  }

  private fun pad(length: Int, index: Int): String = "%0${length}d".format(index)

  private fun createOutputFile(comicName: String): File {
    val nameDir = comicName
      .replace(Regex("""\W+"""), " ")
      .trim()
    return File(FileManager.downloadsAppDir, "$nameDir.${ComicController.FORMAT_CBZ}")
  }

  fun comicToArchive(comic: ComicLite, chapters: List<ChapterWithPages>): Boolean {
    if (comic.comic == null) return false

    createTempDirectory()

    val compress = ArchiveUtils.compressFactory()
    val chaptersLength = chapters.size.toString().length

    val metaFile = File(dirTemp, metaXml)
    compress.addFile(metaFile)
    val meta = MetaXmlCreator()
      .addComic(comic.comic)

    if (comic.cover?.file != null) {
      val coverFrom = File(FileManager.filesDir, comic.cover.file.path)
      val coverTo = File(dirTemp, "cover.${coverFrom.extension}")
      coverFrom.copyTo(coverTo)
      compress.addFile(coverTo)
      meta.addCover(coverTo.relativeTo(dirTemp).path)
    }

    chapters.forEachIndexed { iChapter, chapter ->
      chapter.chapter?.let {
        val chapterPad = pad(chaptersLength, iChapter + 1)
        val chapterDir = File(dirTemp, chapterPad)

        chapterDir.mkdirs()
        compress.addFile(chapterDir)

        val files = mutableListOf<String>()

        val pagesLength = chapter.pages.size.toString().length
        chapter.pages.forEachIndexed { iPage, page ->
          page.file?.let { file ->
            val pagePad = pad(pagesLength, iPage + 1)
            val fileFrom = File(FileManager.filesDir, file.path)
            val fileTo = File(chapterDir, "$pagePad.${fileFrom.extension}")
            fileFrom.copyTo(fileTo)
            files.add(fileTo.relativeTo(dirTemp).path)
          }
        }

        val name = if (chapter.chapter.name.isBlank()) chapterPad
        else chapter.chapter.name
        meta.addChapter(name, files)
      }
    }

    meta.create(metaFile)

    val outFile = createOutputFile(comic.comic.name)
    val parentDir = outFile.parentFile ?: return false
    if (!parentDir.exists()) parentDir.mkdirs()

    compress.compress(outFile, ArchiveFormat.ZIP)

    return true
  }
}