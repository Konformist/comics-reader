package com.konformist.comicsreader.webapi.controllers

import com.konformist.comicsreader.db.author.Author
import com.konformist.comicsreader.db.author.AuthorCreate
import com.konformist.comicsreader.db.author.AuthorDao
import com.konformist.comicsreader.db.author.AuthorDelete
import com.konformist.comicsreader.db.author.AuthorUpdate
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.webapi.Validation

class AuthorController(private val dao: AuthorDao) {
  private val entityName = "Author"
  fun readAll(): List<Author> {
    return dao.readAll()
  }

  fun read(id: Long): Author {
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
    value.mdate = DatesUtils.nowFormatted()
    val count = dao.update(value)
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