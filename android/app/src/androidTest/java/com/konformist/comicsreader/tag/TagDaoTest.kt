package com.konformist.comicsreader.tag

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.tag.TagCreate
import com.konformist.comicsreader.data.tag.TagDao
import com.konformist.comicsreader.data.tag.TagDelete
import com.konformist.comicsreader.data.tag.TagUpdate
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.utils.DatesUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TagDaoTest {
  private lateinit var db: AppDatabase
  private lateinit var dao: TagDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    dao = db.tagDao()
  }

  @Test
  @Throws(Exception::class)
  fun testCreate() {
    val create = TagCreate(name = "New row")
    val rowId = dao.create(create)

    val row = dao.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertNotNull(row?.cdate)
    Assert.assertNotNull(row?.mdate)
    Assert.assertNotEquals("", row?.cdate)
    Assert.assertNotEquals("", row?.mdate)
    Assert.assertEquals("New row", row?.name)
  }

  @Test
  @Throws(Exception::class)
  fun testCreateNullable() {
    val create = TagCreate()
    val rowId = dao.create(create)

    val row = dao.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertEquals("", row?.name)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() {
    val create = TagCreate(name = "New row")
    val rowId = dao.create(create)
    val rowCreated = dao.read(rowId)

    Thread.sleep(1000)
    val update = TagUpdate(
      id = rowId,
      mdate = DatesUtils.Companion.nowFormatted(),
      name = "New row 2",
    )
    dao.update(update)

    val rowUpdated = dao.read(rowId)
    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
    Assert.assertEquals("New row 2", rowUpdated?.name)
  }

  @Test
  @Throws(Exception::class)
  fun testDelete() {
    val create = TagCreate(name = "New row")
    val rowId = dao.create(create)
    val delete = TagDelete(id = rowId)
    dao.delete(delete)
    val row = dao.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}