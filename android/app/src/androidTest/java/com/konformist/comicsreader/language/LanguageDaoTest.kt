package com.konformist.comicsreader.language

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.language.LanguageCreate
import com.konformist.comicsreader.data.language.LanguageDao
import com.konformist.comicsreader.data.language.LanguageDelete
import com.konformist.comicsreader.data.language.LanguageUpdate
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.utils.DatesUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LanguageDaoTest {
  private lateinit var db: AppDatabase
  private lateinit var dao: LanguageDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    dao = db.languageDao()
  }

  @Test
  @Throws(Exception::class)
  fun testCreate() {
    val create = LanguageCreate(name = "New row")
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
    val create = LanguageCreate()
    val rowId = dao.create(create)

    val row = dao.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertEquals("", row?.name)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() {
    val create = LanguageCreate(name = "New row")
    val rowId = dao.create(create)
    val rowCreated = dao.read(rowId)

    Thread.sleep(1000)
    val update = LanguageUpdate(
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
    val create = LanguageCreate(name = "New row")
    val rowId = dao.create(create)
    val delete = LanguageDelete(id = rowId)
    dao.delete(delete)
    val row = dao.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}