package com.konformist.comicsreader.author

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.author.AuthorCreate
import com.konformist.comicsreader.data.author.AuthorDao
import com.konformist.comicsreader.data.author.AuthorDelete
import com.konformist.comicsreader.data.author.AuthorUpdate
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.utils.DatesUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AuthorDaoTest {
  private lateinit var db: AppDatabase
  private lateinit var dao: AuthorDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    dao = db.authorDao()
  }

  @Test
  @Throws(Exception::class)
  fun testCreate() {
    val create = AuthorCreate(name = "New author")
    val rowId = dao.create(create)

    val row = dao.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertNotNull(row?.cdate)
    Assert.assertNotNull(row?.mdate)
    Assert.assertNotEquals("", row?.cdate)
    Assert.assertNotEquals("", row?.mdate)
    Assert.assertEquals("New author", row?.name)
  }

  @Test
  @Throws(Exception::class)
  fun testCreateNullable() {
    val create = AuthorCreate()
    val rowId = dao.create(create)

    val row = dao.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertEquals("", row?.name)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() {
    val create = AuthorCreate(name = "New author")
    val rowId = dao.create(create)
    val rowCreated = dao.read(rowId)

    Thread.sleep(1000)
    val update = AuthorUpdate(
      id = rowId,
      mdate = DatesUtils.Companion.nowFormatted(),
      name = "New author 2",
    )
    dao.update(update)

    val rowUpdated = dao.read(rowId)
    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
    Assert.assertEquals("New author 2", rowUpdated?.name)
  }

  @Test
  @Throws(Exception::class)
  fun testDelete() {
    val create = AuthorCreate(name = "New author")
    val rowId = dao.create(create)
    val delete = AuthorDelete(id = rowId)
    dao.delete(delete)
    val row = dao.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}