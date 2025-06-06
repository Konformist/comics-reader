package com.konformist.comicsreader.webapi.controllers

import com.konformist.comicsreader.db.author.Author
import com.konformist.comicsreader.db.author.AuthorCreate
import com.konformist.comicsreader.db.author.AuthorDao
import com.konformist.comicsreader.db.author.AuthorDelete
import com.konformist.comicsreader.db.author.AuthorUpdate
import com.konformist.comicsreader.db.language.Language
import com.konformist.comicsreader.db.language.LanguageCreate
import com.konformist.comicsreader.db.language.LanguageDao
import com.konformist.comicsreader.db.language.LanguageDelete
import com.konformist.comicsreader.db.language.LanguageUpdate
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.webapi.Validation

class LanguageController(private val dao: LanguageDao) {
  private val entityName = "Language"
  fun readAll(): List<Language> {
    return dao.readAll()
  }

  fun read(id: Long): Language {
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
    value.mdate = DatesUtils.nowFormatted()
    val count = dao.update(value)
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