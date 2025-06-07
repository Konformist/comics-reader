package com.konformist.comicsreader.webapi.controllers

import com.konformist.comicsreader.db.tag.Tag
import com.konformist.comicsreader.db.tag.TagCreate
import com.konformist.comicsreader.db.tag.TagDao
import com.konformist.comicsreader.db.tag.TagDelete
import com.konformist.comicsreader.db.tag.TagUpdate
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.webapi.Validation

class TagController(private val dao: TagDao) {
  private val entityName = "Tag"
  fun readAll(): List<Tag> {
    return dao.readAll()
  }

  fun read(id: Long): Tag {
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
    value.mdate = DatesUtils.nowFormatted()
    val count = dao.update(value)
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
