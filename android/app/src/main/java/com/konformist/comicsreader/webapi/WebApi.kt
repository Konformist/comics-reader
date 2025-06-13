package com.konformist.comicsreader.webapi

import android.util.Log
import androidx.core.net.toUri
import androidx.room.Room
import com.konformist.comicsreader.App
import com.konformist.comicsreader.AppBackup
import com.konformist.comicsreader.data.appfile.AppFileController
import com.konformist.comicsreader.data.author.Author
import com.konformist.comicsreader.data.author.AuthorController
import com.konformist.comicsreader.data.author.AuthorCreate
import com.konformist.comicsreader.data.author.AuthorDelete
import com.konformist.comicsreader.data.author.AuthorUpdate
import com.konformist.comicsreader.data.chapter.ChapterController
import com.konformist.comicsreader.data.chapter.ChapterCreate
import com.konformist.comicsreader.data.chapter.ChapterDelete
import com.konformist.comicsreader.data.chapter.ChapterUpdate
import com.konformist.comicsreader.data.chapter.ChapterUpdateComic
import com.konformist.comicsreader.data.chapterpage.ChapterPageController
import com.konformist.comicsreader.data.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.data.chapterpage.ChapterPageDelete
import com.konformist.comicsreader.data.chapterpage.ChapterPageUpdate
import com.konformist.comicsreader.data.comic.ComicController
import com.konformist.comicsreader.data.comic.ComicCreate
import com.konformist.comicsreader.data.comic.ComicDelete
import com.konformist.comicsreader.data.comic.ComicUpdate
import com.konformist.comicsreader.data.comiccover.ComicCoverController
import com.konformist.comicsreader.data.comiccover.ComicCoverUpdate
import com.konformist.comicsreader.data.comicoverride.ComicOverride
import com.konformist.comicsreader.data.comicoverride.ComicOverrideController
import com.konformist.comicsreader.data.comicoverride.ComicOverrideUpdate
import com.konformist.comicsreader.data.language.Language
import com.konformist.comicsreader.data.language.LanguageController
import com.konformist.comicsreader.data.language.LanguageCreate
import com.konformist.comicsreader.data.language.LanguageDelete
import com.konformist.comicsreader.data.language.LanguageUpdate
import com.konformist.comicsreader.data.parserconfig.ParserConfig
import com.konformist.comicsreader.data.parserconfig.ParserConfigController
import com.konformist.comicsreader.data.parserconfig.ParserConfigCreate
import com.konformist.comicsreader.data.parserconfig.ParserConfigDelete
import com.konformist.comicsreader.data.parserconfig.ParserConfigUpdate
import com.konformist.comicsreader.data.tag.Tag
import com.konformist.comicsreader.data.tag.TagController
import com.konformist.comicsreader.data.tag.TagCreate
import com.konformist.comicsreader.data.tag.TagDelete
import com.konformist.comicsreader.data.tag.TagUpdate
import com.konformist.comicsreader.db.AppDataStore
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.db.Settings
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.exceptions.FilesException
import com.konformist.comicsreader.exceptions.ValidationException
import com.konformist.comicsreader.parser.ParserController
import com.konformist.comicsreader.utils.FileManager
import com.konformist.comicsreader.webapi.serializers.ChapterPageSerializer
import com.konformist.comicsreader.webapi.serializers.ChapterSerializer
import com.konformist.comicsreader.webapi.serializers.ComicSerializer
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream

class WebApi {
  private val jsonDecode = Json { ignoreUnknownKeys = true }
  private val jsonEncode = Json { encodeDefaults = true }

