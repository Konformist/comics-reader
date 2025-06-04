package com.konformist.comicsreader.webapi

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.room.Room
import com.konformist.comicsreader.AppBackup
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.db.appfile.AppFileCreate
import com.konformist.comicsreader.db.appfile.AppFileDelete
import com.konformist.comicsreader.db.author.Author
import com.konformist.comicsreader.db.author.AuthorCreate
import com.konformist.comicsreader.db.author.AuthorDelete
import com.konformist.comicsreader.db.author.AuthorUpdate
import com.konformist.comicsreader.db.chapter.ChapterCreate
import com.konformist.comicsreader.db.chapter.ChapterDelete
import com.konformist.comicsreader.db.chapter.ChapterUpdate
import com.konformist.comicsreader.db.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.db.chapterpage.ChapterPageDelete
import com.konformist.comicsreader.db.chapterpage.ChapterPageUpdate
import com.konformist.comicsreader.db.comic.ComicCreate
import com.konformist.comicsreader.db.comic.ComicDelete
import com.konformist.comicsreader.db.comic.ComicUpdate
import com.konformist.comicsreader.db.comiccover.ComicCoverCreate
import com.konformist.comicsreader.db.comiccover.ComicCoverDelete
import com.konformist.comicsreader.db.comiccover.ComicCoverUpdate
import com.konformist.comicsreader.db.comicoverride.ComicOverride
import com.konformist.comicsreader.db.comicoverride.ComicOverrideCreate
import com.konformist.comicsreader.db.comicoverride.ComicOverrideDelete
import com.konformist.comicsreader.db.comicoverride.ComicOverrideUpdate
import com.konformist.comicsreader.db.language.Language
import com.konformist.comicsreader.db.language.LanguageCreate
import com.konformist.comicsreader.db.language.LanguageDelete
import com.konformist.comicsreader.db.language.LanguageUpdate
import com.konformist.comicsreader.db.parser.Parser
import com.konformist.comicsreader.db.parser.ParserCreate
import com.konformist.comicsreader.db.parser.ParserDelete
import com.konformist.comicsreader.db.parser.ParserUpdate
import com.konformist.comicsreader.db.tag.Tag
import com.konformist.comicsreader.db.tag.TagCreate
import com.konformist.comicsreader.db.tag.TagDelete
import com.konformist.comicsreader.db.tag.TagUpdate
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.exceptions.FilesException
import com.konformist.comicsreader.exceptions.ValidationException
import com.konformist.comicsreader.utils.AppDirectory
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.utils.FileUtils
import com.konformist.comicsreader.utils.ImageUtils
import com.konformist.comicsreader.webapi.serializers.ChapterPageSerializer
import com.konformist.comicsreader.webapi.serializers.ChapterSerializer
import com.konformist.comicsreader.webapi.serializers.ComicSerializer
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

class WebApi(private val context: Context) {
  private val db: AppDatabase = Room
    .databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
    .build()

  private val downloads =
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
  private val documents =
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

  private fun getAppName(): String {
    val applicationInfo = context.applicationInfo
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString()
    else context.getString(stringId)
  }

  private val dbBackup = AppBackup(getAppName(), context)

  private val jsonIgnore = Json { ignoreUnknownKeys = true }

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

  private fun <T : Exception> wrappedToError(value: T): JSONObject {
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
    val dirOut =
      File("${context.filesDir}${File.separator}${AppDirectory.COMICS_IMAGES}${File.separator}${path}")
    if (dirOut.exists()) return
    val result = dirOut.mkdirs()
    if (!result) throw FilesException("Directory not created ${AppDirectory.COMICS_IMAGES}${File.separator}${path}")
  }

  @Throws(FilesException::class)
  private fun deleteComicDirectory(path: String) {
    val dirOut =
      File("${context.filesDir}${File.separator}${AppDirectory.COMICS_IMAGES}${File.separator}${path}")
    if (dirOut.exists()) return
    val result = dirOut.delete()
    if (!result) throw FilesException("Directory not deleted ${AppDirectory.COMICS_IMAGES}${File.separator}${path}")
  }

