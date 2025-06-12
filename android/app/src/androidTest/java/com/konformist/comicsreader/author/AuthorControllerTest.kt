package com.konformist.comicsreader.author

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.author.AuthorController
import com.konformist.comicsreader.data.author.AuthorCreate
import com.konformist.comicsreader.data.author.AuthorDelete
import com.konformist.comicsreader.data.author.AuthorUpdate
import com.konformist.comicsreader.db.AppDatabase
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AuthorControllerTest {
  private lateinit var db: AppDatabase
  private lateinit var controller: AuthorController

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    controller = AuthorController(db.authorDao())
  }

  @Test
  @Throws(Exception::class)
  fun testCreate() {
    val create = AuthorCreate(name = "New row")
    val rowId = controller.create(create)

    val row = controller.read(rowId)
    Assert.assertNotNull(row)
    Assert.assertEquals("New row", row?.name)
  }

  @Test
  @Throws(Exception::class)
  fun testUpdate() {
    val create = AuthorCreate(name = "New row")
    val rowId = controller.create(create)
    val rowCreated = controller.read(rowId)

    val update = AuthorUpdate(
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
  fun testDelete() {
    val create = AuthorCreate(name = "New row")
    val rowId = controller.create(create)
    val delete = AuthorDelete(id = rowId)
    controller.delete(delete)
    val row = controller.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}