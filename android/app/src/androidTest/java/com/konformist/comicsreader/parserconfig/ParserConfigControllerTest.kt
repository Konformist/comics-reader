package com.konformist.comicsreader.parserconfig

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.parserconfig.ParserConfigController
import com.konformist.comicsreader.data.parserconfig.ParserConfigCreate
import com.konformist.comicsreader.data.parserconfig.ParserConfigDelete
import com.konformist.comicsreader.data.parserconfig.ParserConfigUpdate
import com.konformist.comicsreader.db.AppDatabase
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ParserConfigControllerTest {
  private lateinit var db: AppDatabase
  private lateinit var controller: ParserConfigController

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    controller = ParserConfigController(db.parserDao())
  }

  @Test
  fun testCreate() {
    // Создаем объект для вставки
    val create = ParserConfigCreate(
      name = "TestFile",
      siteUrl = "siteUrl",
      titleCSS = "titleCSS",
      annotationCSS = "annotationCSS",
      coverCSS = "coverCSS",
      authorsCSS = "authorsCSS",
      authorsTextCSS = "authorsTextCSS",
      languageCSS = "languageCSS",
      tagsCSS = "tagsCSS",
      tagsTextCSS = "tagsTextCSS",
      chaptersCSS = "chaptersCSS",
      chaptersTitleCSS = "chaptersTitleCSS",
      pagesTemplateUrl = "pagesTemplateUrl",
      pagesCSS = "pagesCSS",
      pagesImageCSS = "pagesImageCSS",
    )

    val rowId = controller.create(create)
    val row = controller.read(rowId)

    Assert.assertEquals("TestFile", row?.name)
    Assert.assertEquals("siteUrl", row?.siteUrl)
    Assert.assertEquals("titleCSS", row?.titleCSS)
    Assert.assertEquals("annotationCSS", row?.annotationCSS)
    Assert.assertEquals("coverCSS", row?.coverCSS)
    Assert.assertEquals("authorsCSS", row?.authorsCSS)
    Assert.assertEquals("authorsTextCSS", row?.authorsTextCSS)
    Assert.assertEquals("languageCSS", row?.languageCSS)
    Assert.assertEquals("tagsCSS", row?.tagsCSS)
    Assert.assertEquals("tagsTextCSS", row?.tagsTextCSS)
    Assert.assertEquals("chaptersCSS", row?.chaptersCSS)
    Assert.assertEquals("chaptersTitleCSS", row?.chaptersTitleCSS)
    Assert.assertEquals("pagesTemplateUrl", row?.pagesTemplateUrl)
    Assert.assertEquals("pagesCSS", row?.pagesCSS)
    Assert.assertEquals("pagesImageCSS", row?.pagesImageCSS)
  }

  @Test
  fun testUpdate() {
    val create = ParserConfigCreate(
      name = "TestFile",
      siteUrl = "siteUrl",
      titleCSS = "titleCSS",
      annotationCSS = "annotationCSS",
      coverCSS = "coverCSS",
      authorsCSS = "authorsCSS",
      authorsTextCSS = "authorsTextCSS",
      languageCSS = "languageCSS",
      tagsCSS = "tagsCSS",
      tagsTextCSS = "tagsTextCSS",
      chaptersCSS = "chaptersCSS",
      chaptersTitleCSS = "chaptersTitleCSS",
      pagesTemplateUrl = "pagesTemplateUrl",
      pagesCSS = "pagesCSS",
      pagesImageCSS = "pagesImageCSS",
    )

    val rowId = controller.create(create)
    val rowCreated = controller.read(rowId)

    val update = ParserConfigUpdate(
      id = rowId,
      mdate = rowCreated?.mdate,
      name = "TestFile 2",
      siteUrl = "siteUrl 2",
      titleCSS = "titleCSS 2",
      annotationCSS = "annotationCSS 2",
      coverCSS = "coverCSS 2",
      authorsCSS = "authorsCSS 2",
      authorsTextCSS = "authorsTextCSS 2",
      languageCSS = "languageCSS 2",
      tagsCSS = "tagsCSS 2",
      tagsTextCSS = "tagsTextCSS 2",
      chaptersCSS = "chaptersCSS 2",
      chaptersTitleCSS = "chaptersTitleCSS 2",
      pagesTemplateUrl = "pagesTemplateUrl 2",
      pagesCSS = "pagesCSS 2",
      pagesImageCSS = "pagesImageCSS 2",
    )

    Thread.sleep(1000)
    controller.update(update)
    val rowUpdated = controller.read(rowId)

    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
    Assert.assertEquals("TestFile 2", rowUpdated?.name)
    Assert.assertEquals("siteUrl 2", rowUpdated?.siteUrl)
    Assert.assertEquals("titleCSS 2", rowUpdated?.titleCSS)
    Assert.assertEquals("annotationCSS 2", rowUpdated?.annotationCSS)
    Assert.assertEquals("coverCSS 2", rowUpdated?.coverCSS)
    Assert.assertEquals("authorsCSS 2", rowUpdated?.authorsCSS)
    Assert.assertEquals("authorsTextCSS 2", rowUpdated?.authorsTextCSS)
    Assert.assertEquals("languageCSS 2", rowUpdated?.languageCSS)
    Assert.assertEquals("tagsCSS 2", rowUpdated?.tagsCSS)
    Assert.assertEquals("tagsTextCSS 2", rowUpdated?.tagsTextCSS)
    Assert.assertEquals("chaptersCSS 2", rowUpdated?.chaptersCSS)
    Assert.assertEquals("chaptersTitleCSS 2", rowUpdated?.chaptersTitleCSS)
    Assert.assertEquals("pagesTemplateUrl 2", rowUpdated?.pagesTemplateUrl)
    Assert.assertEquals("pagesCSS 2", rowUpdated?.pagesCSS)
    Assert.assertEquals("pagesImageCSS 2", rowUpdated?.pagesImageCSS)
  }

  @Test
  fun testUpdateNullable() {
    // Создаем объект для вставки
    val create = ParserConfigCreate(
      name = "TestFile",
      siteUrl = "siteUrl",
      titleCSS = "titleCSS",
      annotationCSS = "annotationCSS",
      coverCSS = "coverCSS",
      authorsCSS = "authorsCSS",
      authorsTextCSS = "authorsTextCSS",
      languageCSS = "languageCSS",
      tagsCSS = "tagsCSS",
      tagsTextCSS = "tagsTextCSS",
      chaptersCSS = "chaptersCSS",
      chaptersTitleCSS = "chaptersTitleCSS",
      pagesTemplateUrl = "pagesTemplateUrl",
      pagesCSS = "pagesCSS",
      pagesImageCSS = "pagesImageCSS",
    )

    val rowId = controller.create(create)
    val rowCreated = controller.read(rowId)

    val update = ParserConfigUpdate(
      id = rowId,
      name = "TestFile 2",
    )

    Thread.sleep(1000)
    controller.update(update)

    // Получаем запись из базы данных по ID
    val rowUpdated = controller.read(rowId)

    // Проверяем, что запись была сохранена корректно
    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
    Assert.assertEquals("TestFile 2", rowUpdated?.name)
    Assert.assertEquals("siteUrl", rowUpdated?.siteUrl)
    Assert.assertEquals("titleCSS", rowUpdated?.titleCSS)
    Assert.assertEquals("annotationCSS", rowUpdated?.annotationCSS)
    Assert.assertEquals("coverCSS", rowUpdated?.coverCSS)
    Assert.assertEquals("authorsCSS", rowUpdated?.authorsCSS)
    Assert.assertEquals("authorsTextCSS", rowUpdated?.authorsTextCSS)
    Assert.assertEquals("languageCSS", rowUpdated?.languageCSS)
    Assert.assertEquals("tagsCSS", rowUpdated?.tagsCSS)
    Assert.assertEquals("tagsTextCSS", rowUpdated?.tagsTextCSS)
    Assert.assertEquals("chaptersCSS", rowUpdated?.chaptersCSS)
    Assert.assertEquals("chaptersTitleCSS", rowUpdated?.chaptersTitleCSS)
    Assert.assertEquals("pagesTemplateUrl", rowUpdated?.pagesTemplateUrl)
    Assert.assertEquals("pagesCSS", rowUpdated?.pagesCSS)
    Assert.assertEquals("pagesImageCSS", rowUpdated?.pagesImageCSS)
  }

  @Test
  fun testDelete() {
    val create = ParserConfigCreate()
    val rowId = controller.create(create)
    controller.delete(ParserConfigDelete(id = rowId))
    val row = controller.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}