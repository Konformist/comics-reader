package com.konformist.comicsreader.webapi

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.room.Room
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.db.DBBackup
import com.konformist.comicsreader.db.appfile.AppFileCreate
import com.konformist.comicsreader.db.appfile.AppFileDelete
import com.konformist.comicsreader.db.appfile.AppFileUpdate
import com.konformist.comicsreader.db.chapter.ChapterCreate
import com.konformist.comicsreader.db.chapter.ChapterDelete
import com.konformist.comicsreader.db.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.db.chapterpage.ChapterPageDelete
import com.konformist.comicsreader.db.chapterpage.ChapterPageUpdate
import com.konformist.comicsreader.db.comic.ComicCreate
import com.konformist.comicsreader.db.comiccover.ComicCoverCreate
import com.konformist.comicsreader.db.comiccover.ComicCoverDelete
import com.konformist.comicsreader.db.comiccover.ComicCoverUpdate
import com.konformist.comicsreader.db.comicoverride.ComicOverrideCreate
import com.konformist.comicsreader.db.comicoverride.ComicOverrideDelete
import com.konformist.comicsreader.utils.AppDirectory
import com.konformist.comicsreader.utils.DatabaseException
import com.konformist.comicsreader.utils.Dates
import com.konformist.comicsreader.utils.FileUtils
import com.konformist.comicsreader.utils.FilesException
import com.konformist.comicsreader.utils.ValidationException
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import kotlin.io.path.Path
import kotlin.io.path.exists

class WebApi(private val context: Context) {
  private val db: AppDatabase = Room
    .databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
    .build()

  private val dbBackup = DBBackup(context)

  private val tagDao = db.tagDao()
  private val authorDao = db.authorDao()
  private val languageDao = db.languageDao()
  private val parserDao = db.parserDao()
  private val fileDao = db.fileDao()
  private val comicOverrideDao = db.comicOverrideDao()
  private val comicCoverDao = db.comicCoverDao()
  private val chapterDao = db.chapterDao()
  private val chapterPageDao = db.chapterPageDao()
  private val comicDao = db.comicDao()

  private val tagSerializer = TagSerializer()
  private val authorSerializer = AuthorSerializer()
  private val languageSerializer = LanguageSerializer()
  private val parserSerializer = ParserSerializer()
  private val fileSerializer = FileSerializer()
  private val chapterSerializer = ChapterSerializer()
  private val chapterPageSerializer = ChapterPageSerializer()
  private val comicCoverSerializer = ComicCoverSerializer()
  private val comicOverrideSerializer = ComicOverrideSerializer()
  private val comicSerializer = ComicSerializer()

  private fun <T : Exception> wrappedToError(value: T): JSONObject {
    Log.d("Error", "${value.message} ${value.stackTrace}")

    return JSONObject()
      .put("error", value.message)
  }

  private fun wrappedToResult(value: Any): JSONObject {
    return JSONObject()
      .put("result", value)
  }

  @Throws(ValidationException::class)
  private fun checkRowId(rowId: Long, field: String? = "Id") {
    if (rowId == 0.toLong()) throw ValidationException("$field is empty")
  }

  @Throws(DatabaseException::class)
  private fun createComicOverride(comicId: Long): Long {
    val rowId = comicOverrideDao.create(ComicOverrideCreate(
      comicId = comicId,
      titleCSS = "",
      coverCSS = "",
      pagesCSS = "",
      authorsCSS = "",
      authorsTextCSS = "",
      languageCSS = "",
      tagsCSS = "",
      tagsTextCSS = "",
    ))
    if (rowId == 0.toLong()) throw DatabaseException("Comic override not created")
    return rowId
  }

  @Throws(DatabaseException::class)
  private fun deleteComicOverride(comicId: Long) {
    val row = comicOverrideDao.readByComic(comicId)

    val count = comicOverrideDao.delete(ComicOverrideDelete(id = row.id))
    if (count == 0) throw DatabaseException("Comic override not deleted")
  }

