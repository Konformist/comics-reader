package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.chapter.Chapter
import com.konformist.comicsreader.db.chapter.ChapterCreate
import com.konformist.comicsreader.db.chapter.ChapterDao
import com.konformist.comicsreader.db.chapter.ChapterDelete
import com.konformist.comicsreader.db.chapter.ChapterUpdate
import com.konformist.comicsreader.db.chapterpage.ChapterPage
import com.konformist.comicsreader.db.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.db.chapterpage.ChapterPageDao
import com.konformist.comicsreader.db.chapterpage.ChapterPageDelete
import com.konformist.comicsreader.db.chapterpage.ChapterPageUpdate
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.DatesUtils
import java.io.InputStream

class ChapterController(
  private val chapterDao: ChapterDao,
  private val chapterPageDao: ChapterPageDao,
  private val filesController: AppFilesController,
) {
  @Throws(DatabaseException::class)
  fun createPageFile(page: ChapterPage, mime: String, file: InputStream): Long {
    if (page.fileId != null && page.fileId != 0L)
      filesController.deleteImage(page.fileId)

    val rowId = filesController.createImage(mime, file)

    updatePage(
      ChapterPageUpdate(
        id = page.id,
        mdate = page.mdate,
        isRead = page.isRead,
        fromUrl = page.fromUrl,
        fileId = rowId,
      )
    )

    return rowId
  }

  @Throws(DatabaseException::class)
  fun deletePageFile(page: ChapterPage): Boolean {
    if (page.fileId == null || page.fileId == 0L)
      filesController.deleteImage(page.fileId)

    return updatePage(
      ChapterPageUpdate(
        id = page.id,
        mdate = page.mdate,
        isRead = page.isRead,
        fromUrl = page.fromUrl,
        fileId = 0,
      )
    )
  }

  @Throws(DatabaseException::class)
  fun createPage(page: ChapterPageCreate): Long {
    val rowId = chapterPageDao.create(
      ChapterPageCreate(
        chapterId = page.chapterId,
        isRead = page.isRead,
        fromUrl = page.fromUrl,
        fileId = page.fileId,
      )
    )
    Validation.dbCreate(rowId, "ChapterPage")
    return rowId
  }

  @Throws(DatabaseException::class)
  fun updatePage(page: ChapterPageUpdate): Boolean {
    val count = chapterPageDao.update(
      ChapterPageUpdate(
        id = page.id,
        mdate = DatesUtils.nowFormatted(),
        fromUrl = page.fromUrl,
        fileId = page.fileId,
        isRead = page.isRead,
      )
    )
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

  @Throws(DatabaseException::class)
  fun create(chapter: ChapterCreate): Long {
    val rowId = chapterDao.create(chapter)
    Validation.dbCreate(rowId, "ChapterPage")
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(chapter: ChapterUpdate): Boolean {
    val count = chapterDao.update(
      ChapterUpdate(
        id = chapter.id,
        mdate = DatesUtils.nowFormatted(),
        name = chapter.name,
      )
    )
    Validation.dbUpdate(count, "ChapterPage")
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