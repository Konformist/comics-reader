package com.konformist.comicsreader.webapi

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.room.Room
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.db.DBBackup
import com.konformist.comicsreader.db.appfile.AppFileCreate
import com.konformist.comicsreader.db.appfile.AppFileDelete
import com.konformist.comicsreader.db.chapter.ChapterDelete
import com.konformist.comicsreader.db.chapterpage.ChapterPageDelete
import com.konformist.comicsreader.db.chapterpage.ChapterPageUpdate
import com.konformist.comicsreader.db.comiccover.ComicCoverCreate
import com.konformist.comicsreader.db.comiccover.ComicCoverDelete
import com.konformist.comicsreader.db.comiccover.ComicCoverUpdate
import com.konformist.comicsreader.db.comicoverride.ComicOverrideCreate
import com.konformist.comicsreader.db.comicoverride.ComicOverrideDelete
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.exceptions.FilesException
import com.konformist.comicsreader.exceptions.ValidationException
import com.konformist.comicsreader.utils.AppDirectory
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.utils.FileUtils
import com.konformist.comicsreader.utils.ImageUtils
import com.konformist.comicsreader.webapi.serializers.AuthorSerializer
import com.konformist.comicsreader.webapi.serializers.ChapterPageSerializer
import com.konformist.comicsreader.webapi.serializers.ChapterSerializer
import com.konformist.comicsreader.webapi.serializers.ComicCoverSerializer
import com.konformist.comicsreader.webapi.serializers.ComicOverrideSerializer
import com.konformist.comicsreader.webapi.serializers.ComicSerializer
import com.konformist.comicsreader.webapi.serializers.LanguageSerializer
import com.konformist.comicsreader.webapi.serializers.ParserSerializer
import com.konformist.comicsreader.webapi.serializers.TagSerializer
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDateTime
import android.util.Base64
import java.io.BufferedOutputStream
import java.io.FileOutputStream

class WebApi(private val context: Context) {
  private val db: AppDatabase = Room
    .databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
    .build()

  private val dbBackup = DBBackup(context)

  private val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

  private fun getAppName(): String {
    val applicationInfo = context.applicationInfo
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString()
    else context.getString(stringId)
  }

  private val tagDao = db.tagDao()
  private val authorDao = db.authorDao()
  private val languageDao = db.languageDao()
  private val parserDao = db.parserDao()
  private val appFileDao = db.appFileDao()
  private val comicOverrideDao = db.comicOverrideDao()
  private val comicCoverDao = db.comicCoverDao()
  private val chapterDao = db.chapterDao()
  private val chapterPageDao = db.chapterPageDao()
  private val comicDao = db.comicDao()

