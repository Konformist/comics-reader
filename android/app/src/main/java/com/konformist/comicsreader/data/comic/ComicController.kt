package com.konformist.comicsreader.data.comic

import com.konformist.comicsreader.data.comiccover.ComicCoverController
import com.konformist.comicsreader.data.comiccover.ComicCoverCreate
import com.konformist.comicsreader.data.comiccover.ComicCoverDelete
import com.konformist.comicsreader.data.comicoverride.ComicOverrideController
import com.konformist.comicsreader.data.comicoverride.ComicOverrideCreate
import com.konformist.comicsreader.data.comicoverride.ComicOverrideDelete
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.data.chapter.ChapterController
import com.konformist.comicsreader.data.chapter.ChapterCreate
import com.konformist.comicsreader.data.chapter.ChapterDelete
import com.konformist.comicsreader.data.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.utils.FileManager
import com.konformist.comicsreader.utils.comicarchive.ComicArchive
import com.konformist.comicsreader.webapi.Validation
import java.io.File

class ComicController(
  private val dao: ComicDao,
  private val comicCoverController: ComicCoverController,
  private val comicOverrideController: ComicOverrideController,
  private val chapterController: ChapterController,
) {
  private val archive = ComicArchive()

  companion object {
    const val FORMAT_CBZ = "cbz"
    const val FORMAT_CBT = "cbt"
    const val FORMAT_ZIP = "zip"
    const val FORMAT_TAR = "tar"
  }

  private val entityName = "Comic"

  fun readLiteAll(): List<ComicLite> {
    return dao.readLiteAll()
  }

  fun readLite(id: Long): ComicLite {
    return dao.readLite(id)
  }

  fun read(id: Long): Comic? {
    return dao.read(id)
  }

  private fun createDir(id: Long) {
    val dir = File(FileManager.comicsDir, id.toString())
    if (dir.exists()) dir.deleteRecursively()
    dir.mkdirs()
  }

  private fun deleteDir(id: Long) {
    val dir = File(FileManager.comicsDir, id.toString())
    if (dir.exists()) dir.deleteRecursively()
  }

  @Throws(DatabaseException::class)
  fun create(value: ComicCreate): Long {
    val rowId = dao.create(value)
    Validation.dbCreate(rowId, entityName)

    createDir(rowId)
    comicCoverController.create(ComicCoverCreate(comicId = rowId))
    comicOverrideController.create(ComicOverrideCreate(comicId = rowId))

    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(value: ComicUpdate): Boolean {
    val row = read(value.id) ?: return false
    val count = dao.update(value.merge(row))
    Validation.dbUpdate(count, entityName)

    return true
  }

  @Throws(DatabaseException::class)
  fun delete(value: ComicDelete): Boolean {
    val cover = comicCoverController.readByComic(value.id)
    if (cover != null) comicCoverController.delete(ComicCoverDelete(id = cover.id))

    val override = comicOverrideController.readByComic(value.id)
    if (override != null) comicOverrideController.delete(ComicOverrideDelete(id = override.id))

    deleteDir(value.id)

    val chapters = chapterController.readByComicAll(value.id)
    chapters.forEach { chapter ->
      chapter.chapter?.let { chapterController.delete(ChapterDelete(id = it.id)) }
    }

    val count = dao.delete(value)
    Validation.dbDelete(count, entityName)

    return true
  }

  fun fromArchive(uri: String): Long {
    val result = archive.comicFromArchive(uri) ?: return 0L

    val comicId = create(result.comic)
    result.chapters.forEach { chapter ->
      val chapterId = chapterController.create(ChapterCreate(
        name = chapter.chapter.name,
        comicId = comicId,
      ))

      chapter.files.forEach { file ->
        val pageId = chapterController.createPage(ChapterPageCreate(chapterId = chapterId))
        chapterController.createPageFile(pageId, file)
      }
    }

    archive.recycle()

    return comicId
  }

  fun toArchive(id: Long): Boolean {
    val comic = readLite(id)
    val chapters = chapterController.readByComicAll(id)
    val result = archive.comicToArchive(comic, chapters)
    archive.recycle()

    return result
  }
}