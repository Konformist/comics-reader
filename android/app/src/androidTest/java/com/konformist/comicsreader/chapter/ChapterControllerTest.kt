package com.konformist.comicsreader.chapter

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.chapter.ChapterController
import com.konformist.comicsreader.data.chapter.ChapterCreate
import com.konformist.comicsreader.data.chapter.ChapterDelete
import com.konformist.comicsreader.data.chapter.ChapterUpdate
import com.konformist.comicsreader.data.chapterpage.ChapterPageController
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.data.appfile.AppFileController
import com.konformist.comicsreader.data.chapter.ChapterUpdateComic
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ChapterControllerTest {
  private lateinit var db: AppDatabase
  private lateinit var controller: ChapterController

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    controller = ChapterController(
      db.chapterDao(),
      ChapterPageController(
        db.chapterPageDao(),
        AppFileController(db.appFileDao())
      )
    )
  }

  @Test
  @Throws(Exception::class)
  fun testCreate() {
    val create = ChapterCreate(comicId = 1, name = "New row")
    val rowId = controller.create(create)

    val row = controller.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertEquals("New row", row?.name)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() {
    val create = ChapterCreate(comicId = 1, name = "New row")
    val rowId = controller.create(create)
    val rowCreated = controller.read(rowId)

    val update = ChapterUpdate(
      id = rowId,
      name = "New row 2",
    )
    Thread.sleep(1000)
    controller.update(update)

    val rowUpdated = controller.read(rowId)
    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
    Assert.assertEquals("New row 2", rowUpdated?.name)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdateComic() {
    val create = ChapterCreate(comicId = 1, name = "New row")
    val rowId = controller.create(create)
    val rowCreated = controller.read(rowId)

    val update = ChapterUpdateComic(
      id = rowId,
      comicId = 2,
    )
    Thread.sleep(1000)
    controller.updateComic(update)

    val rowUpdated = controller.read(rowId)
    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
    Assert.assertEquals(2L, rowUpdated?.comicId)
  }

  @Test
  @Throws(Exception::class)
  fun testDelete() {
    val create = ChapterCreate(comicId = 1, name = "New row")
    val rowId = controller.create(create)
    val delete = ChapterDelete(id = rowId)
    controller.delete(delete)
    val row = controller.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}