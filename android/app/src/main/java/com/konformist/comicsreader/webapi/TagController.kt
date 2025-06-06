package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.tag.Tag
import com.konformist.comicsreader.db.tag.TagCreate
import com.konformist.comicsreader.db.tag.TagDao
import com.konformist.comicsreader.db.tag.TagDelete
import com.konformist.comicsreader.db.tag.TagUpdate
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.DatesUtils

class TagController(private val tagDao: TagDao) {
  fun readAll(): List<Tag> {
    return tagDao.readAll()
  }

  fun read(id: Long): Tag {
    return tagDao.read(id)
  }

  @Throws(DatabaseException::class)
  fun create(tag: TagCreate): Long {
    val rowId = tagDao.create(tag)
    Validation.dbCreate(rowId, "Tag")
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(tag: TagUpdate): Boolean {
    val count = tagDao.update(
      TagUpdate(
        id = tag.id,
        mdate = DatesUtils.nowFormatted(),
        name = tag.name,
      )
    )
    Validation.dbUpdate(count, "Tag")
    return true
  }

  @Throws(DatabaseException::class)
  fun delete(tag: TagDelete): Boolean {
    val count = tagDao.delete(TagDelete(id = tag.id))
    Validation.dbDelete(count, "Tag")
    return true
  }
}