  @Throws(DatabaseException::class)
  private fun createCover(comicId: Long) {
    val rowId = comicCoverDao.create(ComicCoverCreate(
      comicId = comicId,
      fileId = 0,
      fromUrl = "",
    ))
    if (rowId == 0.toLong()) throw DatabaseException("Comic cover not created")
  }

  @Throws(DatabaseException::class)
  private fun updateCover(cover: ComicCoverUpdate) {
    val count = comicCoverDao.update(cover)
    if (count == 0) throw DatabaseException("Comic cover not updated")
  }

  @Throws(DatabaseException::class)
  private fun deleteCover(comicId: Long) {
    val row = comicCoverDao.readByComic(comicId)

    if (row.fileId != null && row.fileId != 0.toLong())
      deleteImage(row.fileId)

    val count = comicCoverDao.delete(ComicCoverDelete(id = row.id))
    if (count == 0) throw DatabaseException("Comic cover not deleted")
  }

  @Throws(DatabaseException::class)
  private fun deleteChapter(chapterId: Long) {
    val row = chapterDao.readWithPages(chapterId)

    val pages = chapterPageDao.readByChapterAll(chapterId)
    for (page in pages) deleteChapterPage(page.page.id)

    val count = chapterDao.delete(ChapterDelete(id = row.chapter.id))
    if (count == 0) throw DatabaseException("Chapter not deleted")
  }

  @Throws(DatabaseException::class)
  private fun deleteChapterPage(id: Long) {
    val row = chapterPageDao.read(id)
    if (row.fileId != 0.toLong()) deleteImage(row.fileId)

    val count = chapterPageDao.delete(ChapterPageDelete(id = id))
    if (count == 0) throw DatabaseException("Chapter page not deleted")
  }

  @Throws(DatabaseException::class)
  private fun createComicImage(directory: String, name: String, file: String): Long {
    val cleaned = FileUtils.cleanImageData(file)
    val decodedImage: Bitmap = FileUtils.base64ToBitmap(cleaned)
    val dirOut = File("${context.filesDir}/${AppDirectory.COMICS_IMAGES}/${directory}")
    val fileOut = File("${dirOut.path}/${name}")

    if (!dirOut.exists()) dirOut.mkdirs()
    FileUtils.writeImage(fileOut, decodedImage)

    try {
      val rowId = fileDao.create(AppFileCreate(
        name = name,
        mime = "image/webp",
        size = fileOut.length(),
        path = "${AppDirectory.COMICS_IMAGES}/$directory/$name",
      ))
      if (rowId == 0.toLong()) throw DatabaseException("File not created")
      return rowId
    } catch (e: Error) {
      if (fileOut.exists()) fileOut.delete()
      throw e
    }
  }

  @Throws(DatabaseException::class)
  private fun deleteImage(id: Long?) {
    if (id == null || id == 0.toLong()) return
    val row = fileDao.read(id)

    val fileOut = File("${context.filesDir}/${row.path}")
    if (fileOut.exists()) fileOut.delete()

    val count = fileDao.delete(AppFileDelete(id = row.id))
    if (count == 0) throw DatabaseException("File not deleted")
  }

  @Throws(FilesException::class)
  private fun createComicDirectory(path: String) {
    val dirOut = File("${context.filesDir}/${AppDirectory.COMICS_IMAGES}/${path}")
    if (dirOut.exists()) return
    val result = dirOut.mkdirs()
    if (!result) throw FilesException("Directory not created ${AppDirectory.COMICS_IMAGES}/${path}")
  }

  @Throws(FilesException::class)
  private fun deleteComicDirectory(path: String) {
    val dirOut = File("${context.filesDir}/${AppDirectory.COMICS_IMAGES}/${path}")
    if (dirOut.exists()) return
    val result = dirOut.delete()
    if (!result) throw FilesException("Directory not deleted ${AppDirectory.COMICS_IMAGES}/${path}")
  }

  fun getTagsAll(): JSONObject {
    return wrappedToResult(tagSerializer.toJSONArray(tagDao.readAll()))
  }

