package com.konformist.comicsreader.webapi

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.room.Room
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
import com.konformist.comicsreader.utils.archive.ArchiveFormat
import com.konformist.comicsreader.utils.archive.ArchiveUtils
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

class WebApi(private val context: Context) {
  private val jsonIgnore = Json { ignoreUnknownKeys = true }
  private val db: AppDatabase = Room
    .databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
    .build()

  private val comicsImagesDir =
    File("${context.filesDir}${File.separator}${AppDirectory.COMICS_IMAGES}")

  private val downloads =
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
  private val documents =
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

  private val downloadsApp = File("$downloads${File.separator}${getAppName()}")
  private val documentsApp = File("$documents${File.separator}${getAppName()}")

  private fun getAppName(): String {
    val applicationInfo = context.applicationInfo
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString()
    else context.getString(stringId)
  }

  private val dbBackup = AppBackup(documentsApp, comicsImagesDir, context)

  private val authorDao = db.authorDao()
  private val languageDao = db.languageDao()
  private val parserDao = db.parserDao()
  private val comicOverrideDao = db.comicOverrideDao()
  private val comicCoverDao = db.comicCoverDao()
  private val chapterDao = db.chapterDao()
  private val chapterPageDao = db.chapterPageDao()
  private val comicDao = db.comicDao()

  private val tagController = TagController(db.tagDao())

  private val filesController = AppFilesController(
    db.appFileDao(),
    comicsImagesDir,
  )
  private val chapterController = ChapterController(
    chapterDao,
    chapterPageDao,
    filesController,
  )
  private val comicController = ComicController(
    comicCoverDao,
    comicOverrideDao,
    comicDao,
    chapterDao,
    filesController,
    chapterController,
  )

  private fun <T : Exception> wrappedToError(value: T): JSONObject {
    return JSONObject()
      .put("error", value.message)
  }

  private fun wrappedToResult(value: Any): JSONObject {
    return JSONObject()
      .put("result", value)
  }

  private fun getTagsAll(): JSONArray {
    return JSONArray(
      Json.encodeToString<List<Tag>>(tagController.readAll())
    )
  }

  @Throws(ValidationException::class)
  private fun getTag(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return JSONObject(
      Json.encodeToString<Tag>(tagController.read(rowId))
    )
  }

