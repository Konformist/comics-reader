package com.konformist.comicsreader.data.chapterpage

import androidx.core.net.toUri
import com.konformist.comicsreader.data.appfile.AppFileController
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.FileManager
import com.konformist.comicsreader.utils.RequestUtils
import com.konformist.comicsreader.webapi.Validation
import java.io.File
import java.io.InputStream
import java.net.URL

class ChapterPageController(
  private val dao: ChapterPageDao,
  private val filesController: AppFileController,
) {
  private val entityName = "ChapterPage"

  @Throws(DatabaseException::class)
  fun createFile(value: ChapterPage, mime: String, file: InputStream): Long {
    if (value.fileId != 0L) filesController.deleteImage(value.fileId)

    val chapterDir = File(FileManager.chaptersDir, value.chapterId.toString())
    val rowId = filesController.createImage(chapterDir, mime, file)
    val update = ChapterPageFileUpdate(id = value.id, fileId = rowId)
    val count = dao.updateFile(update.merge(value))
    Validation.dbUpdate(count, entityName)
    return rowId
  }

  fun createFileFromRequest(id: Long, link: String, cookie: String): Long {
    val connection = RequestUtils.getConnection(URL(link), cookie)
    val mimeType = connection.contentType
    return connection.inputStream.use {
      val row = read(id) ?: return 0L
      val result = createFile(row, mimeType, it)
      connection.disconnect()
      result
    }
  }

  fun createFileFromUri(id: Long, uri: String): Long {
    val extension = FileManager.getFileExtension(uri)
    val mime = FileManager.getMimeFromExtension(extension)
    val result = FileManager.getInputByUri(uri.toUri()) { file ->
      read(id)?.let { row -> createFile(row, mime, file) }
    }

    return result ?: 0L
  }

  @Throws(DatabaseException::class)
  fun deleteFile(value: ChapterPage): Boolean {
    if (value.fileId == 0L) return false

    filesController.deleteImage(value.fileId)
    val update = ChapterPageFileUpdate(id = value.id, fileId = 0)
    val count = dao.updateFile(update.merge(value))
    Validation.dbUpdate(count, entityName)
    return true
  }

  fun readByChapterAll(chapterId: Long): List<ChapterPageWithFile> {
    return dao.readByChapterAll(chapterId)
  }

  fun readWithFile(id: Long): ChapterPageWithFile {
    return dao.readWithFile(id)
  }

  fun read(id: Long): ChapterPage? {
    return dao.read(id)
  }

  @Throws(DatabaseException::class)
  fun create(value: ChapterPageCreate): Long {
    val rowId = dao.create(value)
    Validation.dbCreate(rowId, entityName)
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(value: ChapterPageUpdate): Boolean {
    val row = read(value.id) ?: return false
    val count = dao.update(value.merge(row))
    Validation.dbUpdate(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun delete(value: ChapterPageDelete): Boolean {
    val row = read(value.id) ?: return false
    if (row.fileId != 0L) filesController.deleteImage(row.fileId)
    val count = dao.delete(value)
    Validation.dbDelete(count, entityName)
    return true
  }
}