  fun getTag(data: JSONObject): JSONObject {
    try {
      val rowId = data.optLong("id", 0)
      checkRowId(rowId)

      return wrappedToResult(tagSerializer.toJSON(tagDao.read(rowId)))
    } catch (e: ValidationException) {
      return wrappedToError(e)
    }
  }

  fun addTag(data: JSONObject): JSONObject {
    try {
      val rowId = tagDao.create(tagSerializer.createFromJSON(data))
      if (rowId == 0.toLong()) throw DatabaseException("Tag not created")

      return wrappedToResult(rowId)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun setTag(data: JSONObject): JSONObject {
    try {
      val count = tagDao.update(tagSerializer.updateFromJSON(data))
      if (count == 0) throw DatabaseException("Tag not updated")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun delTag(data: JSONObject): JSONObject {
    try {
      val count = tagDao.delete(tagSerializer.deleteFromJSON(data))
      if (count == 0) throw DatabaseException("Tag not deleted")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun getAuthorsAll(): JSONObject {
    return wrappedToResult(authorSerializer.toJSONArray(authorDao.readAll()))
  }

  fun getAuthor(data: JSONObject): JSONObject {
    try {
      val rowId = data.optLong("id", 0)
      checkRowId(rowId)

      return wrappedToResult(authorSerializer.toJSON(authorDao.read(rowId)))
    } catch (e: ValidationException) {
      return wrappedToError(e)
    }
  }

  fun addAuthor(data: JSONObject): JSONObject {
    try {
      val rowId = authorDao.create(authorSerializer.createFromJSON(data))
      if (rowId == 0.toLong()) throw DatabaseException("Author not created")

      return wrappedToResult(rowId)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun setAuthor(data: JSONObject): JSONObject {
    try {
      val count = authorDao.update(authorSerializer.updateFromJSON(data))
      if (count == 0) throw DatabaseException("Author not updated")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun delAuthor(data: JSONObject): JSONObject {
    try {
      val count = authorDao.delete(authorSerializer.deleteFromJSON(data))
      if (count == 0) throw DatabaseException("Author not deleted")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun getLanguagesAll(): JSONObject {
    return wrappedToResult(languageSerializer.toJSONArray(languageDao.readAll()))
  }

  fun getLanguage(data: JSONObject): JSONObject {
    try {
      val rowId = data.optLong("id", 0)
      checkRowId(rowId)

      return wrappedToResult(languageSerializer.toJSON(languageDao.read(rowId)))
    } catch (e: ValidationException) {
      return wrappedToError(e)
    }
  }

  fun addLanguage(data: JSONObject): JSONObject {
    try {
      val rowId = languageDao.create(languageSerializer.createFromJSON(data))
      if (rowId == 0.toLong()) throw DatabaseException("Language not created")

      return wrappedToResult(rowId)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun setLanguage(data: JSONObject): JSONObject {
    try {
      val count = languageDao.update(languageSerializer.updateFromJSON(data))
      if (count == 0) throw DatabaseException("Language not updated")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun delLanguage(data: JSONObject): JSONObject {
    try {
      val count = languageDao.delete(languageSerializer.deleteFromJSON(data))
      if (count == 0) throw DatabaseException("Language not deleted")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun getParsersAll(): JSONObject {
    return wrappedToResult(parserSerializer.toJSONArray(parserDao.readAll()))
  }

  fun getParser(data: JSONObject): JSONObject {
    try {
      val rowId = data.optLong("id", 0)
      checkRowId(rowId)

      return wrappedToResult(parserSerializer.toJSON(parserDao.read(rowId)))
    } catch (e: ValidationException) {
      return wrappedToError(e)
    }
  }

  fun addParser(data: JSONObject): JSONObject {
    try {
      val rowId = parserDao.create(parserSerializer.createFromJSON(data))
      if (rowId == 0.toLong()) throw DatabaseException("Parser not created")

      return wrappedToResult(rowId)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun setParser(data: JSONObject): JSONObject {
    try {
      val count = parserDao.update(parserSerializer.updateFromJSON(data))
      if (count == 0) throw DatabaseException("Parser not updated")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun delParser(data: JSONObject): JSONObject {
    try {
      val count = parserDao.delete(parserSerializer.deleteFromJSON(data))
      if (count == 0) throw DatabaseException("Parser not deleted")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun getComicsAll(): JSONObject {
    return wrappedToResult(comicSerializer.toJSONArray(comicDao.readLiteAll()))
  }

  fun getComic(data: JSONObject): JSONObject {
    try {
      val rowId = data.optLong("id", 0)
      checkRowId(rowId)

      return wrappedToResult(comicSerializer.toJSON(comicDao.readLite(rowId)))
    } catch (e: ValidationException) {
      return wrappedToError(e)
    }
  }

  fun addComic(data: JSONObject): JSONObject {
    try {
      val rowId = comicDao.create(comicSerializer.createFromJSON(data))
      if (rowId == 0.toLong()) throw DatabaseException("Comic not created")

      createCover(rowId)
      createComicOverride(rowId)
      createComicDirectory(rowId.toString())

      return wrappedToResult(rowId)
    } catch (e: FilesException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun setComic(data: JSONObject): JSONObject {
    try {
      val comicId = data.getLong("id")
      checkRowId(comicId)

      val coverJSON = data.optJSONObject("cover")

      // TODO вынести обновление отдельно
      if (coverJSON != null) {
        val coverId = coverJSON.optLong("id")
        val cover = comicCoverDao.read(coverId)
        coverJSON.put("fileId", cover.fileId)
        updateCover(comicCoverSerializer.updateFromJSON(coverJSON))
      }

      val count = comicDao.update(comicSerializer.updateFromJSON(data))
      if (count == 0) throw DatabaseException("Comic not updated")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun delComic(data: JSONObject): JSONObject {
    try {
      val id = data.getLong("id")
      checkRowId(id)

      deleteCover(id)
      deleteComicOverride(id)

      val chapters = chapterDao.readByComicAll(id)
      for (chapter in chapters) deleteChapter(chapter.id)
      deleteComicDirectory(id.toString())

      val count = comicDao.delete(comicSerializer.deleteFromJSON(data))
      if (count == 0) throw DatabaseException("Comic not deleted")

      return wrappedToResult(true)
    } catch (e: FilesException) {
      return wrappedToError(e)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun getComicOverride(data: JSONObject): JSONObject {
    try {
      val rowId = data.optLong("comicId", 0)
      checkRowId(rowId)

      return wrappedToResult(comicOverrideSerializer.toJSON(comicOverrideDao.readByComic(rowId)))
    } catch (e: ValidationException) {
      return wrappedToError(e)
    }
  }

  fun setComicOverride(data: JSONObject): JSONObject {
    try {
      val count = comicOverrideDao.update(comicOverrideSerializer.updateFromJSON(data))
      if (count == 0) throw DatabaseException("Comic override not updated")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun addCoverFile(data: JSONObject): JSONObject {
    try {
      val comicId = data.optLong("comicId")
      checkRowId(comicId)
      val file = data.optString("file")
      if (file == "") throw ValidationException("File is empty")

      val cover = comicCoverDao.readByComic(comicId)

      if (cover.fileId != null && cover.fileId != 0.toLong())
        deleteImage(cover.fileId)

      val rowId = createComicImage(comicId.toString(), "cover.webp", file)
      if (rowId == 0.toLong()) throw DatabaseException("Comic cover file not added")

      val count = comicCoverDao.update(ComicCoverUpdate(
        id = cover.id,
        mdate = Dates.dateTimeFormatted(LocalDateTime.now()),
        fromUrl = cover.fromUrl,
        fileId = rowId
      ))
      if (count == 0) throw DatabaseException("Comic cover not updated")

      return wrappedToResult(rowId)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun delCoverFile(data: JSONObject): JSONObject {
    try {
      val comicId = data.optLong("comicId")
      checkRowId(comicId)
      val cover = comicCoverDao.readByComic(comicId)
      deleteImage(cover.fileId)
      val count = comicCoverDao.update(ComicCoverUpdate(
        id = cover.id,
        mdate = Dates.dateTimeFormatted(LocalDateTime.now()),
        fromUrl = cover.fromUrl,
        fileId = 0,
      ))
      if (count == 0) throw DatabaseException("Comic cover not updated")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun getChaptersAll(data: JSONObject): JSONObject {
    try {
      val comicId = data.getLong("comicId")
      checkRowId(comicId)
      return wrappedToResult(
        chapterSerializer.toJSONArray(
          chapterDao.readWithPagesByComicAll(comicId)
        )
      )
    } catch (e: ValidationException) {
      return wrappedToError(e)
    }
  }

  fun getChapter(data: JSONObject): JSONObject {
    try {
      val rowId = data.optLong("id", 0)
      checkRowId(rowId)
      return wrappedToResult(
        chapterSerializer.toJSON(
          chapterDao.readWithPages(rowId)
        )
      )
    } catch (e: ValidationException) {
      return wrappedToError(e)
    }
  }

  fun addChapter(data: JSONObject): JSONObject {
    try {
      val rowId = chapterDao.create(chapterSerializer.createFromJSON(data))
      if (rowId == 0.toLong()) throw DatabaseException("Chapter not created")

      return wrappedToResult(rowId)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun setChapter(data: JSONObject): JSONObject {
    try {
      val count = chapterDao.update(chapterSerializer.updateFromJSON(data))
      if (count == 0) throw DatabaseException("Chapter not updated")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun delChapter(data: JSONObject): JSONObject {
    try {
      val id = data.getLong("id")
      checkRowId(id)
      deleteChapter(id)

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun getChapterPagesAll(data: JSONObject): JSONObject {
    try {
      val chapterId = data.getLong("chapterId")
      checkRowId(chapterId)
      return wrappedToResult(
        chapterPageSerializer.toJSONArray(
          chapterPageDao.readByChapterAll(chapterId)
        )
      )
    } catch (e: ValidationException) {
      return wrappedToError(e)
    }
  }

  fun getChapterPage(data: JSONObject): JSONObject {
    try {
      val rowId = data.optLong("id", 0)
      checkRowId(rowId)
      return wrappedToResult(
        chapterPageSerializer.toJSON(
          chapterPageDao.readWithFile(rowId)
        )
      )
    } catch (e: ValidationException) {
      return wrappedToError(e)
    }
  }

  fun addChapterPage(data: JSONObject): JSONObject {
    try {
      val rowId = chapterPageDao.create(chapterPageSerializer.createFromJSON(data))
      if (rowId == 0.toLong()) throw DatabaseException("Chapter page not created")

      return wrappedToResult(rowId)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun setChapterPage(data: JSONObject): JSONObject {
    try {
      val chapterPageId = data.getLong("id")
      checkRowId(chapterPageId)
      val chapterPage = chapterPageDao.read(chapterPageId)
      data.put("fileId", chapterPage.fileId)

      val count = chapterPageDao.update(chapterPageSerializer.updateFromJSON(data))
      if (count == 0) throw DatabaseException("Chapter not updated")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun delChapterPage(data: JSONObject): JSONObject {
    try {
      val id = data.getLong("id")
      checkRowId(id)
      deleteChapterPage(id)

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun addChapterPageFile(data: JSONObject): JSONObject {
    try {
      val chapterPageId = data.optLong("chapterPageId")
      checkRowId(chapterPageId, "ChapterPageId")
      val file = data.optString("file")
      if (file == "") throw ValidationException("File is empty")

      val chapterPage = chapterPageDao.read(chapterPageId)
      val chapter = chapterDao.readWithPages(chapterPage.chapterId)

      if (chapterPage.fileId != 0.toLong()) deleteImage(chapterPage.fileId)

      val rowId = createComicImage(chapter.chapter.comicId.toString(), "$chapterPageId.webp", file)
      if (rowId == 0.toLong()) throw DatabaseException("Chapter page file not added")

      val newPageFile = ChapterPageUpdate(
        id = chapterPage.id,
        mdate = Dates.dateTimeFormatted(LocalDateTime.now()),
        fromUrl = chapterPage.fromUrl,
        fileId = rowId,
        isRead = chapterPage.isRead,
      )

      val count = chapterPageDao.update(newPageFile)
      if (count == 0) throw DatabaseException("Chapter page not updated")

      return wrappedToResult(rowId)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  fun delChapterPageFile(data: JSONObject): JSONObject {
    try {
      val chapterPageId = data.optLong("chapterPageId")
      checkRowId(chapterPageId, "ChapterPageId")

      val page = chapterPageDao.read(chapterPageId)
      deleteImage(page.fileId)

      val count = chapterPageDao.update(ChapterPageUpdate(
        id = page.id,
        mdate = Dates.dateTimeFormatted(LocalDateTime.now()),
        fromUrl = page.fromUrl,
        fileId = 0,
        isRead = page.isRead,
      ))

      if (count == 0) throw DatabaseException("Chapter page not updated")

      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }

  // TODO delete with migration
  private fun fromArrayString(value: String): List<Long> {
    val result = arrayListOf<Long>()
    val data = JSONArray(value)

    for (index in 0 until data.length()) {
      result.add(data.getLong(index))
    }

    return result
  }

  fun addBackup(): JSONObject {
    dbBackup.backup(db)
    return wrappedToResult(true)
  }

  fun restoreBackup(data: JSONObject): JSONObject {
    try {
      val path = data.optString("path", "").trim()
      if (path == "") throw ValidationException("Path is empty")
      dbBackup.restore(db, path)
      return wrappedToResult(true)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    }
  }

  fun migrate(data: JSONObject) {
    val mapTags: MutableMap<Long, Long> = mutableMapOf()
    val tagsJSON = data.getJSONArray("tags")

    for (index in 0 until tagsJSON.length()) {
      val itemJSON = tagsJSON.getJSONObject(index)
      val itemId = tagDao.create(tagSerializer.createFromJSON(itemJSON))
      mapTags[itemJSON.getLong("id")] = itemId
    }

    val mapAuthors: MutableMap<Long, Long> = mutableMapOf()
    val authorsJSON = data.getJSONArray("authors")

    for (index in 0 until authorsJSON.length()) {
      val itemJSON = authorsJSON.getJSONObject(index)
      val itemId = authorDao.create(authorSerializer.createFromJSON(itemJSON))
      mapAuthors[itemJSON.getLong("id")] = itemId
    }

    val mapLanguages: MutableMap<Long, Long> = mutableMapOf()
    val languagesJSON = data.getJSONArray("languages")

    for (index in 0 until languagesJSON.length()) {
      val itemJSON = languagesJSON.getJSONObject(index)
      val itemId = languageDao.create(languageSerializer.createFromJSON(itemJSON))
      mapLanguages[itemJSON.getLong("id")] = itemId
    }

    val mapParsers: MutableMap<Long, Long> = mutableMapOf()
    val parsersJSON = data.getJSONArray("parsers")

    for (index in 0 until parsersJSON.length()) {
      val itemJSON = parsersJSON.getJSONObject(index)
      itemJSON.put("name", itemJSON.optString("name", ""))
      itemJSON.put("siteUrl", itemJSON.optString("site", ""))
      itemJSON.put("titleCSS", itemJSON.optString("title", ""))
      itemJSON.put("coverCSS", itemJSON.optString("image", ""))
      itemJSON.put("pagesCSS", itemJSON.optString("images", ""))
      itemJSON.put("authorsCSS", itemJSON.optString("authors", ""))
      itemJSON.put("authorsTextCSS", itemJSON.optString("authorsText", ""))
      itemJSON.put("languageCSS", itemJSON.optString("language", ""))
      itemJSON.put("tagsCSS", itemJSON.optString("tags", ""))
      itemJSON.put("tagsTextCSS", itemJSON.optString("tagsText", ""))
      val itemId = parserDao.create(parserSerializer.createFromJSON(itemJSON))
      mapParsers[itemJSON.getLong("id")] = itemId
    }

    val mapFiles: MutableMap<Long, Long> = mutableMapOf()
    val filesJSON = data.getJSONArray("files")

    for (index in 0 until filesJSON.length()) {
      val itemJSON = filesJSON.getJSONObject(index)
      val itemId = fileDao.create(fileSerializer.createFromJSON(itemJSON))
      mapFiles[itemJSON.getLong("id")] = itemId
    }

    val comicsJSON = data.getJSONArray("comics")

    for (comicIndex in 0 until comicsJSON.length()) {
      val comicJSON = comicsJSON.getJSONObject(comicIndex)

      val tags: List<Long> = fromArrayString(comicJSON.getString("tags"))
      val tagsSave = mutableListOf<Long>()
      for (item in tags) tagsSave.add(mapTags[item]!!)

      val authors: List<Long> = fromArrayString(comicJSON.getString("authors"))
      val authorsSave = mutableListOf<Long>()
      for (item in authors) authorsSave.add(mapAuthors[item]!!)

      val comicId = comicDao.create(ComicCreate(
        name = comicJSON.optString("name", ""),
        fromUrl = comicJSON.optString("url", ""),
        parserId = mapParsers[comicJSON.getLong("parser")],
        languageId = mapLanguages[comicJSON.getLong("language")],
        tags = tagsSave,
        authors = authorsSave,
      ))

      createComicDirectory(comicId.toString())

      val overrideJSON = comicJSON.getJSONObject("override")

      comicOverrideDao.create(ComicOverrideCreate(
        comicId = comicId,
        titleCSS = overrideJSON.optString("title"),
        coverCSS = overrideJSON.optString("image"),
        pagesCSS = overrideJSON.optString("images"),
        authorsCSS = overrideJSON.optString("authors"),
        authorsTextCSS = overrideJSON.optString("authorsText"),
        languageCSS = overrideJSON.optString("language"),
        tagsCSS = overrideJSON.optString("tags"),
        tagsTextCSS = overrideJSON.optString("tagsText"),
      ))

      val coverJSON = comicJSON.getJSONObject("image")

      val coverFileId = mapFiles[coverJSON.getLong("fileId")]
      comicCoverDao.create(ComicCoverCreate(
        comicId = comicId,
        fileId = coverFileId ?: 0,
        fromUrl = coverJSON.optString("url", ""),
      ))

      if (coverFileId != null && coverFileId != 0.toLong()) {
        val fileCover = fileDao.read(coverFileId)
        val path = "${AppDirectory.COMICS_IMAGES}/${comicId}/cover.webp"
        val fromFile = Path("${context.filesDir}/${fileCover.path}")
        val toFile = Path("${context.filesDir}/${path}")

        if (fromFile.exists()) {
          if (!toFile.exists()) Files.move(fromFile, toFile, StandardCopyOption.REPLACE_EXISTING)
          fileDao.update(AppFileUpdate(id = coverFileId, path = path))
        }
      }

      val chapterId = chapterDao.create(ChapterCreate(name = "", comicId = comicId))

      val pagesJSON = comicJSON.getJSONArray("images")

      for (pageIndex in 0 until pagesJSON.length()) {
        val pageJSON = pagesJSON.getJSONObject(pageIndex)

        val imageId = mapFiles[pageJSON.optLong("fileId", 0)]
        val chapterPageId = chapterPageDao.create(
          ChapterPageCreate(
            chapterId = chapterId,
            fileId = imageId ?: 0,
            fromUrl = pageJSON.optString("url", ""),
          )
        )

        if (imageId != null && imageId != 0.toLong()) {
          val pageFile = fileDao.read(imageId)
          val path = "${AppDirectory.COMICS_IMAGES}/${comicId}/${chapterPageId}.webp"
          val fromFile = Path("${context.filesDir}/${pageFile.path}")
          val toFile = Path("${context.filesDir}/${path}")

          if (fromFile.exists()) {
            if (!toFile.exists()) Files.move(fromFile, toFile, StandardCopyOption.REPLACE_EXISTING)
            fileDao.update(AppFileUpdate(id = imageId, path = path))
          }
        }
      }
    }

    val oldDir = File("${context.filesDir}/comics")
    if (oldDir.exists()) oldDir.delete()
  }
}