  private val tagSerializer = TagSerializer()
  private val authorSerializer = AuthorSerializer()
  private val languageSerializer = LanguageSerializer()
  private val parserSerializer = ParserSerializer()
  private val chapterSerializer = ChapterSerializer(context.filesDir)
  private val chapterPageSerializer = ChapterPageSerializer(context.filesDir)
  private val comicCoverSerializer = ComicCoverSerializer(context.filesDir)
  private val comicOverrideSerializer = ComicOverrideSerializer()
  private val comicSerializer = ComicSerializer(context.filesDir)

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
    val rowId = comicOverrideDao.create(
      ComicOverrideCreate(
        comicId = comicId,
        titleCSS = "",
        coverCSS = "",
        pagesCSS = "",
        authorsCSS = "",
        authorsTextCSS = "",
        languageCSS = "",
        tagsCSS = "",
        tagsTextCSS = "",
      )
    )
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
    val rowId = comicCoverDao.create(
      ComicCoverCreate(
        comicId = comicId,
        fileId = 0,
        fromUrl = "",
      )
    )
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
  private fun createComicImage(
    directory: String,
    name: String,
    file: String,
  ): Long {
    val dirOut = File("${context.filesDir}/${AppDirectory.COMICS_IMAGES}/${directory}")
    val fileOut = File("${dirOut.path}/${name}")

    try {
      if (file.trim() == "") throw ValidationException("File is empty")

      if (!dirOut.exists()) dirOut.mkdirs()

      if (AppDataStore.settings.isCompress) {
        val decodedImage: Bitmap = ImageUtils.base64ToBitmap(file)
        ImageUtils.write(fileOut, decodedImage, 80)
      } else {
        FileUtils.writeBase64(fileOut, file)
      }

      val rowId = appFileDao.create(
        AppFileCreate(
          name = name,
          mime = "image/webp",
          size = fileOut.length(),
          path = "${AppDirectory.COMICS_IMAGES}/$directory/$name",
        )
      )

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
    val row = appFileDao.read(id)

    val fileOut = File("${context.filesDir}/${row.path}")
    if (fileOut.exists()) fileOut.delete()

    val count = appFileDao.delete(AppFileDelete(id = row.id))
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

  private fun getTagsAll(): JSONArray {
    return tagSerializer.toJSONArray(tagDao.readAll())
  }

  private fun getTag(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)
    return tagSerializer.toJSON(tagDao.read(rowId))
  }

  private fun addTag(data: JSONObject): Long {
    val rowId = tagDao.create(tagSerializer.createFromJSON(data))
    if (rowId == 0.toLong()) throw DatabaseException("Tag not created")
    return rowId
  }

  private fun setTag(data: JSONObject): Boolean {
    val count = tagDao.update(tagSerializer.updateFromJSON(data))
    if (count == 0) throw DatabaseException("Tag not updated")
    return true
  }

  private fun delTag(data: JSONObject): Boolean {
    val count = tagDao.delete(tagSerializer.deleteFromJSON(data))
    if (count == 0) throw DatabaseException("Tag not deleted")
    return true
  }

  private fun getAuthorsAll(): JSONArray {
    return authorSerializer.toJSONArray(authorDao.readAll())
  }

  private fun getAuthor(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    return authorSerializer.toJSON(authorDao.read(rowId))
  }

  private fun addAuthor(data: JSONObject): Long {
    val rowId = authorDao.create(authorSerializer.createFromJSON(data))
    if (rowId == 0.toLong()) throw DatabaseException("Author not created")

    return rowId
  }

  private fun setAuthor(data: JSONObject): Boolean {
    val count = authorDao.update(authorSerializer.updateFromJSON(data))
    if (count == 0) throw DatabaseException("Author not updated")
    return true
  }

  private fun delAuthor(data: JSONObject): Boolean {
    val count = authorDao.delete(authorSerializer.deleteFromJSON(data))
    if (count == 0) throw DatabaseException("Author not deleted")

    return true
  }

  private fun getLanguagesAll(): JSONArray {
    return languageSerializer.toJSONArray(languageDao.readAll())
  }

  private fun getLanguage(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    return languageSerializer.toJSON(languageDao.read(rowId))
  }

  private fun addLanguage(data: JSONObject): Long {
    val rowId = languageDao.create(languageSerializer.createFromJSON(data))
    if (rowId == 0.toLong()) throw DatabaseException("Language not created")

    return rowId
  }

  private fun setLanguage(data: JSONObject): Boolean {
    val count = languageDao.update(languageSerializer.updateFromJSON(data))
    if (count == 0) throw DatabaseException("Language not updated")

    return true
  }

  private fun delLanguage(data: JSONObject): Boolean {
    val count = languageDao.delete(languageSerializer.deleteFromJSON(data))
    if (count == 0) throw DatabaseException("Language not deleted")

    return true
  }

  private fun getParsersAll(): JSONArray {
    return parserSerializer.toJSONArray(parserDao.readAll())
  }

  private fun getParser(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    return parserSerializer.toJSON(parserDao.read(rowId))
  }

  private fun addParser(data: JSONObject): Long {
    val rowId = parserDao.create(parserSerializer.createFromJSON(data))
    if (rowId == 0.toLong()) throw DatabaseException("Parser not created")

    return rowId
  }

  private fun setParser(data: JSONObject): Boolean {
    val count = parserDao.update(parserSerializer.updateFromJSON(data))
    if (count == 0) throw DatabaseException("Parser not updated")

    return true
  }

  private fun delParser(data: JSONObject): Boolean {
    val count = parserDao.delete(parserSerializer.deleteFromJSON(data))
    if (count == 0) throw DatabaseException("Parser not deleted")

    return true
  }

  private fun getComicsAll(): JSONArray {
    return comicSerializer.toJSONArray(comicDao.readLiteAll())
  }

  private fun getComic(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    return comicSerializer.toJSON(comicDao.readLite(rowId))
  }

  private fun addComic(data: JSONObject): Long {
    val rowId = comicDao.create(comicSerializer.createFromJSON(data))
    if (rowId == 0.toLong()) throw DatabaseException("Comic not created")

    createCover(rowId)
    createComicOverride(rowId)
    createComicDirectory(rowId.toString())

    return rowId
  }

  private fun setComic(data: JSONObject): Boolean {
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

    return true
  }

  private fun delComic(data: JSONObject): Boolean {
    val id = data.getLong("id")
    checkRowId(id)

    deleteCover(id)
    deleteComicOverride(id)

    val chapters = chapterDao.readByComicAll(id)
    for (chapter in chapters) deleteChapter(chapter.id)
    deleteComicDirectory(id.toString())

    val count = comicDao.delete(comicSerializer.deleteFromJSON(data))
    if (count == 0) throw DatabaseException("Comic not deleted")

    return true
  }

  private fun getComicOverride(data: JSONObject): JSONObject {
    val rowId = data.optLong("comicId", 0)
    checkRowId(rowId)

    return comicOverrideSerializer.toJSON(comicOverrideDao.readByComic(rowId))
  }

  private fun setComicOverride(data: JSONObject): Boolean {
    val count = comicOverrideDao.update(comicOverrideSerializer.updateFromJSON(data))
    if (count == 0) throw DatabaseException("Comic override not updated")

    return true
  }

  private fun addCoverFile(data: JSONObject): Long {
    val comicId = data.optLong("comicId")
    checkRowId(comicId)
    val file = data.optString("file")
    if (file == "") throw ValidationException("File is empty")

    val cover = comicCoverDao.readByComic(comicId)

    if (cover.fileId != null && cover.fileId != 0.toLong())
      deleteImage(cover.fileId)

    val rowId = createComicImage(comicId.toString(), "cover.webp", file)
    if (rowId == 0.toLong()) throw DatabaseException("Comic cover file not added")

    val count = comicCoverDao.update(
      ComicCoverUpdate(
        id = cover.id,
        mdate = DatesUtils.dateTimeFormatted(LocalDateTime.now()),
        fromUrl = cover.fromUrl,
        fileId = rowId
      )
    )
    if (count == 0) throw DatabaseException("Comic cover not updated")

    return rowId
  }

  private fun delCoverFile(data: JSONObject): Boolean {
    val comicId = data.optLong("comicId")
    checkRowId(comicId)
    val cover = comicCoverDao.readByComic(comicId)
    deleteImage(cover.fileId)
    val count = comicCoverDao.update(
      ComicCoverUpdate(
        id = cover.id,
        mdate = DatesUtils.dateTimeFormatted(LocalDateTime.now()),
        fromUrl = cover.fromUrl,
        fileId = 0,
      )
    )
    if (count == 0) throw DatabaseException("Comic cover not updated")

    return true
  }

  private fun getChaptersAll(data: JSONObject): JSONArray {
    val comicId = data.getLong("comicId")
    checkRowId(comicId)
    return chapterSerializer.toJSONArray(
      chapterDao.readWithPagesByComicAll(comicId)
    )
  }

  private fun getChapter(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)
    return chapterSerializer.toJSON(
      chapterDao.readWithPages(rowId)
    )
  }