  private fun getTagsAll(): JSONArray {
    return JSONArray(Json.encodeToString<List<Tag>>(tagDao.readAll()))
  }

  private fun getTag(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    return JSONObject(Json.encodeToString<Tag>(tagDao.read(rowId)))
  }

  private fun addTag(data: JSONObject): Long {
    val rowId = tagDao.create(jsonIgnore.decodeFromString<TagCreate>(data.toString()))
    if (rowId == 0.toLong()) throw DatabaseException("Tag not created")
    return rowId
  }

  private fun setTag(data: JSONObject): Boolean {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    data.put("mdate", DatesUtils.nowFormatted())
    val count = tagDao.update(jsonIgnore.decodeFromString<TagUpdate>(data.toString()))
    if (count == 0) throw DatabaseException("Tag not updated")
    return true
  }

  private fun delTag(data: JSONObject): Boolean {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    val count = tagDao.delete(jsonIgnore.decodeFromString<TagDelete>(data.toString()))
    if (count == 0) throw DatabaseException("Tag not deleted")
    return true
  }

  private fun getAuthorsAll(): JSONArray {
    return JSONArray(Json.encodeToString<List<Author>>(authorDao.readAll()))
  }

  private fun getAuthor(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    return JSONObject(Json.encodeToString<Author>(authorDao.read(rowId)))
  }

  private fun addAuthor(data: JSONObject): Long {
    val rowId = authorDao.create(jsonIgnore.decodeFromString<AuthorCreate>(data.toString()))
    if (rowId == 0.toLong()) throw DatabaseException("Author not created")

    return rowId
  }

  private fun setAuthor(data: JSONObject): Boolean {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    data.put("mdate", DatesUtils.nowFormatted())
    val count = authorDao.update(jsonIgnore.decodeFromString<AuthorUpdate>(data.toString()))
    if (count == 0) throw DatabaseException("Author not updated")
    return true
  }

  private fun delAuthor(data: JSONObject): Boolean {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    val count = authorDao.delete(jsonIgnore.decodeFromString<AuthorDelete>(data.toString()))
    if (count == 0) throw DatabaseException("Author not deleted")

    return true
  }

  private fun getLanguagesAll(): JSONArray {
    return JSONArray(Json.encodeToString<List<Language>>(languageDao.readAll()))
  }

  private fun getLanguage(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    return JSONObject(Json.encodeToString<Language>(languageDao.read(rowId)))
  }

  private fun addLanguage(data: JSONObject): Long {
    val rowId = languageDao.create(jsonIgnore.decodeFromString<LanguageCreate>(data.toString()))
    if (rowId == 0.toLong()) throw DatabaseException("Language not created")

    return rowId
  }

  private fun setLanguage(data: JSONObject): Boolean {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    data.put("mdate", DatesUtils.nowFormatted())
    val count = languageDao.update(jsonIgnore.decodeFromString<LanguageUpdate>(data.toString()))
    if (count == 0) throw DatabaseException("Language not updated")

    return true
  }

  private fun delLanguage(data: JSONObject): Boolean {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    val count = languageDao.delete(jsonIgnore.decodeFromString<LanguageDelete>(data.toString()))
    if (count == 0) throw DatabaseException("Language not deleted")

    return true
  }

  private fun getParsersAll(): JSONArray {
    return JSONArray(Json.encodeToString<List<Parser>>(parserDao.readAll()))
  }

  private fun getParser(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    return JSONObject(Json.encodeToString<Parser>(parserDao.read(rowId)))
  }

  private fun addParser(data: JSONObject): Long {
    val rowId = parserDao.create(jsonIgnore.decodeFromString<ParserCreate>(data.toString()))
    if (rowId == 0.toLong()) throw DatabaseException("Parser not created")

    return rowId
  }

