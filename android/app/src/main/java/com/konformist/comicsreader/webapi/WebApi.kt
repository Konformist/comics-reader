package com.konformist.comicsreader.webapi

import android.util.Log
import androidx.core.net.toUri
import androidx.room.Room
import com.konformist.comicsreader.App
import com.konformist.comicsreader.AppBackup
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.db.author.Author
import com.konformist.comicsreader.db.author.AuthorCreate
import com.konformist.comicsreader.db.author.AuthorDelete
import com.konformist.comicsreader.db.author.AuthorUpdate
import com.konformist.comicsreader.db.chapter.ChapterCreate
import com.konformist.comicsreader.db.chapter.ChapterUpdate
import com.konformist.comicsreader.db.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.db.chapterpage.ChapterPageUpdate
import com.konformist.comicsreader.db.comic.ComicCreate
import com.konformist.comicsreader.db.comic.ComicDelete
import com.konformist.comicsreader.db.comic.ComicUpdate
import com.konformist.comicsreader.db.comiccover.ComicCoverUpdate
import com.konformist.comicsreader.db.comicoverride.ComicOverride
import com.konformist.comicsreader.db.comicoverride.ComicOverrideUpdate
import com.konformist.comicsreader.db.language.Language
import com.konformist.comicsreader.db.language.LanguageCreate
import com.konformist.comicsreader.db.language.LanguageDelete
import com.konformist.comicsreader.db.language.LanguageUpdate
import com.konformist.comicsreader.db.parser.ParserConfig
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
import com.konformist.comicsreader.utils.FileManager
import com.konformist.comicsreader.webapi.controllers.AppFilesController
import com.konformist.comicsreader.webapi.controllers.AuthorController
import com.konformist.comicsreader.webapi.controllers.ChapterController
import com.konformist.comicsreader.webapi.controllers.ComicController
import com.konformist.comicsreader.webapi.controllers.LanguageController
import com.konformist.comicsreader.webapi.controllers.ParserController
import com.konformist.comicsreader.webapi.controllers.TagController
import com.konformist.comicsreader.webapi.serializers.ChapterPageSerializer
import com.konformist.comicsreader.webapi.serializers.ChapterSerializer
import com.konformist.comicsreader.webapi.serializers.ComicSerializer
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class WebApi {
  private val jsonIgnore = Json { ignoreUnknownKeys = true }
  private val db: AppDatabase = Room
    .databaseBuilder(App.context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
    .build()

  private val dbBackup = AppBackup()

  private val tagController = TagController(db.tagDao())
  private val authorController = AuthorController(db.authorDao())
  private val languageController = LanguageController(db.languageDao())
  private val parserController = ParserController(db.parserDao())
  private val filesController = AppFilesController(db.appFileDao())
  private val chapterController = ChapterController(
    db.chapterDao(),
    db.chapterPageDao(),
    filesController,
  )
  private val comicController = ComicController(
    db.comicCoverDao(),
    db.comicOverrideDao(),
    db.comicDao(),
    filesController,
    chapterController,
  )
  private val archiveComic = ArchiveComic(
    comicController,
    chapterController,
  )

  fun <T : Exception> wrappedToError(value: T): String {
    return """{"error": "${value.message}"}"""
  }

  fun wrappedToResult(value: Any?): String {
    return """{"result": ${value}}"""
  }

  private fun getTagsAll(): String {
    return Json.encodeToString<List<Tag>>(tagController.readAll())
  }

  @Throws(ValidationException::class)
  private fun getTag(data: JSONObject): String {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return Json.encodeToString<Tag>(tagController.read(rowId))
  }

  @Throws(DatabaseException::class)
  private fun addTag(data: JSONObject): Long {
    val decoded = jsonIgnore.decodeFromString<TagCreate>(data.toString())
    return tagController.create(decoded)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setTag(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decoded = jsonIgnore.decodeFromString<TagUpdate>(data.toString())
    return tagController.update(decoded)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delTag(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decoded = jsonIgnore.decodeFromString<TagDelete>(data.toString())
    return tagController.delete(decoded)
  }

  private fun getAuthorsAll(): String {
    return Json.encodeToString<List<Author>>(authorController.readAll())
  }

  @Throws(ValidationException::class)
  private fun getAuthor(data: JSONObject): String {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return Json.encodeToString<Author>(authorController.read(rowId))
  }

  @Throws(DatabaseException::class)
  private fun addAuthor(data: JSONObject): Long {
    val decoded = jsonIgnore.decodeFromString<AuthorCreate>(data.toString())
    return authorController.create(decoded)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setAuthor(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decoded = jsonIgnore.decodeFromString<AuthorUpdate>(data.toString())
    return authorController.update(decoded)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delAuthor(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decoded = jsonIgnore.decodeFromString<AuthorDelete>(data.toString())
    return authorController.delete(decoded)
  }

  private fun getLanguagesAll(): String {
    return Json.encodeToString<List<Language>>(languageController.readAll())
  }

  @Throws(ValidationException::class)
  private fun getLanguage(data: JSONObject): String {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return Json.encodeToString<Language>(languageController.read(rowId))
  }

  @Throws(DatabaseException::class)
  private fun addLanguage(data: JSONObject): Long {
    val decode = jsonIgnore.decodeFromString<LanguageCreate>(data.toString())
    return languageController.create(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setLanguage(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonIgnore.decodeFromString<LanguageUpdate>(data.toString())
    return languageController.update(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delLanguage(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonIgnore.decodeFromString<LanguageDelete>(data.toString())
    return languageController.delete(decode)
  }

  private fun getParsersAll(): String {
    return Json.encodeToString<List<ParserConfig>>(parserController.readAll())
  }

  @Throws(ValidationException::class)
  private fun getParser(data: JSONObject): String {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return Json.encodeToString<ParserConfig>(parserController.read(rowId))
  }

  @Throws(DatabaseException::class)
  private fun addParser(data: JSONObject): Long {
    val decode = jsonIgnore.decodeFromString<ParserCreate>(data.toString())
    return parserController.create(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setParser(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonIgnore.decodeFromString<ParserUpdate>(data.toString())
    return parserController.update(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delParser(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonIgnore.decodeFromString<ParserDelete>(data.toString())
    return parserController.delete(decode)
  }

  private fun getComicsAll(): JSONArray {
    return ComicSerializer.toJSONArray(comicController.readLiteAll())
  }

  @Throws(ValidationException::class)
  private fun getComic(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return ComicSerializer.toJSON(comicController.readLite(rowId))
  }

  @Throws(DatabaseException::class)
  private fun addComic(data: JSONObject): Long {
    val comic = jsonIgnore.decodeFromString<ComicCreate>(data.toString())
    return comicController.create(comic)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setComic(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val coverJSON = data.optJSONObject("cover")
    if (coverJSON != null) setCover(coverJSON)

    return comicController.update(jsonIgnore.decodeFromString<ComicUpdate>(data.toString()))
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delComic(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    return comicController.delete(jsonIgnore.decodeFromString<ComicDelete>(data.toString()))
  }

  private fun uploadComic(data: JSONObject): Boolean {
    val id = data.optLong("id")
    Validation.id(id, "id")

    return archiveComic.uploadComic(id)
  }

  @Throws(ValidationException::class, FilesException::class)
  private fun addComicArchive(data: JSONObject): Long {
    val uriStr = data.optString("uri")
    Validation.uri(uriStr, "uri")

    return archiveComic.addComicFromArchive(uriStr)
  }

  @Throws(ValidationException::class)
  private fun getComicOverride(data: JSONObject): String? {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    return when (val row = comicController.readOverrideByComic(comicId)) {
      null -> null
      else -> Json.encodeToString<ComicOverride>(row)
    }
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setComicOverride(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonIgnore.decodeFromString<ComicOverrideUpdate>(data.toString())
    return comicController.updateOverride(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setCover(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val cover = comicController.readCover(rowId) ?: return false
    val decode = jsonIgnore.decodeFromString<ComicCoverUpdate>(data.toString())
    decode.fileId = cover.fileId
    return comicController.updateCover(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun downloadCoverFile(data: JSONObject): Long {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    val link = data.optString("link", "")
    Validation.link(link, "link")

    val cookie = data.optString("cookie", "")
    val connection = getConnection(URL(link), cookie)

    // Получаем MIME тип из заголовков
    val mimeType = connection.contentType

    val cover = comicController.readCoverByComic(comicId) ?: return 0L
    val result = comicController.createCoverFile(cover, mimeType, connection.inputStream)

    connection.inputStream.close()
    connection.disconnect()

    return result
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun addCoverFile(data: JSONObject): Long {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    val uriStr = data.optString("uri")
    Validation.uri(uriStr, "uri")

    val extension = FileManager.getFileExtension(uriStr)
    val path = App.context.contentResolver.openFileDescriptor(uriStr.toUri(), "r") ?: return 0L
    val inputStream = FileInputStream(path.fileDescriptor)

    val cover = comicController.readCoverByComic(comicId) ?: return 0L
    val result = comicController.createCoverFile(
      cover,
      FileManager.getMimeFromExtension(extension),
      inputStream,
    )

    inputStream.close()
    path.close()

    return result
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delCoverFile(data: JSONObject): Boolean {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    val cover = comicController.readCoverByComic(comicId) ?: return false
    return comicController.deleteCoverFile(cover)
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
    val decode = jsonIgnore.decodeFromString<ChapterCreate>(data.toString())
    return chapterController.create(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setChapter(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val decode = jsonIgnore.decodeFromString<ChapterUpdate>(data.toString())
    return chapterController.update(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delChapter(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val chapter = chapterController.read(rowId)
    return chapterController.delete(chapter)
  }

  @Throws(ValidationException::class)
  private fun getChapterPagesAll(data: JSONObject): JSONArray {
    val chapterId = data.getLong("chapterId")
    Validation.id(chapterId, "chapterId")

    return ChapterPageSerializer.toJSONArray(chapterController.readPageByChapterAll(chapterId))
  }

  @Throws(ValidationException::class)
  private fun getChapterPage(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return ChapterPageSerializer.toJSON(chapterController.readPageWithFile(rowId))
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun addChapterPage(data: JSONObject): Long {
    val decode = jsonIgnore.decodeFromString<ChapterPageCreate>(data.toString())
    return chapterController.createPage(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setChapterPage(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val row = chapterController.readPage(rowId)
    val decode = jsonIgnore.decodeFromString<ChapterPageUpdate>(data.toString())
    decode.fileId = row.fileId
    return chapterController.updatePage(decode)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delChapterPage(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val row = chapterController.readPage(rowId)
    return chapterController.deletePage(row)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun downloadChapterPageFile(data: JSONObject): Long {
    val chapterPageId = data.optLong("chapterPageId")
    Validation.id(chapterPageId, "chapterPageId")

    val link = data.optString("link", "")
    Validation.link(link, "link")

    val cookie = data.optString("cookie", "")
    val connection = getConnection(URL(link), cookie)

    // Получаем MIME тип из заголовков
    val mimeType = connection.contentType

    val result = chapterController.createPageFile(
      chapterController.readPage(chapterPageId),
      mimeType,
      connection.inputStream,
    )

    connection.inputStream.close()
    connection.disconnect()

    return result
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun addChapterPageFile(data: JSONObject): Long {
    val chapterPageId = data.optLong("chapterPageId")
    Validation.id(chapterPageId, "chapterPageId")

    val uriStr = data.optString("uri")
    Validation.uri(uriStr, "uri")

    val lastDotIndex = uriStr.lastIndexOf('.')
    val extension = if (lastDotIndex == -1 || lastDotIndex >= uriStr.length - 1) ""
    else uriStr.substring(lastDotIndex + 1) // No extension found

    val path = App.context.contentResolver.openFileDescriptor(uriStr.toUri(), "r") ?: return 0L
    val fileInputStream = FileInputStream(path.fileDescriptor)

    val result = chapterController.createPageFile(
      chapterController.readPage(chapterPageId),
      FileManager.getMimeFromExtension(extension),
      fileInputStream,
    )

    fileInputStream.close()
    path.close()

    return result
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delChapterPageFile(data: JSONObject): Boolean {
    val chapterPageId = data.optLong("chapterPageId")
    Validation.id(chapterPageId, "chapterPageId")

    val page = chapterController.readPage(chapterPageId)
    return chapterController.deletePageFile(page)
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
    val readingMode = data.optString("readingMode")

    Validation.string(readingMode, "readingMode")
    Validation.contain(readingMode, AppDataStore.readingModeList, "readingMode")

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

  private fun getConnection(url: URL, cookie: String? = ""): HttpURLConnection {
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    if (!cookie.isNullOrBlank()) connection.setRequestProperty("Cookie", cookie)

    connection.setRequestProperty(
      "User-Agent",
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36"
    )
    connection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
    connection.setRequestProperty("Priority", "u=0, i")
    connection.setRequestProperty("Referer", "${url.protocol}://${url.host}")
    connection.setRequestProperty(
      "sec-ch-ua",
      "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\""
    )
    connection.setRequestProperty("sec-ch-ua-arch", "\"x86\"")
    connection.setRequestProperty("sec-ch-ua-bitness", "\"64\"")
    connection.setRequestProperty("sec-ch-ua-full-version", "\"136.0.7103.114\"")
    connection.setRequestProperty(
      "sec-ch-ua-full-version-list",
      "\"Chromium\";v=\"136.0.7103.114\", \"Google Chrome\";v=\"136.0.7103.114\", \"Not.A/Brand\";v=\"99.0.0.0\""
    )
    connection.setRequestProperty("sec-ch-ua-mobile", "?0")
    connection.setRequestProperty("sec-ch-ua-model", "\"\"")
    connection.setRequestProperty("sec-ch-ua-platform", "\"Windows\"")
    connection.setRequestProperty("sec-ch-ua-platform-version", "\"19.0.0\"")
    connection.setRequestProperty("sec-fetch-dest", "document")
    connection.setRequestProperty("sec-fetch-mode", "navigate")
    connection.setRequestProperty("sec-fetch-site", "same-origin")
    connection.setRequestProperty("sec-fetch-user", "?1")
    connection.setRequestProperty("upgrade-insecure-requests", "1")
    connection.connect()

    if (connection.responseCode != 200)
      throw Exception("Error connection: ${connection.responseCode}")

    return connection
  }

  private fun downloadHTML(data: JSONObject): String {
    val url = data.optString("url", "")
    Validation.link(url, "url")

    val cookie = data.optString("cookie", "")
    val connection = getConnection(URL(url), cookie)

    // Читаем входной поток
    val inputStream = connection.inputStream
    val inputStreamReader = InputStreamReader(inputStream)
    val reader = BufferedReader(inputStreamReader)
    val stringBuilder = StringBuilder()

    var line: String?
    while (reader.readLine().also { line = it } != null) {
      stringBuilder.append(line)
    }

    reader.close()
    inputStreamReader.close()
    inputStream.close()
    connection.inputStream.close()
    connection.disconnect()

    // @TODO Fuck
    return "\"${stringBuilder.replace(Regex("""""""), "\\\\\\\"")}\""
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
        Query.COMIC_COMIC_UPLOAD to { uploadComic(data) },
        Query.COMIC_ARCHIVE_ADD to { addComicArchive(data) },
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
        Query.PARSER_HTML_DOWNLOAD to { downloadHTML(data) },
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
