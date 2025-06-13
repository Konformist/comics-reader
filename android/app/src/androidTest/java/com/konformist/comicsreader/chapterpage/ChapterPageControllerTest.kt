package com.konformist.comicsreader.chapterpage

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.chapterpage.ChapterPageController
import com.konformist.comicsreader.data.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.data.chapterpage.ChapterPageDelete
import com.konformist.comicsreader.data.chapterpage.ChapterPageUpdate
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.data.appfile.AppFileController
import com.konformist.comicsreader.utils.DatesUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ChapterPageControllerTest {
  private lateinit var db: AppDatabase
  private lateinit var controller: ChapterPageController

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    controller = ChapterPageController(
      db.chapterPageDao(),
      AppFileController(db.appFileDao()),
    )
  }

  @Test
  @Throws(Exception::class)
  fun testCreate() {
    val create = ChapterPageCreate(
      chapterId = 1,
      fileId = 2,
      fromUrl = "url",
      isRead = true,
    )
    val rowId = controller.create(create)

    val row = controller.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertEquals(1L, row?.chapterId)
    Assert.assertEquals(2L, row?.fileId)
    Assert.assertEquals("url", row?.fromUrl)
    Assert.assertEquals(true, row?.isRead)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() {
    val create = ChapterPageCreate(
      chapterId = 1,
      fileId = 2,
      fromUrl = "url",
      isRead = true,
    )
    val rowId = controller.create(create)
    val rowCreated = controller.read(rowId)

    val mdate = DatesUtils.nowFormatted()
    val update = ChapterPageUpdate(
      id = rowId,
      mdate = mdate,
      fromUrl = "url",
      isRead = false,
    )
    Thread.sleep(1000)
    controller.update(update)

    val rowUpdated = controller.read(rowId)
    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotEquals(mdate, rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
    Assert.assertEquals(1L, rowUpdated?.chapterId)
    Assert.assertEquals(2L, rowUpdated?.fileId)
    Assert.assertEquals("url", rowUpdated?.fromUrl)
    Assert.assertEquals(false, rowUpdated?.isRead)
  }

  @Test
  @Throws(Exception::class)
  fun testDelete() {
    val create = ChapterPageCreate(chapterId = 1L)
    val rowId = controller.create(create)
    controller.delete(ChapterPageDelete(id = rowId))
    val row = controller.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}