package com.konformist.comicsreader.data.tag

import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.webapi.Validation

class TagController(private val dao: TagDao) {
  private val entityName = "Tag"
  fun readAll(): List<Tag> {
    return dao.readAll()
  }

  fun read(id: Long): Tag? {
    return dao.read(id)
  }

  @Throws(DatabaseException::class)
  fun create(value: TagCreate): Long {
    val rowId = dao.create(value)
    Validation.dbCreate(rowId, entityName)
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(value: TagUpdate): Boolean {
    val row = read(value.id) ?: return false
    val count = dao.update(value.merge(row))
    Validation.dbUpdate(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun delete(value: TagDelete): Boolean {
    val count = dao.delete(value)
    Validation.dbDelete(count, entityName)
    return true
  }
}