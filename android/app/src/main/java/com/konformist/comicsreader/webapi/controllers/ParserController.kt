package com.konformist.comicsreader.webapi.controllers

import com.konformist.comicsreader.db.parser.Parser
import com.konformist.comicsreader.db.parser.ParserCreate
import com.konformist.comicsreader.db.parser.ParserDao
import com.konformist.comicsreader.db.parser.ParserDelete
import com.konformist.comicsreader.db.parser.ParserUpdate
import com.konformist.comicsreader.db.tag.Tag
import com.konformist.comicsreader.db.tag.TagCreate
import com.konformist.comicsreader.db.tag.TagDao
import com.konformist.comicsreader.db.tag.TagDelete
import com.konformist.comicsreader.db.tag.TagUpdate
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.webapi.Validation

class ParserController(private val dao: ParserDao) {
  private val entityName = "Parser"
  fun readAll(): List<Parser> {
    return dao.readAll()
  }

  fun read(id: Long): Parser {
    return dao.read(id)
  }

  @Throws(DatabaseException::class)
  fun create(value: ParserCreate): Long {
    val rowId = dao.create(value)
    Validation.dbCreate(rowId, entityName)
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(value: ParserUpdate): Boolean {
    value.mdate = DatesUtils.nowFormatted()
    val count = dao.update(value)
    Validation.dbUpdate(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun delete(value: ParserDelete): Boolean {
    val count = dao.delete(value)
    Validation.dbDelete(count, entityName)
    return true
  }
}
