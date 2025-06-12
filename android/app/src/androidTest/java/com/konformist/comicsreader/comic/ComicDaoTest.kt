package com.konformist.comicsreader.comic

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.comic.ComicCreate
import com.konformist.comicsreader.data.comic.ComicDao
import com.konformist.comicsreader.data.comic.ComicDelete
import com.konformist.comicsreader.data.comic.ComicUpdate
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
import kotlin.String

class ComicDaoTest {
  private lateinit var db: AppDatabase
  private lateinit var dao: ComicDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    dao = db.comicDao()
  }

  @Test
  @Throws(Exception::class)
  fun testCreate() {
    val create = ComicCreate(
      name = "New row",
      annotation = "annotation",
      parserId = 1,
      languageId = 2,
      fromUrl = "fromUrl",
      tags = listOf(1,2),
      authors = listOf(3, 4),
    )
    val rowId = dao.create(create)

    val row = dao.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertNotNull(row?.cdate)
    Assert.assertNotNull(row?.mdate)
    Assert.assertNotEquals("", row?.cdate)
    Assert.assertNotEquals("", row?.mdate)
    Assert.assertEquals("New row", row?.name)
    Assert.assertEquals("annotation", row?.annotation)
    Assert.assertEquals(1L, row?.parserId)
    Assert.assertEquals(2L, row?.languageId)
    Assert.assertEquals("fromUrl", row?.fromUrl)
    Assert.assertEquals(listOf(1L,2L), row?.tags)
    Assert.assertEquals(listOf(3L,4L), row?.authors)
  }

  @Test
  @Throws(Exception::class)
  fun testCreateNullable() {
    val create = ComicCreate()
    val rowId = dao.create(create)

    val row = dao.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertEquals("", row?.name)
    Assert.assertEquals("", row?.annotation)
    Assert.assertEquals(0L, row?.parserId)
    Assert.assertEquals(0L, row?.languageId)
    Assert.assertEquals("", row?.fromUrl)
    Assert.assertEquals(listOf<Long>(), row?.tags)
    Assert.assertEquals(listOf<Long>(), row?.authors)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() {
    val create = ComicCreate(
      name = "New row",
      annotation = "annotation",
      parserId = 1,
      languageId = 2,
      fromUrl = "fromUrl",
      tags = listOf(1,2),
      authors = listOf(3, 4),
    )
    val rowId = dao.create(create)
    val rowCreated = dao.read(rowId)

    Thread.sleep(1000)
    val update = ComicUpdate(
      id = rowId,
      name = "New row 2",
      mdate = DatesUtils.Companion.nowFormatted(),
      annotation = "annotation 2",
      parserId = 2,
      languageId = 3,
      fromUrl = "fromUrl 2",
      tags = listOf(2,3),
      authors = listOf(4, 5),
    )
    dao.update(update)

    val rowUpdated = dao.read(rowId)
    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
    Assert.assertEquals("New row 2", rowUpdated?.name)
    Assert.assertEquals("annotation 2", rowUpdated?.annotation)
    Assert.assertEquals(2L, rowUpdated?.parserId)
    Assert.assertEquals(3L, rowUpdated?.languageId)
    Assert.assertEquals("fromUrl 2", rowUpdated?.fromUrl)
    Assert.assertEquals(listOf(2L,3L), rowUpdated?.tags)
    Assert.assertEquals(listOf(4L,5L), rowUpdated?.authors)
  }

  @Test
  @Throws(Exception::class)
  fun testDelete() {
    val create = ComicCreate(name = "New row")
    val rowId = dao.create(create)
    val delete = ComicDelete(id = rowId)
    dao.delete(delete)
    val row = dao.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}