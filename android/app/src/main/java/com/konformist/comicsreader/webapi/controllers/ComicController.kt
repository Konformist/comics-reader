package com.konformist.comicsreader.webapi.controllers

import com.konformist.comicsreader.db.chapter.ChapterDao
import com.konformist.comicsreader.db.comic.Comic
import com.konformist.comicsreader.db.comic.ComicCreate
import com.konformist.comicsreader.db.comic.ComicDao
import com.konformist.comicsreader.db.comic.ComicDelete
import com.konformist.comicsreader.db.comic.ComicLite
import com.konformist.comicsreader.db.comic.ComicUpdate
import com.konformist.comicsreader.db.comiccover.ComicCover
import com.konformist.comicsreader.db.comiccover.ComicCoverCreate
import com.konformist.comicsreader.db.comiccover.ComicCoverDao
import com.konformist.comicsreader.db.comiccover.ComicCoverDelete
import com.konformist.comicsreader.db.comiccover.ComicCoverUpdate
import com.konformist.comicsreader.db.comiccover.ComicCoverWithFile
import com.konformist.comicsreader.db.comicoverride.ComicOverride
import com.konformist.comicsreader.db.comicoverride.ComicOverrideCreate
import com.konformist.comicsreader.db.comicoverride.ComicOverrideDao
import com.konformist.comicsreader.db.comicoverride.ComicOverrideDelete
import com.konformist.comicsreader.db.comicoverride.ComicOverrideUpdate
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.webapi.Validation
import java.io.InputStream

class ComicController(
  private val comicCoverDao: ComicCoverDao,
  private val comicOverrideDao: ComicOverrideDao,
  private val comicDao: ComicDao,
  private val filesController: AppFilesController,
  private val chapterController: ChapterController,
) {
  companion object {
    const val FORMAT_CBZ = "cbz"
    const val FORMAT_CBT = "cbt"
    const val FORMAT_ZIP = "zip"
    const val FORMAT_TAR = "tar"
  }

  private fun makeCoverUpdate(cover: ComicCover, fielId: Long): ComicCoverUpdate {
    return ComicCoverUpdate(
      id = cover.id,
      mdate = cover.mdate,
      fromUrl = cover.fromUrl,
      fileId = fielId
    )
  }

  @Throws(DatabaseException::class)
  fun createCoverFile(cover: ComicCover, mime: String, file: InputStream): Long {
    if (cover.fileId != null && cover.fileId != 0L)
      filesController.deleteImage(cover.fileId)

    val rowId = filesController.createImage(mime, file)
    updateCover(makeCoverUpdate(cover, rowId))
    return rowId
  }

  @Throws(DatabaseException::class)
  fun deleteCoverFile(cover: ComicCover): Boolean {
    if (cover.fileId == null || cover.fileId == 0L)
      filesController.deleteImage(cover.fileId)

    return updateCover(makeCoverUpdate(cover, 0L))
  }

  fun readCover(id: Long): ComicCover? {
    return comicCoverDao.read(id)
  }

  fun readCoverByComic(id: Long): ComicCover? {
    return comicCoverDao.readByComic(id)
  }

  @Throws(DatabaseException::class)
  fun createCover(cover: ComicCoverCreate): Long {
    val rowId = comicCoverDao.create(cover)

    Validation.dbCreate(rowId, "ComicCover")
    return rowId
  }

  @Throws(DatabaseException::class)
  fun updateCover(cover: ComicCoverUpdate): Boolean {
    cover.mdate = DatesUtils.nowFormatted()
    val count = comicCoverDao.update(cover)
    Validation.dbUpdate(count, "ComicCover")
    return true
  }

  @Throws(DatabaseException::class)
  fun deleteCover(cover: ComicCover): Boolean {
    if (cover.fileId != null && cover.fileId != 0L)
      filesController.deleteImage(cover.fileId)

    val count = comicCoverDao.delete(ComicCoverDelete(id = cover.id))
    Validation.dbDelete(count, "ComicCover")
    return true
  }

  fun readOverrideByComic(comicId: Long): ComicOverride? {
    return comicOverrideDao.readByComic(comicId)
  }

  @Throws(DatabaseException::class)
  fun createOverride(override: ComicOverrideCreate): Long {
    val rowId = comicOverrideDao.create(override)
    Validation.dbCreate(rowId, "ComicOverride")

    return rowId
  }

  @Throws(DatabaseException::class)
  fun updateOverride(override: ComicOverrideUpdate): Boolean {
    override.mdate = DatesUtils.nowFormatted()
    val count = comicOverrideDao.update(override)
    Validation.dbUpdate(count, "ComicCover")
    return true
  }

  @Throws(DatabaseException::class)
  fun deleteOverride(override: ComicOverride): Boolean {
    val count = comicOverrideDao.delete(ComicOverrideDelete(id = override.id))
    Validation.dbDelete(count, "ComicOverride")
    return true
  }

  fun read(id: Long): Comic {
    return comicDao.read(id)
  }

  fun readLiteAll(): List<ComicLite> {
    return comicDao.readLiteAll()
  }

  fun readLite(id: Long): ComicLite {
    return comicDao.readLite(id)
  }

  @Throws(DatabaseException::class)
  fun create(comic: ComicCreate): Long {
    val rowId = comicDao.create(comic)
    Validation.dbCreate(rowId, "Comic")

    createCover(ComicCoverCreate(comicId = rowId))
    createOverride(ComicOverrideCreate(comicId = rowId))

    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(comic: ComicUpdate): Boolean {
    comic.mdate = DatesUtils.nowFormatted()
    val count = comicDao.update(comic)
    Validation.dbUpdate(count, "Comic")

    return true
  }

  @Throws(DatabaseException::class)
  fun delete(comic: ComicDelete): Boolean {
    val cover = comicCoverDao.readByComic(comic.id)
    if (cover != null) deleteCover(cover)

    val override = comicOverrideDao.readByComic(comic.id)
    if (override != null) deleteOverride(override)

    val chapters = chapterController.readByComicAll(comic.id)
    chapters.forEach { chapter -> chapterController.delete(chapter.chapter) }

    val count = comicDao.delete(comic)
    Validation.dbDelete(count, "Comic")

    return true
  }
}