package com.konformist.comicsreader.webapi

import androidx.core.net.toUri
import com.konformist.comicsreader.App
import com.konformist.comicsreader.db.chapter.ChapterCreate
import com.konformist.comicsreader.db.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.db.comic.ComicCreate
import com.konformist.comicsreader.exceptions.FilesException
import com.konformist.comicsreader.exceptions.ValidationException
import com.konformist.comicsreader.utils.FileManager
import com.konformist.comicsreader.utils.archive.ArchiveFormat
import com.konformist.comicsreader.utils.archive.ArchiveUtils
import com.konformist.comicsreader.webapi.controllers.ChapterController
import com.konformist.comicsreader.webapi.controllers.ComicController
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class ArchiveComic(
  private val comicController: ComicController,
  private val chapterController: ChapterController,
) {
  @Throws(ValidationException::class, FilesException::class)
  fun addComicFromArchive(uriStr: String): Long {
    val extension = FileManager.getFileExtension(uriStr)
    val archiveFormat = getArchiveFormat(extension)

    val path = App.context.contentResolver.openFileDescriptor(uriStr.toUri(), "r") ?: return 0L
    val outFile = createTempDirectory()
    val inputStream = FileInputStream(path.fileDescriptor)

    try {
      extractArchive(inputStream, archiveFormat, outFile)
    } finally {
      inputStream.close()
      path.close()
    }

    val comicId = createComic()
    val listFiles = outFile.listFiles() ?: throw FilesException("No files")

    processFiles(listFiles, comicId)

    // Удаление временной папки после завершения
    outFile.deleteRecursively()

    return comicId
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

  private fun extractArchive(inputStream: InputStream, archiveFormat: ArchiveFormat, outFile: File) {
    val extractor = ArchiveUtils.extractFactory()
    extractor.extract(inputStream, archiveFormat, outFile)
  }

  private fun createComic(): Long {
    return comicController.create(ComicCreate(name = "New Comic"))
  }

  private fun createChapter(comicId: Long, chapterName: String): Long {
    return chapterController.create(ChapterCreate(name = chapterName, comicId = comicId))
  }

  private fun processFile(file: File, chapterId: Long) {
    val pageId = chapterController.createPage(ChapterPageCreate(chapterId = chapterId))
    val page = chapterController.readPage(pageId)
    FileInputStream(file).use { item ->
      chapterController.createPageFile(page, FileManager.getMimeFromExtension(file.extension), item)
    }
  }

  private fun processFiles(files: Array<File>, comicId: Long) {
    if (files[0].isFile) {
      val chapterId = createChapter(comicId, "")
      files.forEach { file -> processFile(file, chapterId) }
    } else {
      files.forEach { directory ->
        val filesInDir = directory.listFiles()
        if (filesInDir != null) {
          val chapterId = createChapter(comicId, directory.name)
          filesInDir.forEach { file -> processFile(file, chapterId) }
        }
      }
    }
  }

  fun uploadComic(id: Long): Boolean {
    val dirTmp = createTempDirectory()

    val comic = comicController.read(id)
    val chapters = chapterController.readByComicAll(id)

    val compress = ArchiveUtils.compressFactory()
    val chaptersLength = chapters.size.toString().length

    chapters.forEachIndexed { index, chapter ->
      val chapterDir = createChapterDirectory(dirTmp, index + 1, chaptersLength)
      compress.addFile(chapterDir)
      val pagesLength = chapter.pages.size.toString().length

      chapter.pages.forEachIndexed { iPage, page ->
        page.file?.let { file ->
          val fileFrom = File(FileManager.filesDir, file.path)
          val fileTo = createPageFile(chapterDir, fileFrom, iPage + 1, pagesLength)
          fileFrom.copyTo(fileTo)
        }
      }
    }

    val outFile = createOutputFile(comic.name)
    val parentDir = outFile.parentFile ?: return false
    if (!parentDir.exists()) parentDir.mkdirs()

    compress.compress(outFile, ArchiveFormat.ZIP)
    dirTmp.deleteRecursively()

    return true
  }

  private fun createTempDirectory(): File {
    val dirTmp = File(FileManager.cacheDir, "comic-tmp")
    if (dirTmp.exists()) dirTmp.deleteRecursively()
    dirTmp.mkdirs()
    return dirTmp
  }

  private fun createChapterDirectory(parentDir: File, chapterIndex: Int, chaptersLength: Int): File {
    val chapterDirName = "%0${chaptersLength}d".format(chapterIndex)
    val chapterDir = File(parentDir.absolutePath, chapterDirName)
    chapterDir.mkdirs()
    return chapterDir
  }

  private fun createPageFile(chapterDir: File, file: File, pageIndex: Int, pagesLength: Int): File {
    val fileToName = "%0${pagesLength}d".format(pageIndex)
    return File(chapterDir.absolutePath, "$fileToName.${file.extension}")
  }

  private fun createOutputFile(comicName: String): File {
    val nameDir = comicName.replace(Regex("""\W+"""), "_")
    return File(FileManager.downloadsAppDir, "$nameDir.${ComicController.FORMAT_CBZ}")
  }
}