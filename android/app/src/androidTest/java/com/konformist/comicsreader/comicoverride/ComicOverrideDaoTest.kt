package com.konformist.comicsreader.comicoverride

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.konformist.comicsreader.data.comicoverride.ComicOverrideCreate
import com.konformist.comicsreader.data.comicoverride.ComicOverrideDao
import com.konformist.comicsreader.data.comicoverride.ComicOverrideDelete
import com.konformist.comicsreader.data.comicoverride.ComicOverrideUpdate
import com.konformist.comicsreader.data.parserconfig.ParserConfigCreate
import com.konformist.comicsreader.data.parserconfig.ParserConfigDao
import com.konformist.comicsreader.data.parserconfig.ParserConfigDelete
import com.konformist.comicsreader.data.parserconfig.ParserConfigUpdate
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.utils.DatesUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ComicOverrideDaoTest {
  private lateinit var db: AppDatabase
  private lateinit var dao: ComicOverrideDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    dao = db.comicOverrideDao()
  }

  @Test
  fun testCreate() {
    val create = ComicOverrideCreate(
      comicId = 1L,
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

    val rowId = dao.create(create)
    val row = dao.read(rowId)

    Assert.assertNotNull(row)
    Assert.assertNotNull(row?.cdate)
    Assert.assertNotNull(row?.mdate)
    Assert.assertNotEquals("", row?.cdate)
    Assert.assertNotEquals("", row?.mdate)
    Assert.assertEquals(1L, row?.comicId)
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
  fun testCreateNullable() {
    val create = ComicOverrideCreate(comicId = 1)
    val rowId = dao.create(create)
    val row = dao.read(rowId)

    Assert.assertNotNull(row)
    Assert.assertEquals(1L, row?.comicId)
    Assert.assertEquals("", row?.titleCSS)
    Assert.assertEquals("", row?.annotationCSS)
    Assert.assertEquals("", row?.coverCSS)
    Assert.assertEquals("", row?.authorsCSS)
    Assert.assertEquals("", row?.authorsTextCSS)
    Assert.assertEquals("", row?.languageCSS)
    Assert.assertEquals("", row?.tagsCSS)
    Assert.assertEquals("", row?.tagsTextCSS)
    Assert.assertEquals("", row?.chaptersCSS)
    Assert.assertEquals("", row?.chaptersTitleCSS)
    Assert.assertEquals("", row?.pagesTemplateUrl)
    Assert.assertEquals("", row?.pagesCSS)
    Assert.assertEquals("", row?.pagesImageCSS)
  }

  @Test
  fun testUpdate() {
    // Создаем объект для вставки
    val create = ComicOverrideCreate(
      comicId = 1,
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

    val rowId = dao.create(create)
    val rowCreated = dao.read(rowId)

    Thread.sleep(1000)
    val update = ComicOverrideUpdate(
      id = rowId,
      mdate = DatesUtils.nowFormatted(),
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

    dao.update(update)
    val rowUpdated = dao.read(rowId)

    Assert.assertEquals(rowCreated?.cdate, rowUpdated?.cdate)
    Assert.assertNotEquals("", rowUpdated?.mdate)
    Assert.assertNotEquals(rowCreated?.mdate, rowUpdated?.mdate)
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
  fun testDelete() {
    val create = ComicOverrideCreate(comicId = 1)
    val rowId = dao.create(create)
    val delete = ComicOverrideDelete(id = rowId)
    dao.delete(delete)
    val row = dao.read(rowId)

    Assert.assertNull(row)
  }

  @After
  fun tearDown() {
    db.close()
  }
}