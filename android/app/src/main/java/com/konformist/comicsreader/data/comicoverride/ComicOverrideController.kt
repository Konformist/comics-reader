package com.konformist.comicsreader.data.comicoverride

import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.webapi.Validation

class ComicOverrideController(private val dao: ComicOverrideDao) {
  private val entityName = "ComicOverride"

  fun readByComic(comicId: Long): ComicOverride? {
    return dao.readByComic(comicId)
  }

  fun read(id: Long): ComicOverride? {
    return dao.read(id)
  }

  @Throws(DatabaseException::class)
  fun create(value: ComicOverrideCreate): Long {
    val rowId = dao.create(value)
    Validation.dbCreate(rowId, entityName)
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(value: ComicOverrideUpdate): Boolean {
    val row = read(value.id) ?: return false
    val count = dao.update(value.merge(row))
    Validation.dbUpdate(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun delete(value: ComicOverrideDelete): Boolean {
    val count = dao.delete(value)
    Validation.dbDelete(count, entityName)
    return true
  }
}