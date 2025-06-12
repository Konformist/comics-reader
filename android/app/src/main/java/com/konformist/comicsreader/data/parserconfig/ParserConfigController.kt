package com.konformist.comicsreader.data.parserconfig

import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.webapi.Validation

class ParserConfigController(private val dao: ParserConfigDao) {
  private val entityName = "ParserConfig"

  fun readAll(): List<ParserConfig> {
    return dao.readAll()
  }

  fun read(id: Long): ParserConfig? {
    return dao.read(id)
  }

  @Throws(DatabaseException::class)
  fun create(value: ParserConfigCreate): Long {
    val rowId = dao.create(value)
    Validation.dbCreate(rowId, entityName)
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(value: ParserConfigUpdate): Boolean {
    val row = read(value.id) ?: return false
    val count = dao.update(value.merge(row))
    Validation.dbUpdate(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun delete(value: ParserConfigDelete): Boolean {
    val count = dao.delete(value)
    Validation.dbDelete(count, entityName)
    return true
  }
}