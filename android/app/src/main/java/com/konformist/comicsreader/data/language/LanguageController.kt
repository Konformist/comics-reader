package com.konformist.comicsreader.data.language

import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.webapi.Validation

class LanguageController(private val dao: LanguageDao) {
  private val entityName = "Language"
  fun readAll(): List<Language> {
    return dao.readAll()
  }

  fun read(id: Long): Language? {
    return dao.read(id)
  }

  @Throws(DatabaseException::class)
  fun create(value: LanguageCreate): Long {
    val rowId = dao.create(value)
    Validation.dbCreate(rowId, entityName)
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(value: LanguageUpdate): Boolean {
    val row = read(value.id) ?: return false
    val count = dao.update(value.merge(row))
    Validation.dbUpdate(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun delete(value: LanguageDelete): Boolean {
    val count = dao.delete(value)
    Validation.dbDelete(count, entityName)
    return true
  }
}