  @Throws(DatabaseException::class)
  private fun addTag(data: JSONObject): Long {
    return tagController.create(
      jsonIgnore.decodeFromString<TagCreate>(data.toString())
    )
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setTag(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return tagController.update(
      jsonIgnore.decodeFromString<TagUpdate>(data.toString())
    )
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delTag(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return tagController.delete(
      jsonIgnore.decodeFromString<TagDelete>(data.toString())
    )
  }

  private fun getAuthorsAll(): JSONArray {
    return JSONArray(Json.encodeToString<List<Author>>(authorDao.readAll()))
  }

  private fun getAuthor(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return JSONObject(Json.encodeToString<Author>(authorDao.read(rowId)))
  }

  private fun addAuthor(data: JSONObject): Long {
    val rowId = authorDao.create(jsonIgnore.decodeFromString<AuthorCreate>(data.toString()))
    Validation.dbCreate(rowId, "Author")

    return rowId
  }

  private fun setAuthor(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    data.put("mdate", DatesUtils.nowFormatted())
    val count = authorDao.update(jsonIgnore.decodeFromString<AuthorUpdate>(data.toString()))
    Validation.dbUpdate(count, "Author")
    return true
  }

  private fun delAuthor(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val count = authorDao.delete(jsonIgnore.decodeFromString<AuthorDelete>(data.toString()))
    Validation.dbDelete(count, "Author")

    return true
  }

  private fun getLanguagesAll(): JSONArray {
    return JSONArray(Json.encodeToString<List<Language>>(languageDao.readAll()))
  }

  private fun getLanguage(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return JSONObject(Json.encodeToString<Language>(languageDao.read(rowId)))
  }

  private fun addLanguage(data: JSONObject): Long {
    val rowId = languageDao.create(jsonIgnore.decodeFromString<LanguageCreate>(data.toString()))
    Validation.dbCreate(rowId, "Language")

    return rowId
  }

  private fun setLanguage(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    data.put("mdate", DatesUtils.nowFormatted())
    val count = languageDao.update(jsonIgnore.decodeFromString<LanguageUpdate>(data.toString()))
    Validation.dbUpdate(count, "Language")

    return true
  }

  private fun delLanguage(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val count = languageDao.delete(jsonIgnore.decodeFromString<LanguageDelete>(data.toString()))
    Validation.dbDelete(count, "Language")

    return true
  }

  private fun getParsersAll(): JSONArray {
    return JSONArray(Json.encodeToString<List<Parser>>(parserDao.readAll()))
  }

  private fun getParser(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return JSONObject(Json.encodeToString<Parser>(parserDao.read(rowId)))
  }

  private fun addParser(data: JSONObject): Long {
    val rowId = parserDao.create(jsonIgnore.decodeFromString<ParserCreate>(data.toString()))
    Validation.dbCreate(rowId, "Parser")

    return rowId
  }

  private fun setParser(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    data.put("mdate", DatesUtils.nowFormatted())
    val count = parserDao.update(jsonIgnore.decodeFromString<ParserUpdate>(data.toString()))
    Validation.dbUpdate(count, "Parser")

    return true
  }

  private fun delParser(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val count = parserDao.delete(jsonIgnore.decodeFromString<ParserDelete>(data.toString()))
    Validation.dbDelete(count, "Parser")

    return true
  }

  private fun getComicsAll(): JSONArray {
    return ComicSerializer.toJSONArray(comicDao.readLiteAll())
  }

  private fun getComic(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return ComicSerializer.toJSON(comicDao.readLite(rowId))
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

    val dirTmp = File("${context.cacheDir}${File.separator}comic-tmp")
    if (dirTmp.exists()) dirTmp.deleteRecursively()
    dirTmp.mkdirs()

    val comic = comicDao.read(id)
    val chapters = chapterDao.readWithPagesByComicAll(id)

    val compress = ArchiveUtils.compressFactory()
    val chaptersLength = chapters.size.toString().length

    chapters.forEachIndexed { index, chapter ->
      val chapterDirName = String.format("%0${chaptersLength}d", index + 1)
      val chapterDir = File("$dirTmp${File.separator}$chapterDirName")
      chapterDir.mkdirs()
      compress.addFile(chapterDir)
      val pagesLength = chapter.pages.size.toString().length

      chapter.pages.forEachIndexed { iPage, page ->
        if (page.file != null) {
          val fileFrom = File(page.file.path)
          val fileToName = "${String.format("%0${pagesLength}d", iPage + 1)}.${fileFrom.extension}"
          val fileTo = File("$chapterDir${File.separator}$fileToName")
          fileFrom.copyTo(target = fileTo)
        }
      }
    }

    val outFile = File("$downloadsApp${File.separator}${comic.name}.${ComicController.FORMAT_CBZ}")
    val parent = outFile.parentFile ?: return false
    if (!parent.exists()) parent.mkdirs()
    compress.compress(outFile, ArchiveFormat.ZIP)
    dirTmp.deleteRecursively()

    return true
  }

  @Throws(ValidationException::class, FilesException::class)
  private fun addComicArchive(data: JSONObject): Long {
    val uriStr = data.optString("uri")
    Validation.uri(uriStr, "uri")

    val lastDotIndex = uriStr.lastIndexOf('.')
    val extension = if (lastDotIndex == -1 || lastDotIndex >= uriStr.length - 1) ""
    else uriStr.substring(lastDotIndex + 1) // No extension found

    if (extension.isBlank())
      throw FilesException("Unknown extension $extension")

    val archiveFormat = when (extension) {
      ComicController.FORMAT_CBZ -> ArchiveFormat.ZIP
      ComicController.FORMAT_ZIP -> ArchiveFormat.ZIP
      ComicController.FORMAT_CBT -> ArchiveFormat.TAR
      ComicController.FORMAT_TAR -> ArchiveFormat.TAR
      else -> throw FilesException("Unknown extension $extension")
    }

    val path = context.contentResolver.openFileDescriptor(uriStr.toUri(), "r") ?: return 0L
    val outStream = File("${context.cacheDir}${File.separator}comic-tmp")
    outStream.mkdirs()
    val inputStream = FileInputStream(path.fileDescriptor)

    val extractor = ArchiveUtils.extractFactory()
    extractor.extract(inputStream, archiveFormat, outStream)

    val comicId = comicController.create(
      ComicCreate(
        name = "New Comic",
        parserId = null,
        fromUrl = null,
        annotation = null,
        languageId = null,
        tags = null,
        authors = null,
      )
    )

    val listFiles = outStream.listFiles() ?: throw FilesException("No files")

    if (listFiles[0].isFile) {
      val chapterId = chapterController.create(
        ChapterCreate(name = "", comicId = comicId)
      )

      listFiles.forEach { file ->
        val pageId = chapterController.createPage(
          ChapterPageCreate(chapterId = chapterId)
        )
        val page = chapterPageDao.read(pageId)
        chapterController.createPageFile(
          page,
          FileUtils.getMimeFromExtension(file.extension),
          FileInputStream(file),
        )
      }
    } else {
      listFiles.forEach { directory ->
        val files = directory.listFiles()

        if (files != null) {
          val chapterId = chapterController.create(
            ChapterCreate(name = directory.name, comicId = comicId)
          )
          files.forEach { file ->
            val pageId = chapterController.createPage(
              ChapterPageCreate(chapterId = chapterId)
            )
            val page = chapterPageDao.read(pageId)
            chapterController.createPageFile(
              page,
              FileUtils.getMimeFromExtension(file.extension),
              FileInputStream(file),
            )
          }
        }
      }
    }

    return comicId
  }

  @Throws(ValidationException::class)
  private fun getComicOverride(data: JSONObject): JSONObject {
    val rowId = data.optLong("comicId")
    Validation.id(rowId, "comicId")

    val row = comicOverrideDao.readByComic(rowId)
    return if (row == null) JSONObject()
    else JSONObject(Json.encodeToString<ComicOverride>(row))
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setComicOverride(data: JSONObject): Boolean {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    return comicController.updateOverride(
      jsonIgnore.decodeFromString<ComicOverrideUpdate>(data.toString())
    )
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setCover(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val cover = comicCoverDao.read(rowId) ?: return false
    data.put("fileId", cover.fileId)

    return comicController.updateCover(
      jsonIgnore.decodeFromString<ComicCoverUpdate>(data.toString())
    )
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun downloadCoverFile(data: JSONObject): Long {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    val link = data.optString("link", "")
    Validation.link(link, "link")

    val connection = URL(link).openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.connect()

    if (connection.responseCode != 200)
      throw Exception("Error connection: ${connection.responseCode}")

    // Получаем MIME тип из заголовков
    val mimeType = connection.contentType

    val cover = comicCoverDao.readByComic(comicId) ?: return 0L
    val result = comicController.createCoverFile(
      cover,
      mimeType,
      connection.inputStream,
    )

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

    val lastDotIndex = uriStr.lastIndexOf('.')
    val extension = if (lastDotIndex == -1 || lastDotIndex >= uriStr.length - 1) ""
    else uriStr.substring(lastDotIndex + 1) // No extension found

    val path = context.contentResolver.openFileDescriptor(uriStr.toUri(), "r") ?: return 0L
    val inputStream = FileInputStream(path.fileDescriptor)

    val cover = comicCoverDao.readByComic(comicId) ?: return 0L
    val result = comicController.createCoverFile(
      cover,
      FileUtils.getMimeFromExtension(extension),
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

    val cover = comicCoverDao.readByComic(comicId) ?: return false
    return comicController.deleteCoverFile(cover)
  }

  @Throws(ValidationException::class)
  private fun getChaptersAll(data: JSONObject): JSONArray {
    val comicId = data.getLong("comicId")
    Validation.id(comicId, "comicId")

    return ChapterSerializer.toJSONArray(chapterDao.readWithPagesByComicAll(comicId))
  }

  @Throws(ValidationException::class)
  private fun getChapter(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return ChapterSerializer.toJSON(chapterDao.readWithPages(rowId))
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun addChapter(data: JSONObject): Long {
    return chapterController.create(
      jsonIgnore.decodeFromString<ChapterCreate>(data.toString())
    )
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setChapter(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return chapterController.update(
      jsonIgnore.decodeFromString<ChapterUpdate>(data.toString())
    )
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delChapter(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val chapter = chapterDao.read(rowId)
    return chapterController.delete(chapter)
  }

  @Throws(ValidationException::class)
  private fun getChapterPagesAll(data: JSONObject): JSONArray {
    val chapterId = data.getLong("chapterId")
    Validation.id(chapterId, "chapterId")

    return ChapterPageSerializer.toJSONArray(chapterPageDao.readByChapterAll(chapterId))
  }

  @Throws(ValidationException::class)
  private fun getChapterPage(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return ChapterPageSerializer.toJSON(chapterPageDao.readWithFile(rowId))
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun addChapterPage(data: JSONObject): Long {
    return chapterController.createPage(
      jsonIgnore.decodeFromString<ChapterPageCreate>(data.toString())
    )
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun setChapterPage(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val chapterPage = chapterPageDao.read(rowId)
    data.put("fileId", chapterPage.fileId)

    return chapterController.updatePage(
      jsonIgnore.decodeFromString<ChapterPageUpdate>(data.toString())
    )
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun delChapterPage(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val page = chapterPageDao.read(rowId)
    return chapterController.deletePage(page)
  }

  @Throws(ValidationException::class, DatabaseException::class)
  private fun downloadChapterPageFile(data: JSONObject): Long {
    val chapterPageId = data.optLong("chapterPageId")
    Validation.id(chapterPageId, "chapterPageId")

    val link = data.optString("link", "")
    Validation.link(link, "link")

    val connection = URL(link).openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.connect()

    if (connection.responseCode != 200)
      throw Exception("Error connection: ${connection.responseCode}")

    // Получаем MIME тип из заголовков
    val mimeType = connection.contentType

    val chapterPage = chapterPageDao.read(chapterPageId)
    val result = chapterController.createPageFile(
      chapterPage,
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

    val path = context.contentResolver.openFileDescriptor(uriStr.toUri(), "r") ?: return 0L
    val fileInputStream = FileInputStream(path.fileDescriptor)

    val chapterPage = chapterPageDao.read(chapterPageId)
    val result = chapterController.createPageFile(
      chapterPage,
      FileUtils.getMimeFromExtension(extension),
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

    val page = chapterPageDao.read(chapterPageId)
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

    val path = context.contentResolver
      .openFileDescriptor(uriStr.toUri(), "r") ?: return false
    val fileInputStream = FileInputStream(path.fileDescriptor)

    val result = dbBackup.restore(db, fileInputStream)
    fileInputStream.close()
    path.close()

    return result
  }

  private fun getFilesTree(): JSONArray {
    val tree = JSONArray()
    tree.put(FileUtils.tree(context.filesDir))
    tree.put(FileUtils.tree(context.cacheDir))
    tree.put(FileUtils.tree(documents))
    tree.put(FileUtils.tree(downloads))
    return tree
  }

  private fun setFileToDownloads(data: JSONObject): Boolean {
    val file = data.optString("file", "")
    Validation.string(file, "file")

    val fileName = data.optString("fileName", "")
    Validation.string(fileName, "fileName")

    val filePath = File("${downloadsApp}${File.separator}${fileName}")

    val result = FileUtils.write(filePath, file)
    Validation.fileCreate(result, filePath)
    return true
  }

  private fun downloadHTML(data: JSONObject): String {
    val url = data.optString("url", "")
    Validation.link(url, "url")
    val cookie = data.optString("cookie", "")

    // Создаем URL объект и открываем соединение
    val urlObj = URL(url)
    val connection = urlObj.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty(
      "User-Agent",
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36"
    )
    connection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
    connection.setRequestProperty("Cookie", cookie)
    connection.setRequestProperty("Priority", "u=0, i")
    connection.setRequestProperty("Referer", "${urlObj.protocol}://${urlObj.host}")
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

    return stringBuilder.toString()
  }

  private fun migrate(): Boolean {
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
