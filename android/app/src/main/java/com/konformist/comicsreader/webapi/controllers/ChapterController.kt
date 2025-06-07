package com.konformist.comicsreader.webapi.controllers

import com.konformist.comicsreader.db.chapter.Chapter
import com.konformist.comicsreader.db.chapter.ChapterCreate
import com.konformist.comicsreader.db.chapter.ChapterDao
import com.konformist.comicsreader.db.chapter.ChapterDelete
import com.konformist.comicsreader.db.chapter.ChapterUpdate
import com.konformist.comicsreader.db.chapter.ChapterWithPages
import com.konformist.comicsreader.db.chapterpage.ChapterPage
import com.konformist.comicsreader.db.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.db.chapterpage.ChapterPageDao
import com.konformist.comicsreader.db.chapterpage.ChapterPageDelete
import com.konformist.comicsreader.db.chapterpage.ChapterPageUpdate
import com.konformist.comicsreader.db.chapterpage.ChapterPageWithFile
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.webapi.Validation
import java.io.InputStream

class ChapterController(
  private val chapterDao: ChapterDao,
  private val chapterPageDao: ChapterPageDao,
  private val filesController: AppFilesController,
) {
  private fun makePageUpdate(page: ChapterPage, fileId: Long): ChapterPageUpdate {
    return ChapterPageUpdate(
      id = page.id,
      mdate = page.mdate,
      isRead = page.isRead,
      fromUrl = page.fromUrl,
      fileId = fileId,
    )
  }

  @Throws(DatabaseException::class)
  fun createPageFile(page: ChapterPage, mime: String, file: InputStream): Long {
    if (page.fileId != null && page.fileId != 0L)
      filesController.deleteImage(page.fileId)

    val rowId = filesController.createImage(mime, file)
    updatePage(makePageUpdate(page, rowId))
    return rowId
  }

  @Throws(DatabaseException::class)
  fun deletePageFile(page: ChapterPage): Boolean {
    if (page.fileId == null || page.fileId == 0L)
      filesController.deleteImage(page.fileId)

    return updatePage(makePageUpdate(page, 0))
  }

  fun readPageByChapterAll(chapterId: Long): List<ChapterPageWithFile> {
    return chapterPageDao.readByChapterAll(chapterId)
  }

  fun readPageWithFile(id: Long): ChapterPageWithFile {
    return chapterPageDao.readWithFile(id)
  }

  fun readPage(id: Long): ChapterPage {
    return chapterPageDao.read(id)
  }

  @Throws(DatabaseException::class)
  fun createPage(page: ChapterPageCreate): Long {
    val rowId = chapterPageDao.create(page)
    Validation.dbCreate(rowId, "ChapterPage")
    return rowId
  }

  @Throws(DatabaseException::class)
  fun updatePage(page: ChapterPageUpdate): Boolean {
    page.mdate = DatesUtils.nowFormatted()
    val count = chapterPageDao.update(page)
    Validation.dbUpdate(count, "ChapterPage")
    return true
  }

  @Throws(DatabaseException::class)
  fun deletePage(page: ChapterPage): Boolean {
    if (page.fileId != null && page.fileId != 0L) {
      filesController.deleteImage(page.fileId)
    }

    val count = chapterPageDao.delete(ChapterPageDelete(id = page.id))
    Validation.dbDelete(count, "ChapterPage")
    return true
  }

  fun readByComicAll(comicId: Long): List<ChapterWithPages> {
    return chapterDao.readWithPagesByComicAll(comicId)
  }

  fun readWithPages(id: Long): ChapterWithPages {
    return chapterDao.readWithPages(id)
  }

  fun read(id: Long): Chapter {
    return chapterDao.read(id)
  }

  @Throws(DatabaseException::class)
  fun create(chapter: ChapterCreate): Long {
    val rowId = chapterDao.create(chapter)
    Validation.dbCreate(rowId, "Chapter")
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(chapter: ChapterUpdate): Boolean {
    chapter.mdate = DatesUtils.nowFormatted()
    val count = chapterDao.update(chapter)
    Validation.dbUpdate(count, "Chapter")
    return true
  }

  @Throws(DatabaseException::class)
  fun delete(chapter: Chapter): Boolean {
    val row = chapterDao.readWithPages(chapter.id)
    row.pages.forEach { page -> deletePage(page.page) }

    val count = chapterDao.delete(ChapterDelete(id = chapter.id))
    Validation.dbDelete(count, "Chapter")
    return true
  }
}