package com.konformist.comicsreader.comicover

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
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

class ComicCoverDaoTest {
  private lateinit var db: AppDatabase
  private lateinit var dao: ComicCoverDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    dao = db.comicCoverDao()
  }

  @Test
  @Throws(Exception::class)
  fun testCreate() {
    val create = ComicCoverCreate(comicId = 1, fileId = 2, fromUrl = "url")
    val rowId = dao.create(create)

    val row = dao.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertNotNull(row?.cdate)
    Assert.assertNotNull(row?.mdate)
    Assert.assertNotEquals("", row?.cdate)
    Assert.assertNotEquals("", row?.mdate)
    Assert.assertEquals(1L, row?.comicId)
    Assert.assertEquals(2L, row?.fileId)
    Assert.assertEquals("url", row?.fromUrl)
  }

  @Test
  @Throws(Exception::class)
  fun testCreateNullable() {
    val create = ComicCoverCreate(comicId = 1)
    val rowId = dao.create(create)

    val row = dao.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertEquals(0L, row?.fileId)
    Assert.assertEquals("", row?.fromUrl)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() {
    val create = ComicCoverCreate(comicId = 1)
    val rowId = dao.create(create)
    val rowCreated = dao.read(rowId)

    Thread.sleep(1000)
    val update = ComicCoverUpdate(
      id = rowId,
      mdate = DatesUtils.Companion.nowFormatted(),
      fromUrl = "url"
    )
    dao.update(update)

    val rowUpdated = dao.read(rowId)
    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
    Assert.assertEquals("url", rowUpdated?.fromUrl)
  }

  @Test
  @Throws(Exception::class)
  fun testDelete() {
    val create = ComicCoverCreate(comicId = 1)
    val rowId = dao.create(create)
    val delete = ComicCoverDelete(id = rowId)
    dao.delete(delete)
    val row = dao.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}