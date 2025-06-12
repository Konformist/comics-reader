package com.konformist.comicsreader.utils.comicarchive

import androidx.core.net.toUri
import com.konformist.comicsreader.data.chapter.ChapterCreate
import com.konformist.comicsreader.data.chapter.ChapterWithPages
import com.konformist.comicsreader.data.comic.ComicController
import com.konformist.comicsreader.data.comic.ComicCreate
import com.konformist.comicsreader.data.comic.ComicLite
import com.konformist.comicsreader.exceptions.FilesException
import com.konformist.comicsreader.utils.FileManager
import com.konformist.comicsreader.utils.archive.ArchiveFormat
import com.konformist.comicsreader.utils.archive.ArchiveUtils
import kotlinx.coroutines.runBlocking
import java.io.File

class ComicArchive() {
  private val dirTemp = File(FileManager.cacheDir, "comic-temp")

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

  data class ChapterResult(
    val chapter: ChapterCreate,
    val files: List<File>,
  )

  data class FromResult(
    val comic: ComicCreate,
    val chapters: List<ChapterResult>
  )

  fun comicFromArchive(uriStr: String): FromResult? {
    val extension = FileManager.getFileExtension(uriStr)
    val archiveFormat = getArchiveFormat(extension)

    return FileManager.getInputByUri(uriStr.toUri()) { file ->
      createTempDirectory()
      ArchiveUtils
        .extractFactory()
        .extract(file, archiveFormat, dirTemp)

      val files = mutableListOf<File>()

      // TODO хз, вроде хрень
      runBlocking {
        dirTemp.walk().forEach { file ->
          if (file.isFile) files.add(file)
        }
      }

      val comic = ComicCreate(name = "New Comic")
      val chapter = ChapterResult(
        chapter = ChapterCreate(comicId = 0),
        files = files,
      )

      FromResult(
        comic = comic,
        chapters = listOf(chapter)
      )
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

    chapters.forEachIndexed { iChapter, chapter ->
      val chapterPad = pad(chaptersLength, iChapter + 1)
      val chapterNameDir = if (chapter.chapter?.name?.isNotBlank() != true) chapterPad
      else "$chapterPad-${chapter.chapter.name}"
      val chapterDir = File(dirTemp, chapterNameDir)
      chapterDir.mkdirs()
      compress.addFile(chapterDir)

      val pagesLength = chapter.pages.size.toString().length
      chapter.pages.forEachIndexed { iPage, page ->
        page.file?.let { file ->
          val pagePad = pad(pagesLength, iPage + 1)
          val fileFrom = File(FileManager.filesDir, file.path)
          val fileTo = File(chapterDir, "$pagePad.${FileManager.getExtensionFromMime(file.mime)}")
          fileFrom.copyTo(fileTo)
        }
      }
    }

    val outFile = createOutputFile(comic.comic.name)
    val parentDir = outFile.parentFile ?: return false
    if (!parentDir.exists()) parentDir.mkdirs()

    compress.compress(outFile, ArchiveFormat.ZIP)

    return true
  }
}