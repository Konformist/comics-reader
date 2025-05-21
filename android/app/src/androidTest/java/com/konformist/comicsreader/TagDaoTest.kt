package com.konformist.comicsreader

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.db.tag.Tag
import com.konformist.comicsreader.db.tag.TagDao
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TagDaoTest {
  private lateinit var tagDao: TagDao
  private lateinit var db: AppDatabase

  @Before
  fun createDb() {
    db = Room
      .databaseBuilder(
        ApplicationProvider.getApplicationContext(),
        AppDatabase::class.java,
        "app-database"
      )
      .build()
    tagDao = db.tagDao()
  }

  @After
  fun closeDb() {
    db.close()
  }

  @Test
  @Throws(Exception::class)
  fun create() {
    val tag = Tag(id = null, cdate = null, mdate = null, name = "New tag")

    val rowId = tagDao.create(tag)

    Assert.assertEquals(1, rowId)

    val tagSaved = tagDao.read(rowId)
    val tagExpect = Tag(id = 1, cdate = null, mdate = null, name = "New tag")

    Assert.assertEquals(tagExpect, tagSaved)
  }

  @Test
  @Throws(Exception::class)
  fun update() {
    val tag = Tag(id = null, cdate = null, mdate = null, name = "New tag 2")
    val rowId = tagDao.create(tag)

    tagDao.update(tag)

    val tagSaved = tagDao.read(rowId)
    val tagExpect = Tag(id = 1, cdate = null, mdate = null, name = "New tag 2")

    Assert.assertEquals(tagExpect, tagSaved)
  }

  @Test
  @Throws(Exception::class)
  fun delete() {
    val tag = Tag(id = null, cdate = null, mdate = null, name = "New tag")
    val rowId = tagDao.create(tag)
    val tagExpect = Tag(id = rowId, cdate = null, mdate = null, name = "")

    val rows = tagDao.delete(tagExpect)

    Assert.assertEquals(1, rows)
  }
}