  private val db: AppDatabase = Room
    .databaseBuilder(App.context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
    .build()

  private val dbBackup = AppBackup()

  private val tagController = TagController(db.tagDao())
  private val authorController = AuthorController(db.authorDao())
  private val languageController = LanguageController(db.languageDao())
  private val parserConfigController = ParserConfigController(db.parserDao())
  private val filesController = AppFileController(db.appFileDao())
  private val chapterPageController = ChapterPageController(
    db.chapterPageDao(),
    filesController,
  )
  private val chapterController = ChapterController(
    db.chapterDao(),
    chapterPageController,
  )
  private val comicCoverController = ComicCoverController(
    db.comicCoverDao(),
    filesController,
  )
  private val comicOverrideController = ComicOverrideController(db.comicOverrideDao())
  private val comicController = ComicController(
    db.comicDao(),
    comicCoverController,
    comicOverrideController,
    chapterController,
  )

  private val parserController = ParserController(
    tagController,
    authorController,
    languageController,
    comicController,
    comicCoverController,
    chapterController,
    chapterPageController,
  )

  fun <T : Exception> wrappedToError(value: T): String {
    return """{"error": "${value.message}"}"""
  }

  fun wrappedToResult(value: Any?): String {
    return """{"result": ${value}}"""
  }

  private fun getTagsAll(): String {
    return jsonEncode.encodeToString<List<Tag>>(tagController.readAll())
  }

  @Throws(ValidationException::class)
  private fun getTag(data: JSONObject): String {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return jsonEncode.encodeToString<Tag?>(tagController.read(rowId))
  }

  @Throws(DatabaseException::class)
  private fun addTag(data: JSONObject): Long {
    val decoded = jsonDecode.decodeFromString<TagCreate>(data.toString())
    return tagController.create(decoded)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setTag(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decoded = jsonDecode.decodeFromString<TagUpdate>(data.toString())
    return tagController.update(decoded)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delTag(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decoded = jsonDecode.decodeFromString<TagDelete>(data.toString())
    return tagController.delete(decoded)
  }

  private fun getAuthorsAll(): String {
    return jsonEncode.encodeToString<List<Author>>(authorController.readAll())
  }

  @Throws(ValidationException::class)
  private fun getAuthor(data: JSONObject): String {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return jsonEncode.encodeToString<Author?>(authorController.read(rowId))
  }

  @Throws(DatabaseException::class)
  private fun addAuthor(data: JSONObject): Long {
    val decoded = jsonDecode.decodeFromString<AuthorCreate>(data.toString())
    return authorController.create(decoded)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setAuthor(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decoded = jsonDecode.decodeFromString<AuthorUpdate>(data.toString())
    return authorController.update(decoded)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delAuthor(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decoded = jsonDecode.decodeFromString<AuthorDelete>(data.toString())
    return authorController.delete(decoded)
  }

  private fun getLanguagesAll(): String {
    return jsonEncode.encodeToString<List<Language>>(languageController.readAll())
  }

  @Throws(ValidationException::class)
  private fun getLanguage(data: JSONObject): String {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return jsonEncode.encodeToString<Language?>(languageController.read(rowId))
  }

  @Throws(DatabaseException::class)
  private fun addLanguage(data: JSONObject): Long {
    val decode = jsonDecode.decodeFromString<LanguageCreate>(data.toString())
    return languageController.create(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setLanguage(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonDecode.decodeFromString<LanguageUpdate>(data.toString())
    return languageController.update(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delLanguage(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonDecode.decodeFromString<LanguageDelete>(data.toString())
    return languageController.delete(decode)
  }

  private fun getParsersAll(): String {
    return jsonEncode.encodeToString<List<ParserConfig>>(parserConfigController.readAll())
  }

  @Throws(ValidationException::class)
  private fun getParser(data: JSONObject): String {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return jsonEncode.encodeToString<ParserConfig?>(parserConfigController.read(rowId))
  }

  @Throws(DatabaseException::class)
  private fun addParser(data: JSONObject): Long {
    val decode = jsonDecode.decodeFromString<ParserConfigCreate>(data.toString())
    return parserConfigController.create(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setParser(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonDecode.decodeFromString<ParserConfigUpdate>(data.toString())
    return parserConfigController.update(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delParser(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonDecode.decodeFromString<ParserConfigDelete>(data.toString())
    return parserConfigController.delete(decode)
  }

  private fun getComicsAll(): JSONArray {
    return ComicSerializer.toJSONArray(comicController.readLiteAll())
  }

  @Throws(ValidationException::class)
  private fun getComic(data: JSONObject): JSONObject? {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return ComicSerializer.toJSON(comicController.readLite(rowId))
  }

  @Throws(DatabaseException::class)
  private fun addComic(data: JSONObject): Long {
    val comic = jsonDecode.decodeFromString<ComicCreate>(data.toString())
    return comicController.create(comic)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setComic(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val coverJSON = data.optJSONObject("cover")
    if (coverJSON != null) setCover(coverJSON)

    return comicController.update(jsonDecode.decodeFromString<ComicUpdate>(data.toString()))
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delComic(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    return comicController.delete(jsonDecode.decodeFromString<ComicDelete>(data.toString()))
  }

  @Throws(ValidationException::class)
  private fun parseComic(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")
    val cookie = data.optString("cookie", "")

    val comic = comicController.read(rowId) ?: return false
    if (comic.fromUrl.isBlank() || comic.parserId == 0L) return false

    val parserConfig = parserConfigController.read(comic.parserId) ?: return false
    val override = comicOverrideController.readByComic(comic.id) ?: return false

    val parsedData = parserController.parse(
      comic.fromUrl,
      cookie,
      parserConfig,
      override,
    )

    return parserController.saveData(rowId, parsedData)
  }

  private fun uploadComic(data: JSONObject): Boolean {
    val id = data.optLong("id")
    Validation.id(id, "id")

    return comicController.toArchive(id)
  }

  @Throws(ValidationException::class, FilesException::class)
  private fun addComicArchive(data: JSONObject): Long {
    val uriStr = data.optString("uri")
    Validation.uri(uriStr, "uri")

    return comicController.fromArchive(uriStr)
  }

  @Throws(ValidationException::class)
  private fun getComicOverride(data: JSONObject): String? {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")
    return jsonEncode.encodeToString<ComicOverride?>(comicOverrideController.readByComic(comicId))
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setComicOverride(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonDecode.decodeFromString<ComicOverrideUpdate>(data.toString())
    return comicOverrideController.update(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setCover(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonDecode.decodeFromString<ComicCoverUpdate>(data.toString())
    return comicCoverController.update(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun downloadCoverFile(data: JSONObject): Long {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    val link = data.optString("link", "")
    Validation.link(link, "link")

    val cookie = data.optString("cookie", "")

    return comicCoverController.createFileFromRequest(comicId, link, cookie)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun addCoverFile(data: JSONObject): Long {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    val uriStr = data.optString("uri")
    Validation.uri(uriStr, "uri")

    return comicCoverController.createFileFromUri(comicId, uriStr)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delCoverFile(data: JSONObject): Boolean {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    val cover = comicCoverController.readByComic(comicId) ?: return false
    return comicCoverController.deleteFile(cover)
  }

  @Throws(ValidationException::class)
  private fun getChaptersAll(data: JSONObject): JSONArray {
    val comicId = data.getLong("comicId")
    Validation.id(comicId, "comicId")

    return ChapterSerializer.toJSONArray(chapterController.readByComicAll(comicId))
  }

  @Throws(ValidationException::class)
  private fun getChapter(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return ChapterSerializer.toJSON(chapterController.readWithPages(rowId))
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun addChapter(data: JSONObject): Long {
    val decode = jsonDecode.decodeFromString<ChapterCreate>(data.toString())
    return chapterController.create(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setChapter(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonDecode.decodeFromString<ChapterUpdate>(data.toString())
    return chapterController.update(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setChapterComic(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    val decode = jsonDecode.decodeFromString<ChapterUpdateComic>(data.toString())
    return chapterController.updateComic(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delChapter(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val decode = jsonDecode.decodeFromString<ChapterDelete>(data.toString())
    return chapterController.delete(decode)
  }

  @Throws(ValidationException::class)
  private fun getChapterPagesAll(data: JSONObject): JSONArray {
    val chapterId = data.getLong("chapterId")
    Validation.id(chapterId, "chapterId")

    return ChapterPageSerializer.toJSONArray(chapterPageController.readByChapterAll(chapterId))
  }

  @Throws(ValidationException::class)
  private fun getChapterPage(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return ChapterPageSerializer.toJSON(chapterPageController.readWithFile(rowId))
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun addChapterPage(data: JSONObject): Long {
    val decode = jsonDecode.decodeFromString<ChapterPageCreate>(data.toString())
    return chapterPageController.create(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setChapterPage(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val decode = jsonDecode.decodeFromString<ChapterPageUpdate>(data.toString())
    return chapterPageController.update(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delChapterPage(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val decode = jsonDecode.decodeFromString<ChapterPageDelete>(data.toString())
    return chapterPageController.delete(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun downloadChapterPageFile(data: JSONObject): Long {
    val id = data.optLong("chapterPageId")
    Validation.id(id, "chapterPageId")

    val link = data.optString("link", "")
    Validation.link(link, "link")

    val cookie = data.optString("cookie", "")

    return chapterPageController.createFileFromRequest(id, link, cookie)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun addChapterPageFile(data: JSONObject): Long {
    val id = data.optLong("chapterPageId")
    Validation.id(id, "chapterPageId")

    val uriStr = data.optString("uri")
    Validation.uri(uriStr, "uri")

    return chapterPageController.createFileFromUri(id, uriStr)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delChapterPageFile(data: JSONObject): Boolean {
    val chapterPageId = data.optLong("chapterPageId")
    Validation.id(chapterPageId, "chapterPageId")

    val page = chapterPageController.read(chapterPageId) ?: return false
    return chapterPageController.deleteFile(page)
  }

  private fun getSettings(): String {
    val encode = runBlocking {
      AppDataStore.readStore()
      jsonEncode.encodeToString<Settings>(AppDataStore.settings)
    }

    return encode
  }

  private fun setSettings(data: JSONObject): Boolean {
    val readingMode = data.optString("readingMode")
    Validation.string(readingMode, "readingMode")
    Validation.contain(readingMode, AppDataStore.readingModeList, "readingMode")

    return runBlocking {
      AppDataStore.settings = jsonDecode.decodeFromString<Settings>(data.toString())
      AppDataStore.saveStore()
    }
  }

  private fun addBackup(): Boolean {
    return dbBackup.backup(db)
  }

  private fun restoreBackup(data: JSONObject): Boolean {
    val uriStr = data.optString("uri")
    Validation.uri(uriStr, "uri")

    val path = App.context.contentResolver
      .openFileDescriptor(uriStr.toUri(), "r") ?: return false
    val fileInputStream = FileInputStream(path.fileDescriptor)

    val result = dbBackup.restore(db, fileInputStream)
    fileInputStream.close()
    path.close()

    return result
  }

  private fun getFilesTree(): JSONArray {
    val tree = JSONArray()
    tree.put(FileManager.tree(FileManager.filesDir))
    tree.put(FileManager.tree(FileManager.cacheDir))
    tree.put(FileManager.tree(FileManager.documentsDir))
    tree.put(FileManager.tree(FileManager.downloadsDir))
    return tree
  }

  private fun setFileToDownloads(data: JSONObject): Boolean {
    val file = data.optString("file", "")
    Validation.string(file, "file")

    val fileName = data.optString("fileName", "")
    Validation.string(fileName, "fileName")

    val filePath = File(FileManager.downloadsAppDir, fileName)
    val result = FileManager.write(filePath, file)
    Validation.fileCreate(result, filePath)
    return true
  }

  private fun migrate(): Boolean {
    return true
  }

  fun api(query: String, data: JSONObject? = JSONObject()): String {
    try {
      // Если data null, выбрасываем исключение
      data ?: throw ValidationException("Body is null")

      // Создаем Map с ассоциациями между запросами и методами
      val actions: Map<String, (JSONObject) -> Any?> = mapOf(
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
        Query.COMIC_COMIC_PARSE to { parseComic(data) },
        Query.COMIC_COMIC_UPLOAD to { uploadComic(data) },
        Query.COMIC_ARCHIVE_ADD to { addComicArchive(data) },
        Query.COMIC_OVERRIDE_GET to { getComicOverride(data) },
        Query.COMIC_OVERRIDE_SET to { setComicOverride(data) },
        Query.CHAPTER_CHAPTER_LIST to { getChaptersAll(data) },
        Query.CHAPTER_CHAPTER_GET to { getChapter(data) },
        Query.CHAPTER_CHAPTER_ADD to { addChapter(data) },
        Query.CHAPTER_CHAPTER_SET to { setChapter(data) },
        Query.CHAPTER_COMIC_SET to { setChapterComic(data) },
        Query.CHAPTER_CHAPTER_DEL to { delChapter(data) },
        Query.CHAPTER_PAGE_LIST to { getChapterPagesAll(data) },
        Query.CHAPTER_PAGE_GET to { getChapterPage(data) },
        Query.CHAPTER_PAGE_ADD to { addChapterPage(data) },
        Query.CHAPTER_PAGE_SET to { setChapterPage(data) },
        Query.CHAPTER_PAGE_DEL to { delChapterPage(data) },
        Query.FILE_COMIC_COVER_DOWNLOAD to { downloadCoverFile(data) },
        Query.FILE_COMIC_COVER_ADD to { addCoverFile(data) },
        Query.FILE_COMIC_COVER_DEL to { delCoverFile(data) },
        Query.FILE_CHAPTER_PAGE_DOWNLOAD to { downloadChapterPageFile(data) },
        Query.FILE_CHAPTER_PAGE_ADD to { addChapterPageFile(data) },
        Query.FILE_CHAPTER_PAGE_DEL to { delChapterPageFile(data) },
        Query.FILE_FILES_TREE to { getFilesTree() },
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
