package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.chapter.ChapterDao
import com.konformist.comicsreader.db.comic.ComicCreate
import com.konformist.comicsreader.db.comic.ComicDao
import com.konformist.comicsreader.db.comic.ComicDelete
import com.konformist.comicsreader.db.comic.ComicUpdate
import com.konformist.comicsreader.db.comiccover.ComicCover
import com.konformist.comicsreader.db.comiccover.ComicCoverCreate
import com.konformist.comicsreader.db.comiccover.ComicCoverDao
import com.konformist.comicsreader.db.comiccover.ComicCoverDelete
import com.konformist.comicsreader.db.comiccover.ComicCoverUpdate
import com.konformist.comicsreader.db.comicoverride.ComicOverride
import com.konformist.comicsreader.db.comicoverride.ComicOverrideCreate
import com.konformist.comicsreader.db.comicoverride.ComicOverrideDao
import com.konformist.comicsreader.db.comicoverride.ComicOverrideDelete
import com.konformist.comicsreader.db.comicoverride.ComicOverrideUpdate
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.DatesUtils
import java.io.InputStream

class ComicController(
  private val comicCoverDao: ComicCoverDao,
  private val comicOverrideDao: ComicOverrideDao,
  private val comicDao: ComicDao,
  private val chapterDao: ChapterDao,
  private val filesController: AppFilesController,
  private val chapterController: ChapterController,
) {
  @Throws(DatabaseException::class)
  fun createCoverFile(cover: ComicCover, mime: String, file: InputStream): Long {
    if (cover.fileId != null && cover.fileId != 0L)
      filesController.deleteImage(cover.fileId)

    val rowId = filesController.createImage(mime, file)

    updateCover(
      ComicCoverUpdate(
        id = cover.id,
        mdate = cover.mdate,
        fromUrl = cover.fromUrl,
        fileId = rowId
      )
    )

    return rowId
  }

  @Throws(DatabaseException::class)
  fun deleteCoverFile(cover: ComicCover): Boolean {
    if (cover.fileId == null || cover.fileId == 0L)
      filesController.deleteImage(cover.fileId)

    return updateCover(
      ComicCoverUpdate(
        id = cover.id,
        mdate = cover.mdate,
        fromUrl = cover.fromUrl,
        fileId = 0,
      )
    )
  }

  @Throws(DatabaseException::class)
  fun createCover(cover: ComicCoverCreate): Long {
    val rowId = comicCoverDao.create(cover)

    Validation.dbCreate(rowId, "ComicCover")
    return rowId
  }

  @Throws(DatabaseException::class)
  fun updateCover(cover: ComicCoverUpdate): Boolean {
    val count = comicCoverDao.update(
      ComicCoverUpdate(
        id = cover.id,
        mdate = DatesUtils.nowFormatted(),
        fileId = cover.fileId,
        fromUrl = cover.fromUrl,
      )
    )
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

  @Throws(DatabaseException::class)
  fun createOverride(override: ComicOverrideCreate): Long {
    val rowId = comicOverrideDao.create(override)
    Validation.dbCreate(rowId, "ComicOverride")

    return rowId
  }

  @Throws(DatabaseException::class)
  fun updateOverride(override: ComicOverrideUpdate): Boolean {
    val count = comicOverrideDao.update(
      ComicOverrideUpdate(
        id = override.id,
        mdate = DatesUtils.nowFormatted(),
        titleCSS = override.titleCSS,
        annotationCSS = override.annotationCSS,
        coverCSS = override.coverCSS,
        authorsCSS = override.authorsCSS,
        authorsTextCSS = override.authorsTextCSS,
        languageCSS = override.languageCSS,
        tagsCSS = override.tagsCSS,
        tagsTextCSS = override.tagsTextCSS,
        chaptersCSS = override.chaptersCSS,
        chaptersTitleCSS = override.chaptersTitleCSS,
        pagesTemplateUrl = override.pagesTemplateUrl,
        pagesCSS = override.pagesCSS,
        pagesImageCSS = override.pagesImageCSS,
      )
    )
    Validation.dbUpdate(count, "ComicCover")
    return true
  }

  @Throws(DatabaseException::class)
  fun deleteOverride(override: ComicOverride): Boolean {
    val count = comicOverrideDao.delete(ComicOverrideDelete(id = override.id))
    Validation.dbDelete(count, "ComicOverride")
    return true
  }

  @Throws(DatabaseException::class)
  fun create(comic: ComicCreate): Long {
    val rowId = comicDao.create(comic)
    Validation.dbCreate(rowId, "Comic")

    createCover(
      ComicCoverCreate(
        comicId = rowId,
        fromUrl = "",
        fileId = 0L,
      )
    )
    createOverride(
      ComicOverrideCreate(
        comicId = rowId,
        titleCSS = "",
        annotationCSS = "",
        coverCSS = "",
        authorsCSS = "",
        authorsTextCSS = "",
        languageCSS = "",
        tagsCSS = "",
        tagsTextCSS = "",
        chaptersCSS = "",
        chaptersTitleCSS = "",
        pagesCSS = "",
        pagesTemplateUrl = "",
        pagesImageCSS = "",
      )
    )

    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(comic: ComicUpdate): Boolean {
    val count = comicDao.update(
      ComicUpdate(
        id = comic.id,
        mdate = DatesUtils.nowFormatted(),
        name = comic.name,
        parserId = comic.parserId,
        fromUrl = comic.fromUrl,
        annotation = comic.annotation,
        languageId = comic.languageId,
        tags = comic.tags,
        authors = comic.authors,
      )
    )
    Validation.dbUpdate(count, "Comic")

    return true
  }

  @Throws(DatabaseException::class)
  fun delete(comic: ComicDelete): Boolean {
    val cover = comicCoverDao.readByComic(comic.id)
    deleteCover(cover)

    val override = comicOverrideDao.readByComic(comic.id)
    deleteOverride(override)

    val chapters = chapterDao.readByComicAll(comic.id)
    chapters.forEach { chapter -> chapterController.delete(chapter) }

    val count = comicDao.delete(comic)
    Validation.dbDelete(count, "Comic")

    return true
  }
}