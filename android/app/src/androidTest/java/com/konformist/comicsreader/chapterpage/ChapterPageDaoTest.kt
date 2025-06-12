package com.konformist.comicsreader.chapterpage

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.data.chapterpage.ChapterPageDao
import com.konformist.comicsreader.data.chapterpage.ChapterPageDelete
import com.konformist.comicsreader.data.chapterpage.ChapterPageUpdate
import com.konformist.comicsreader.data.comiccover.ComicCoverCreate
import com.konformist.comicsreader.data.comiccover.ComicCoverDao
import com.konformist.comicsreader.data.comiccover.ComicCoverDelete
import com.konformist.comicsreader.data.comiccover.ComicCoverUpdate
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

class ChapterPageDaoTest {
  private lateinit var db: AppDatabase
  private lateinit var dao: ChapterPageDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    dao = db.chapterPageDao()
  }

  @Test
  @Throws(Exception::class)
  fun testCreate() {
    val create = ChapterPageCreate(
      chapterId = 1,
      fileId = 2,
      isRead = true,
      fromUrl = "url",
    )
    val rowId = dao.create(create)

    val row = dao.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertNotNull(row?.cdate)
    Assert.assertNotNull(row?.mdate)
    Assert.assertNotEquals("", row?.cdate)
    Assert.assertNotEquals("", row?.mdate)
    Assert.assertEquals(1L, row?.chapterId)
    Assert.assertEquals(2L, row?.fileId)
    Assert.assertEquals(true, row?.isRead)
    Assert.assertEquals("url", row?.fromUrl)
  }

  @Test
  @Throws(Exception::class)
  fun testCreateNullable() {
    val create = ChapterPageCreate(chapterId = 1)
    val rowId = dao.create(create)

    val row = dao.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertEquals(0L, row?.fileId)
    Assert.assertEquals(false, row?.isRead)
    Assert.assertEquals("", row?.fromUrl)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() {
    val create = ChapterPageCreate(chapterId = 1, isRead = true)
    val rowId = dao.create(create)
    val rowCreated = dao.read(rowId)

    Thread.sleep(1000)
    val update = ChapterPageUpdate(
      id = rowId,
      mdate = DatesUtils.Companion.nowFormatted(),
      fromUrl = "url",
      isRead = false,
    )
    dao.update(update)

    val rowUpdated = dao.read(rowId)
    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
    Assert.assertEquals("url", rowUpdated?.fromUrl)
    Assert.assertEquals(false, rowUpdated?.isRead)
  }

  @Test
  @Throws(Exception::class)
  fun testDelete() {
    val create = ChapterPageCreate(chapterId = 1)
    val rowId = dao.create(create)
    val delete = ChapterPageDelete(id = rowId)
    dao.delete(delete)
    val row = dao.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}