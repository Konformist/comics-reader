package com.konformist.comicsreader.appfile

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.appfile.AppFileCreate
import com.konformist.comicsreader.data.appfile.AppFileDao
import com.konformist.comicsreader.data.appfile.AppFileDelete
import com.konformist.comicsreader.data.appfile.AppFileUpdate
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.utils.DatesUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AppFileDaoTest {
  private lateinit var db: AppDatabase
  private lateinit var dao: AppFileDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    dao = db.appFileDao()
  }

  @Test
  @Throws(Exception::class)
  fun testCreate() {
    val create = AppFileCreate(
      name = "New row",
      mime = "image/jpeg",
      size = 8435,
      path = "path/1.jpg",
    )
    val rowId = dao.create(create)

    val row = dao.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertNotNull(row?.cdate)
    Assert.assertNotNull(row?.mdate)
    Assert.assertNotEquals("", row?.cdate)
    Assert.assertNotEquals("", row?.mdate)
    Assert.assertEquals("New row", row?.name)
    Assert.assertEquals("image/jpeg", row?.mime)
    Assert.assertEquals(8435L, row?.size)
    Assert.assertEquals("path/1.jpg", row?.path)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() {
    val create = AppFileCreate(
      name = "New row",
      mime = "image/jpeg",
      size = 8435,
      path = "path/1.jpg",
    )
    val rowId = dao.create(create)
    val rowCreated = dao.read(rowId)

    Thread.sleep(1000)
    val update = AppFileUpdate(
      id = rowId,
      mdate = DatesUtils.nowFormatted(),
      name = "New row 2",
      mime = "image/webp",
      size = 1000,
      path = "path/2.webp",
    )
    dao.update(update)

    val rowUpdated = dao.read(rowId)
    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotNull(rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
    Assert.assertEquals("New row 2", rowUpdated?.name)
    Assert.assertEquals("image/webp", rowUpdated?.mime)
    Assert.assertEquals(1000L, rowUpdated?.size)
    Assert.assertEquals("path/2.webp", rowUpdated?.path)
  }

  @Test
  @Throws(Exception::class)
  fun testDelete() {
    val create = AppFileCreate(
      name = "New row",
      mime = "image/jpeg",
      size = 8435,
      path = "path/1.jpg",
    )
    val rowId = dao.create(create)
    val delete = AppFileDelete(id = rowId)
    dao.delete(delete)
    val row = dao.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}