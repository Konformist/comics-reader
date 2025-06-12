package com.konformist.comicsreader.data.chapter

import com.konformist.comicsreader.data.chapterpage.ChapterPage
import com.konformist.comicsreader.data.chapterpage.ChapterPageController
import com.konformist.comicsreader.data.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.data.chapterpage.ChapterPageDelete
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.FileManager
import com.konformist.comicsreader.webapi.Validation
import java.io.File
import java.io.FileInputStream

class ChapterController(
  private val chapterDao: ChapterDao,
  private val chapterPageController: ChapterPageController,
) {
  private val entityName = "Chapter"

  fun createPage(value: ChapterPageCreate) = chapterPageController.create(value)
  fun createPageFile(id: Long, file: File): Long {
    return FileInputStream(file).use {
      val page = chapterPageController.read(id) ?: return 0L

      chapterPageController.createFile(
        page,
        FileManager.getMimeFromExtension(file.extension),
        it,
      )
    }
  }

  fun readByComicAll(comicId: Long): List<ChapterWithPages> {
    return chapterDao.readWithPagesByComicAll(comicId)
  }

  fun readWithPages(id: Long): ChapterWithPages {
    return chapterDao.readWithPages(id)
  }

  fun read(id: Long): Chapter? {
    return chapterDao.read(id)
  }

  private fun createDir(id: Long) {
    val dir = File(FileManager.chaptersDir, id.toString())
    if (dir.exists()) dir.deleteRecursively()
    dir.mkdirs()
  }

  private fun deleteDir(id: Long) {
    val dir = File(FileManager.chaptersDir, id.toString())
    if (dir.exists()) dir.deleteRecursively()
  }

  @Throws(DatabaseException::class)
  fun create(chapter: ChapterCreate): Long {
    val rowId = chapterDao.create(chapter)
    Validation.dbCreate(rowId, entityName)
    createDir(rowId)

    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(chapter: ChapterUpdate): Boolean {
    val row = read(chapter.id) ?: return false
    val count = chapterDao.update(chapter.merge(row))
    Validation.dbUpdate(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun updateComic(chapter: ChapterUpdateComic): Boolean {
    val row = read(chapter.id) ?: return false
    val count = chapterDao.updateComic(chapter.merge(row))
    Validation.dbUpdate(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun delete(chapter: ChapterDelete): Boolean {
    val row = chapterDao.readWithPages(chapter.id)
    row.pages.forEach { page ->
      page.page?.let { chapterPageController.delete(ChapterPageDelete(id = it.id)) }
    }

    deleteDir(chapter.id)

    val count = chapterDao.delete(ChapterDelete(id = chapter.id))
    Validation.dbDelete(count, entityName)

    return true
  }
}