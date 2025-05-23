package com.konformist.comicsreader

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.db.tag.TagCreate
import com.konformist.comicsreader.db.tag.TagDao
import com.konformist.comicsreader.db.tag.TagDelete
import com.konformist.comicsreader.db.tag.TagUpdate
import com.konformist.comicsreader.utils.Dates
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

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
    val rowId = tagDao.create(TagCreate(name = "New tag"))
    Assert.assertEquals(1, rowId)

    val tagSaved = tagDao.read(rowId)
    Assert.assertEquals(TagCreate(name = "New tag"), TagCreate(name = tagSaved.name))
  }

  @Test
  @Throws(Exception::class)
  fun update() {
    val mdate = Dates.dateTimeFormatted(LocalDateTime.now())
    val rowId = tagDao.create(TagCreate(name = "New tag 2"))
    tagDao.update(TagUpdate(id = 1, mdate = mdate, name = "New tag 2"))

    val tagSaved = tagDao.read(rowId)
    Assert.assertEquals(TagUpdate(id = 1, mdate = mdate, name = "New tag 2"), TagUpdate(id = tagSaved.id, mdate = tagSaved.mdate, name = tagSaved.name))
  }

  @Test
  @Throws(Exception::class)
  fun delete() {
    val rowId = tagDao.create(TagCreate(name = "New tag"))
    val rows = tagDao.delete(TagDelete(id = rowId))

    Assert.assertEquals(1, rows)
  }
}