  private fun addChapter(data: JSONObject): Long {
    val rowId = chapterDao.create(chapterSerializer.createFromJSON(data))
    if (rowId == 0.toLong()) throw DatabaseException("Chapter not created")

    return rowId
  }

  private fun setChapter(data: JSONObject): Boolean {
    val count = chapterDao.update(chapterSerializer.updateFromJSON(data))
    if (count == 0) throw DatabaseException("Chapter not updated")

    return true
  }

  private fun delChapter(data: JSONObject): Boolean {
    val id = data.getLong("id")
    checkRowId(id)
    deleteChapter(id)

    return true
  }

  private fun getChapterPagesAll(data: JSONObject): JSONArray {
    val chapterId = data.getLong("chapterId")
    checkRowId(chapterId)
    return chapterPageSerializer.toJSONArray(
      chapterPageDao.readByChapterAll(chapterId)
    )
  }

  private fun getChapterPage(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)
    return chapterPageSerializer.toJSON(
      chapterPageDao.readWithFile(rowId)
    )
  }

  private fun addChapterPage(data: JSONObject): Long {
    val rowId = chapterPageDao.create(chapterPageSerializer.createFromJSON(data))
    if (rowId == 0.toLong()) throw DatabaseException("Chapter page not created")

    return rowId
  }

  private fun setChapterPage(data: JSONObject): Boolean {
    val chapterPageId = data.getLong("id")
    checkRowId(chapterPageId)
    val chapterPage = chapterPageDao.read(chapterPageId)
    data.put("fileId", chapterPage.fileId)

    val count = chapterPageDao.update(chapterPageSerializer.updateFromJSON(data))
    if (count == 0) throw DatabaseException("Chapter not updated")

    return true
  }

  private fun delChapterPage(data: JSONObject): Boolean {
    val id = data.getLong("id")
    checkRowId(id)
    deleteChapterPage(id)

    return true
  }

  private fun addChapterPageFile(data: JSONObject): Long {
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
      mdate = DatesUtils.dateTimeFormatted(LocalDateTime.now()),
      fromUrl = chapterPage.fromUrl,
      fileId = rowId,
      isRead = chapterPage.isRead,
    )

    val count = chapterPageDao.update(newPageFile)
    if (count == 0) throw DatabaseException("Chapter page not updated")

    return rowId
  }

  private fun delChapterPageFile(data: JSONObject): Boolean {
    val chapterPageId = data.optLong("chapterPageId")
    checkRowId(chapterPageId, "ChapterPageId")

    val page = chapterPageDao.read(chapterPageId)
    deleteImage(page.fileId)

    val count = chapterPageDao.update(
      ChapterPageUpdate(
        id = page.id,
        mdate = DatesUtils.dateTimeFormatted(LocalDateTime.now()),
        fromUrl = page.fromUrl,
        fileId = 0,
        isRead = page.isRead,
      )
    )

    if (count == 0) throw DatabaseException("Chapter page not updated")

    return true
  }

  private fun getComicImagesTree(): JSONObject {
    return FileUtils.tree(File("${context.filesDir}${File.separator}${AppDirectory.COMICS_IMAGES}"))
  }

  private fun getSettings(): JSONObject {
    runBlocking { AppDataStore.readStore() }
    val result = JSONObject()
    result.put("autoReading", AppDataStore.settings.autoReading)
    result.put("autoReadingTimeout", AppDataStore.settings.autoReadingTimeout)
    result.put("readingMode", AppDataStore.settings.readingMode)
    result.put("isCompress", AppDataStore.settings.isCompress)
    return result
  }

  private fun setSettings(data: JSONObject): Boolean {
    val readingMode = data.optString("readingMode", "")

    if (!AppDataStore.readingModeList.contains(readingMode))
      throw ValidationException("ReadingMode invalid, use ${AppDataStore.readingModeList}")

    AppDataStore.settings.autoReading = data.optBoolean("autoReading", false)
    AppDataStore.settings.autoReadingTimeout = data.optInt("autoReadingTimeout", 0)
    AppDataStore.settings.readingMode = readingMode
    AppDataStore.settings.isCompress = data.optBoolean("isCompress", true)
    return runBlocking { AppDataStore.saveStore() }
  }

  private fun addBackup(): Boolean {
    dbBackup.backup(db)
    return true
  }

  private fun delBackup(data: JSONObject): Boolean {
    val fileName = data.optString("fileName", "")
    if (fileName == "") throw ValidationException("fileName is empty")
    val filePath =
      File("${context.filesDir}${File.separator}${AppDirectory.BACKUPS}${File.separator}${fileName}")
    if (!filePath.exists()) throw FilesException("File not found")
    filePath.delete()
    return true
  }

  private fun restoreBackup(data: JSONObject): Boolean {
    val fileName = data.optString("fileName", "").trim()
    if (fileName == "") throw ValidationException("fileName is empty")
    dbBackup.restore(db, fileName)
    return true
  }

  private fun getBackupsTree(): JSONObject {
    return FileUtils.tree(File("${context.filesDir}${File.separator}${AppDirectory.BACKUPS}"))
  }

  private fun setBackupsUpload(data: JSONObject): Boolean {
    val file = data.optString("file", "")
    val fileName = data.optString("fileName", "")

    if (file == "") throw ValidationException("File is empty")
    if (fileName == "") throw ValidationException("FileName is empty")

    val filePath = File("${context.filesDir}${File.separator}${AppDirectory.BACKUPS}${File.separator}${fileName}")

    return FileUtils.writeBase64(filePath, file)
  }

  private fun setBackupsToDownloads(data: JSONObject): Boolean {
    val fileName = data.optString("fileName", "")
    if (fileName == "") throw ValidationException("FileName is empty")

    val dirFrom = File("${context.filesDir}${File.separator}${AppDirectory.BACKUPS}${File.separator}${fileName}")
    val dirTo = File("${downloads.path}${File.separator}${getAppName()}${File.separator}${AppDirectory.BACKUPS}${File.separator}${fileName}")

    dirFrom.copyTo(target = dirTo, overwrite = true)

    return true
  }

  private fun setComicsImagesFromDownloads(): Boolean {
    val fileFrom = File("${downloads.path}${File.separator}${getAppName()}${File.separator}${AppDirectory.COMICS_IMAGES}")
    val fileTo = File("${context.filesDir}${File.separator}${AppDirectory.COMICS_IMAGES}")

    return fileFrom.copyRecursively(target = fileTo, overwrite = true)
  }

  private fun setComicsImagesToDownloads(): Boolean {
    val fileFrom = File("${context.filesDir}${File.separator}${AppDirectory.COMICS_IMAGES}")
    val fileTo = File("${downloads.path}${File.separator}${getAppName()}${File.separator}${AppDirectory.COMICS_IMAGES}")

    return fileFrom.copyRecursively(target = fileTo, overwrite = true)
  }

  private fun setFileToDownloads(data: JSONObject): Boolean {
    val file = data.optString("file", "")
    val fileName = data.optString("fileName", "")

    if (file == "") throw ValidationException("File is empty")
    if (fileName == "") throw ValidationException("FileName is empty")

    val filePath = File("${downloads.path}${File.separator}${getAppName()}${File.separator}${fileName}")

    val result = FileUtils.write(filePath, file)
    if (!result) throw FilesException("File not written")
    return true
  }

  fun api(query: String, data: JSONObject? = JSONObject()): JSONObject {
    try {
      if (data == null) throw ValidationException("Body is null")

      return wrappedToResult(
        when (query) {
          Query.TAG_TAG_LIST -> getTagsAll()
          Query.TAG_TAG_GET -> getTag(data)
          Query.TAG_TAG_ADD -> addTag(data)
          Query.TAG_TAG_SET -> setTag(data)
          Query.TAG_TAG_DEL -> delTag(data)
          Query.AUTHOR_AUTHOR_LIST -> getAuthorsAll()
          Query.AUTHOR_AUTHOR_GET -> getAuthor(data)
          Query.AUTHOR_AUTHOR_ADD -> addAuthor(data)
          Query.AUTHOR_AUTHOR_SET -> setAuthor(data)
          Query.AUTHOR_AUTHOR_DEL -> delAuthor(data)
          Query.LANGUAGE_LANGUAGE_LIST -> getLanguagesAll()
          Query.LANGUAGE_LANGUAGE_GET -> getLanguage(data)
          Query.LANGUAGE_LANGUAGE_ADD -> addLanguage(data)
          Query.LANGUAGE_LANGUAGE_SET -> setLanguage(data)
          Query.LANGUAGE_LANGUAGE_DEL -> delLanguage(data)
          Query.PARSER_PARSER_LIST -> getParsersAll()
          Query.PARSER_PARSER_GET -> getParser(data)
          Query.PARSER_PARSER_ADD -> addParser(data)
          Query.PARSER_PARSER_SET -> setParser(data)
          Query.PARSER_PARSER_DEL -> delParser(data)
          Query.COMIC_COMIC_LIST -> getComicsAll()
          Query.COMIC_COMIC_GET -> getComic(data)
          Query.COMIC_COMIC_ADD -> addComic(data)
          Query.COMIC_COMIC_SET -> setComic(data)
          Query.COMIC_COMIC_DEL -> delComic(data)
          Query.COMIC_OVERRIDE_GET -> getComicOverride(data)
          Query.COMIC_OVERRIDE_SET -> setComicOverride(data)
          Query.CHAPTER_CHAPTER_LIST -> getChaptersAll(data)
          Query.CHAPTER_CHAPTER_GET -> getChapter(data)
          Query.CHAPTER_CHAPTER_ADD -> addChapter(data)
          Query.CHAPTER_CHAPTER_SET -> setChapter(data)
          Query.CHAPTER_CHAPTER_DEL -> delChapter(data)
          Query.CHAPTER_PAGE_LIST -> getChapterPagesAll(data)
          Query.CHAPTER_PAGE_GET -> getChapterPage(data)
          Query.CHAPTER_PAGE_ADD -> addChapterPage(data)
          Query.CHAPTER_PAGE_SET -> setChapterPage(data)
          Query.CHAPTER_PAGE_DEL -> delChapterPage(data)
          Query.FILE_COMIC_COVER_ADD -> addCoverFile(data)
          Query.FILE_COMIC_COVER_DEL -> delCoverFile(data)
          Query.FILE_CHAPTER_PAGE_ADD -> addChapterPageFile(data)
          Query.FILE_CHAPTER_PAGE_DEL -> delChapterPageFile(data)
          Query.FILE_COMIC_IMAGES_TREE -> getComicImagesTree()
          Query.FILE_BACKUPS_TREE -> getBackupsTree()
          Query.FILE_FILE_DOWNLOADS -> setFileToDownloads(data)
          Query.FILE_BACKUPS_DOWNLOADS -> setBackupsToDownloads(data)
          Query.FILE_BACKUPS_UPLOAD -> setBackupsUpload(data)
          Query.FILE_COMICS_IMAGES_DOWNLOADS -> setComicsImagesToDownloads()
          Query.FILE_COMICS_IMAGES_UPLOAD -> setComicsImagesFromDownloads()
          Query.SETTINGS_SETTINGS_GET -> getSettings()
          Query.SETTINGS_SETTINGS_SET -> setSettings(data)
          Query.BACKUP_BACKUP_ADD -> addBackup()
          Query.BACKUP_BACKUP_DEL -> delBackup(data)
          Query.BACKUP_BACKUP_RESTORE -> restoreBackup(data)
          else -> throw Exception("Not implemented")
        }
      )
    } catch (e: Exception) {
      return wrappedToError(e)
    } catch (e: FilesException) {
      return wrappedToError(e)
    } catch (e: ValidationException) {
      return wrappedToError(e)
    } catch (e: DatabaseException) {
      return wrappedToError(e)
    }
  }
}
