package com.konformist.comicsreader.comicover

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.comiccover.ComicCoverController
import com.konformist.comicsreader.data.comiccover.ComicCoverCreate
import com.konformist.comicsreader.data.comiccover.ComicCoverDelete
import com.konformist.comicsreader.data.comiccover.ComicCoverUpdate
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.data.appfile.AppFileController
import com.konformist.comicsreader.utils.DatesUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ComicCoverControllerTest {
  private lateinit var db: AppDatabase
  private lateinit var controller: ComicCoverController

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    controller = ComicCoverController(
      db.comicCoverDao(),
      AppFileController(db.appFileDao()),
    )
  }

  @Test
  @Throws(Exception::class)
  fun testCreate() {
    val create = ComicCoverCreate(
      comicId = 1,
      fileId = 2,
      fromUrl = "url",
    )
    val rowId = controller.create(create)

    val row = controller.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertEquals(1L, row?.comicId)
    Assert.assertEquals(2L, row?.fileId)
    Assert.assertEquals("url", row?.fromUrl)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() {
    val create = ComicCoverCreate(
      comicId = 1,
      fileId = 2,
      fromUrl = "url",
    )
    val rowId = controller.create(create)
    val rowCreated = controller.read(rowId)

    val mdate = DatesUtils.nowFormatted()
    val update = ComicCoverUpdate(
      id = rowId,
      mdate = mdate,
      fromUrl = "url",
    )
    Thread.sleep(1000)
    controller.update(update)

    val rowUpdated = controller.read(rowId)
    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotEquals(mdate, rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
    Assert.assertEquals(1L, rowUpdated?.comicId)
    Assert.assertEquals(2L, rowUpdated?.fileId)
    Assert.assertEquals("url", rowUpdated?.fromUrl)
  }

  @Test
  @Throws(Exception::class)
  fun testDelete() {
    val create = ComicCoverCreate(comicId = 1L)
    val rowId = controller.create(create)
    controller.delete(ComicCoverDelete(id = rowId))
    val row = controller.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}