  private fun setParser(data: JSONObject): Boolean {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    data.put("mdate", DatesUtils.nowFormatted())
    val count = parserDao.update(jsonIgnore.decodeFromString<ParserUpdate>(data.toString()))
    if (count == 0) throw DatabaseException("Parser not updated")

    return true
  }

  private fun delParser(data: JSONObject): Boolean {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    val count = parserDao.delete(jsonIgnore.decodeFromString<ParserDelete>(data.toString()))
    if (count == 0) throw DatabaseException("Parser not deleted")

    return true
  }

  private fun getComicsAll(): JSONArray {
    return ComicSerializer.toJSONArray(comicDao.readLiteAll(), context.filesDir)
  }

  private fun getComic(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    return ComicSerializer.toJSON(comicDao.readLite(rowId), context.filesDir)
  }

  private fun addComic(data: JSONObject): Long {
    val rowId = comicDao.create(jsonIgnore.decodeFromString<ComicCreate>(data.toString()))
    if (rowId == 0.toLong()) throw DatabaseException("Comic not created")

    createCover(rowId)
    createComicOverride(rowId)
    createComicDirectory(rowId.toString())

    return rowId
  }

  private fun setCover(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    checkRowId(rowId)

    val cover = comicCoverDao.read(rowId)
    data.put("mdate", DatesUtils.nowFormatted())
    data.put("fileId", cover.fileId)
    updateCover(jsonIgnore.decodeFromString<ComicCoverUpdate>(data.toString()))
    return true
  }

  private fun setComic(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    checkRowId(rowId)

    val coverJSON = data.optJSONObject("cover")
    if (coverJSON != null) setCover(coverJSON)

    data.put("mdate", DatesUtils.nowFormatted())
    val count = comicDao.update(jsonIgnore.decodeFromString<ComicUpdate>(data.toString()))
    if (count == 0) throw DatabaseException("Comic not updated")

    return true
  }

  private fun delComic(data: JSONObject): Boolean {
    val id = data.getLong("id")
    checkRowId(id)

    deleteCover(id)
    deleteComicOverride(id)

    val chapters = chapterDao.readByComicAll(id)
    chapters.forEach { chapter -> deleteChapter(chapter.id) }
    deleteComicDirectory(id.toString())

    val count = comicDao.delete(jsonIgnore.decodeFromString<ComicDelete>(data.toString()))
    if (count == 0) throw DatabaseException("Comic not deleted")

    return true
  }

  private fun getComicOverride(data: JSONObject): JSONObject {
    val rowId = data.optLong("comicId", 0)
    checkRowId(rowId)

    return JSONObject(Json.encodeToString<ComicOverride>(comicOverrideDao.readByComic(rowId)))
  }

