package com.konformist.comicsreader.data.author

import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.webapi.Validation

class AuthorController(private val dao: AuthorDao) {
  private val entityName = "Author"
  fun readAll(): List<Author> {
    return dao.readAll()
  }

  fun read(id: Long): Author? {
    return dao.read(id)
  }

  @Throws(DatabaseException::class)
  fun create(value: AuthorCreate): Long {
    val rowId = dao.create(value)
    Validation.dbCreate(rowId, entityName)
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(value: AuthorUpdate): Boolean {
    val row = read(value.id) ?: return false
    val count = dao.update(value.merge(row))
    Validation.dbUpdate(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun delete(value: AuthorDelete): Boolean {
    val count = dao.delete(value)
    Validation.dbDelete(count, entityName)
    return true
  }
}