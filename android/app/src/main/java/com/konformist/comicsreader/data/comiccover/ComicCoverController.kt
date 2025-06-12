package com.konformist.comicsreader.data.comiccover

import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.data.appfile.AppFileController
import com.konformist.comicsreader.utils.FileManager
import com.konformist.comicsreader.webapi.Validation
import java.io.File
import java.io.InputStream

class ComicCoverController(
  private val dao: ComicCoverDao,
  private val filesController: AppFileController,
) {
  private val entityName = "ComicCover"

  @Throws(DatabaseException::class)
  fun createFile(value: ComicCover, mime: String, file: InputStream): Long {
    if (value.fileId != 0L) filesController.deleteImage(value.fileId)

    val comicDir = File(FileManager.comicsDir, value.comicId.toString())
    val rowId = filesController.createImage(comicDir, mime, file)
    val update = ComicCoverFileUpdate(id = value.id, fileId = rowId)
    val count = dao.updateFile(update.merge(value))
    Validation.dbUpdate(count, entityName)
    return rowId
  }

  @Throws(DatabaseException::class)
  fun deleteFile(value: ComicCover): Boolean {
    if (value.fileId == 0L) return false

    filesController.deleteImage(value.fileId)
    val update = ComicCoverFileUpdate(id = value.id, fileId = 0)
    val count = dao.updateFile(update.merge(value))
    Validation.dbUpdate(count, entityName)
    return true
  }

  fun read(id: Long): ComicCover? {
    return dao.read(id)
  }

  fun readByComic(id: Long): ComicCover? {
    return dao.readByComic(id)
  }

  @Throws(DatabaseException::class)
  fun create(cover: ComicCoverCreate): Long {
    val rowId = dao.create(cover)

    Validation.dbCreate(rowId, entityName)
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(value: ComicCoverUpdate): Boolean {
    val row = read(value.id) ?: return false
    val count = dao.update(value.merge(row))
    Validation.dbUpdate(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun delete(value: ComicCoverDelete): Boolean {
    val row = read(value.id) ?: return false
    if (row.fileId != 0L) filesController.deleteImage(row.fileId)
    val count = dao.delete(value)
    Validation.dbDelete(count, entityName)
    return true
  }
}