  private fun setComicOverride(data: JSONObject): Boolean {
    val rowId = data.optLong("comicId", 0)
    checkRowId(rowId)

    data.put("mdate", DatesUtils.nowFormatted())
    val count =
      comicOverrideDao.update(jsonIgnore.decodeFromString<ComicOverrideUpdate>(data.toString()))
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
        mdate = DatesUtils.nowFormatted(),
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
        mdate = DatesUtils.nowFormatted(),
        fromUrl = cover.fromUrl,
        fileId = 0,
      )
    )
    if (count == 0) throw DatabaseException("Comic cover not updated")

    return true
  }

  private fun getChaptersAll(data: JSONObject): JSONArray {
    val comicId = data.getLong("comicId")
    checkRowId(comicId, "comicId")

    return ChapterSerializer.toJSONArray(
      chapterDao.readWithPagesByComicAll(comicId),
      context.filesDir,
    )
  }

  private fun getChapter(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    return ChapterSerializer.toJSON(
      chapterDao.readWithPages(rowId),
      context.filesDir,
    )
  }

  private fun addChapter(data: JSONObject): Long {
    val rowId = chapterDao.create(jsonIgnore.decodeFromString<ChapterCreate>(data.toString()))
    if (rowId == 0.toLong()) throw DatabaseException("Chapter not created")

    return rowId
  }

  private fun setChapter(data: JSONObject): Boolean {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    data.put("mdate", DatesUtils.nowFormatted())
    val count = chapterDao.update(jsonIgnore.decodeFromString<ChapterUpdate>(data.toString()))
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

    return ChapterPageSerializer.toJSONArray(
      chapterPageDao.readByChapterAll(chapterId),
      context.filesDir
    )
  }

  private fun getChapterPage(data: JSONObject): JSONObject {
    val rowId = data.optLong("id", 0)
    checkRowId(rowId)

    return ChapterPageSerializer.toJSON(
      chapterPageDao.readWithFile(rowId),
      context.filesDir
    )
  }

  private fun addChapterPage(data: JSONObject): Long {
    val rowId =
      chapterPageDao.create(jsonIgnore.decodeFromString<ChapterPageCreate>(data.toString()))
    if (rowId == 0.toLong()) throw DatabaseException("Chapter page not created")

    return rowId
  }

  private fun setChapterPage(data: JSONObject): Boolean {
    val chapterPageId = data.getLong("id")
    checkRowId(chapterPageId)

    val chapterPage = chapterPageDao.read(chapterPageId)
    data.put("fileId", chapterPage.fileId)
    data.put("mdate", DatesUtils.nowFormatted())

    val count =
      chapterPageDao.update(jsonIgnore.decodeFromString<ChapterPageUpdate>(data.toString()))
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
      mdate = DatesUtils.nowFormatted(),
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
        mdate = DatesUtils.nowFormatted(),
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
    return dbBackup.backup(db)
  }

  private fun restoreBackup(data: JSONObject): Boolean {
    val uriStr = data.optString("uri", "")
    if (uriStr.isBlank()) throw ValidationException("Uri is empty")
    val path = context.contentResolver
      .openFileDescriptor(uriStr.toUri(), "r") ?: return false

    return dbBackup.restore(db, FileInputStream(path.fileDescriptor))
  }

  // @TODO Deprecated
  private fun getBackupsTree(): JSONObject {
    return FileUtils.tree(File("${context.filesDir}${File.separator}${AppDirectory.BACKUPS}"))
  }

  @OptIn(ExperimentalPathApi::class)
  private fun migrate(): Boolean {
    val dirFrom = File("${context.filesDir}${File.separator}${AppDirectory.BACKUPS}")
    if (dirFrom.exists()) {
      val dirTo =
        File("${documents.path}${File.separator}${getAppName()}${File.separator}${AppDirectory.BACKUPS}")

      if (!dirTo.exists()) dirTo.mkdirs()
      dirFrom.copyRecursively(target = dirTo, overwrite = true)
      dirFrom.toPath().deleteRecursively()
    }

    return true
  }

  private fun setFileToDownloads(data: JSONObject): Boolean {
    val file = data.optString("file", "")
    val fileName = data.optString("fileName", "")

    if (file == "") throw ValidationException("File is empty")
    if (fileName == "") throw ValidationException("FileName is empty")

    val filePath =
      File("${downloads.path}${File.separator}${getAppName()}${File.separator}${fileName}")

    val result = FileUtils.write(filePath, file)
    if (!result) throw FilesException("File not written")
    return true
  }

  fun api(query: String, data: JSONObject? = JSONObject()): JSONObject {
    try {
      // Если data null, выбрасываем исключение
      data ?: throw ValidationException("Body is null")

      // Создаем Map с ассоциациями между запросами и методами
      val actions: Map<String, (JSONObject) -> Any> = mapOf(
        Query.TAG_TAG_LIST to { getTagsAll() },
        Query.TAG_TAG_GET to { getTag(data) },
        Query.TAG_TAG_ADD to { addTag(data) },
        Query.TAG_TAG_SET to { setTag(data) },
        Query.TAG_TAG_DEL to { delTag(data) },
        Query.AUTHOR_AUTHOR_LIST to { getAuthorsAll() },
        Query.AUTHOR_AUTHOR_GET to { getAuthor(data) },
        Query.AUTHOR_AUTHOR_ADD to { addAuthor(data) },
        Query.AUTHOR_AUTHOR_SET to { setAuthor(data) },
        Query.AUTHOR_AUTHOR_DEL to { delAuthor(data) },
        Query.LANGUAGE_LANGUAGE_LIST to { getLanguagesAll() },
        Query.LANGUAGE_LANGUAGE_GET to { getLanguage(data) },
        Query.LANGUAGE_LANGUAGE_ADD to { addLanguage(data) },
        Query.LANGUAGE_LANGUAGE_SET to { setLanguage(data) },
        Query.LANGUAGE_LANGUAGE_DEL to { delLanguage(data) },
        Query.PARSER_PARSER_LIST to { getParsersAll() },
        Query.PARSER_PARSER_GET to { getParser(data) },
        Query.PARSER_PARSER_ADD to { addParser(data) },
        Query.PARSER_PARSER_SET to { setParser(data) },
        Query.PARSER_PARSER_DEL to { delParser(data) },
        Query.COMIC_COMIC_LIST to { getComicsAll() },
        Query.COMIC_COMIC_GET to { getComic(data) },
        Query.COMIC_COMIC_ADD to { addComic(data) },
        Query.COMIC_COMIC_SET to { setComic(data) },
        Query.COMIC_COMIC_DEL to { delComic(data) },
        Query.COMIC_OVERRIDE_GET to { getComicOverride(data) },
        Query.COMIC_OVERRIDE_SET to { setComicOverride(data) },
        Query.CHAPTER_CHAPTER_LIST to { getChaptersAll(data) },
        Query.CHAPTER_CHAPTER_GET to { getChapter(data) },
        Query.CHAPTER_CHAPTER_ADD to { addChapter(data) },
        Query.CHAPTER_CHAPTER_SET to { setChapter(data) },
        Query.CHAPTER_CHAPTER_DEL to { delChapter(data) },
        Query.CHAPTER_PAGE_LIST to { getChapterPagesAll(data) },
        Query.CHAPTER_PAGE_GET to { getChapterPage(data) },
        Query.CHAPTER_PAGE_ADD to { addChapterPage(data) },
        Query.CHAPTER_PAGE_SET to { setChapterPage(data) },
        Query.CHAPTER_PAGE_DEL to { delChapterPage(data) },
        Query.FILE_COMIC_COVER_ADD to { addCoverFile(data) },
        Query.FILE_COMIC_COVER_DEL to { delCoverFile(data) },
        Query.FILE_CHAPTER_PAGE_ADD to { addChapterPageFile(data) },
        Query.FILE_CHAPTER_PAGE_DEL to { delChapterPageFile(data) },
        Query.FILE_COMICS_IMAGES_TREE to { getComicImagesTree() },
        Query.FILE_BACKUPS_TREE to { getBackupsTree() },
        Query.FILE_FILE_DOWNLOADS to { setFileToDownloads(data) },
        Query.SETTINGS_SETTINGS_GET to { getSettings() },
        Query.SETTINGS_SETTINGS_SET to { setSettings(data) },
        Query.BACKUP_BACKUP_ADD to { addBackup() },
        Query.BACKUP_BACKUP_RESTORE to { restoreBackup(data) },
        Query.DATA_DATA_MIGRATE to { migrate() }
      )

      // Выполнение нужной операции, если запрос найден в map
      val action = actions[query] ?: throw Exception("Not implemented")
      return wrappedToResult(action.invoke(data))
    } catch (e: Exception) {
      Log.d("Api Error", e.toString())
      return wrappedToError(e)
    }
  }
}
