package com.konformist.comicsreader.webapi

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.room.Room
import com.konformist.comicsreader.AppBackup
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.db.appfile.AppFileCreate
import com.konformist.comicsreader.db.appfile.AppFileDelete
import com.konformist.comicsreader.db.appfile.AppFileUpdate
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
import com.konformist.comicsreader.exceptions.ValidationException
import com.konformist.comicsreader.utils.AppDirectory
import com.konformist.comicsreader.utils.archive.ArchiveUtils
import com.konformist.comicsreader.utils.archive.ArchiveFormat
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
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
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
    Validation.dbCreate(rowId, "ComicOverride")

    return rowId
  }

  @Throws(DatabaseException::class)
  private fun deleteComicOverride(comicId: Long) {
    val row = comicOverrideDao.readByComic(comicId)

    val count = comicOverrideDao.delete(ComicOverrideDelete(id = row.id))
    Validation.dbDelete(count, "ComicOverride")
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

    Validation.dbCreate(rowId, "ComicCover")
  }

  @Throws(DatabaseException::class)
  private fun updateCover(cover: ComicCoverUpdate) {
    val count = comicCoverDao.update(cover)
    Validation.dbUpdate(count, "ComicCover")
  }

  @Throws(DatabaseException::class)
  private fun deleteCover(comicId: Long) {
    val row = comicCoverDao.readByComic(comicId)

    if (row.fileId != null && row.fileId != 0.toLong())
      deleteImage(row.fileId)

    val count = comicCoverDao.delete(ComicCoverDelete(id = row.id))
    Validation.dbDelete(count, "ComicCover")
  }

  @Throws(DatabaseException::class)
  private fun deleteChapter(chapterId: Long) {
    val row = chapterDao.readWithPages(chapterId)

    val pages = chapterPageDao.readByChapterAll(chapterId)
    for (page in pages) deleteChapterPage(page.page.id)

    val count = chapterDao.delete(ChapterDelete(id = row.chapter.id))
    Validation.dbDelete(count, "Chapter")
  }

  @Throws(DatabaseException::class)
  private fun deleteChapterPage(id: Long) {
    val row = chapterPageDao.read(id)
    if (row.fileId != 0.toLong()) deleteImage(row.fileId)

    val count = chapterPageDao.delete(ChapterPageDelete(id = id))
    Validation.dbDelete(count, "ChapterPage")
  }

  @Throws(DatabaseException::class)
  private fun createComicImage(mime: String, file: InputStream): Long {
    val rowId = appFileDao.create(
      AppFileCreate(
        name = "",
        mime = "",
        size = 0,
        path = "",
      )
    )
    Validation.dbCreate(rowId, "AppFile")

    if (!comicsImagesDir.exists()) comicsImagesDir.mkdirs()

    val extension = if (AppDataStore.settings.isCompress) "webp"
    else FileUtils.getExtensionFromMime(mime)

    val fileOut = File("${comicsImagesDir}${File.separator}${rowId}.${extension}")

    try {
      if (AppDataStore.settings.isCompress) {
        val decodedImage: Bitmap = BitmapFactory.decodeStream(file)
        FileOutputStream(fileOut).use { item ->
          ImageUtils.writeStream(item, decodedImage, 80)
        }
        decodedImage.recycle()
      } else {
        FileOutputStream(fileOut).use { item ->
          FileUtils.writeStream(item, file)
        }
      }

      val count = appFileDao.update(
        AppFileUpdate(
          id = rowId,
          mdate = DatesUtils.nowFormatted(),
          name = fileOut.name,
          mime = mime,
          size = fileOut.length(),
          path = fileOut.path,
        )
      )
      Validation.dbUpdate(count, "AppFile")

      return rowId
    } catch (e: Error) {
      if (fileOut.exists()) fileOut.delete()
      appFileDao.delete(AppFileDelete(id = rowId))
      throw e
    }
  }

  @Throws(DatabaseException::class)
  private fun deleteImage(id: Long?) {
    if (id == null || id == 0L) return
    val row = appFileDao.read(id)

    val fileOut = File("${context.filesDir}/${row.path}")
    if (fileOut.exists()) fileOut.delete()

    val count = appFileDao.delete(AppFileDelete(id = row.id))
    Validation.dbDelete(count, "AppFile")
  }

  private fun getTagsAll(): JSONArray {
    return JSONArray(Json.encodeToString<List<Tag>>(tagDao.readAll()))
  }

  private fun getTag(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return JSONObject(Json.encodeToString<Tag>(tagDao.read(rowId)))
  }

  private fun addTag(data: JSONObject): Long {
    val rowId = tagDao.create(jsonIgnore.decodeFromString<TagCreate>(data.toString()))
    Validation.dbCreate(rowId, "Tag")

    return rowId
  }

  private fun setTag(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    data.put("mdate", DatesUtils.nowFormatted())
    val count = tagDao.update(jsonIgnore.decodeFromString<TagUpdate>(data.toString()))
    Validation.dbUpdate(count, "Tag")
    return true
  }

  private fun delTag(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val count = tagDao.delete(jsonIgnore.decodeFromString<TagDelete>(data.toString()))
    Validation.dbDelete(count, "Tag")
    return true
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

  private fun addComic(data: JSONObject): Long {
    val rowId = comicDao.create(jsonIgnore.decodeFromString<ComicCreate>(data.toString()))
    Validation.dbCreate(rowId, "Comic")

    createCover(rowId)
    createComicOverride(rowId)

    return rowId
  }

  private fun setCover(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    val cover = comicCoverDao.read(rowId)
    data.put("mdate", DatesUtils.nowFormatted())
    data.put("fileId", cover.fileId)
    updateCover(jsonIgnore.decodeFromString<ComicCoverUpdate>(data.toString()))
    return true
  }

  private fun setComic(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val coverJSON = data.optJSONObject("cover")
    if (coverJSON != null) setCover(coverJSON)

    data.put("mdate", DatesUtils.nowFormatted())
    val count = comicDao.update(jsonIgnore.decodeFromString<ComicUpdate>(data.toString()))
    Validation.dbUpdate(count, "Comic")

    return true
  }

  private fun delComic(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    deleteCover(rowId)
    deleteComicOverride(rowId)

    val chapters = chapterDao.readByComicAll(rowId)
    chapters.forEach { chapter -> deleteChapter(chapter.id) }

    val count = comicDao.delete(jsonIgnore.decodeFromString<ComicDelete>(data.toString()))
    Validation.dbDelete(count, "Comic")

    return true
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

    val outFile = File("$downloadsApp${File.separator}${comic.name}.cbz")
    if (!outFile.parentFile.exists()) outFile.parentFile.mkdirs()
    compress.compress(outFile, ArchiveFormat.ZIP)
    dirTmp.deleteRecursively()

    return true
  }

  private fun getComicOverride(data: JSONObject): JSONObject {
    val rowId = data.optLong("comicId")
    Validation.id(rowId, "id")

    return JSONObject(Json.encodeToString<ComicOverride>(comicOverrideDao.readByComic(rowId)))
  }

  private fun setComicOverride(data: JSONObject): Boolean {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    data.put("mdate", DatesUtils.nowFormatted())
    val count =
      comicOverrideDao.update(jsonIgnore.decodeFromString<ComicOverrideUpdate>(data.toString()))
    Validation.dbUpdate(count, "ComicOverride")

    return true
  }

  private fun downloadCoverFile(data: JSONObject): Long {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    val link = data.optString("link", "")
    Validation.link(link, "link")

    val cover = comicCoverDao.readByComic(comicId)
    if (cover.fileId != null && cover.fileId != 0.toLong())
      deleteImage(cover.fileId)

    val connection = URL(link).openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.connect()

    if (connection.responseCode != 200)
      throw Exception("Error connection: ${connection.responseCode}")

    // Получаем MIME тип из заголовков
    val mimeType = connection.contentType

    val rowId = createComicImage(mimeType, connection.inputStream)
    connection.inputStream.close()
    connection.disconnect()
    val count = comicCoverDao.update(
      ComicCoverUpdate(
        id = cover.id,
        mdate = DatesUtils.nowFormatted(),
        fromUrl = cover.fromUrl,
        fileId = rowId
      )
    )
    Validation.dbUpdate(count, "ComicCover")

    return rowId
  }

  private fun addCoverFile(data: JSONObject): Long {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

    val uriStr = data.optString("uri")
    Validation.uri(uriStr, "uri")

    val cover = comicCoverDao.readByComic(comicId)

    if (cover.fileId != null && cover.fileId != 0.toLong())
      deleteImage(cover.fileId)

    val lastDotIndex = uriStr.lastIndexOf('.')
    val extension = if (lastDotIndex == -1 || lastDotIndex >= uriStr.length - 1) ""
    else uriStr.substring(lastDotIndex + 1) // No extension found

    val path = context.contentResolver.openFileDescriptor(uriStr.toUri(), "r") ?: return 0L
    val inputStream = FileInputStream(path.fileDescriptor)
    val rowId = createComicImage(
      FileUtils.getMimeFromExtension(extension),
      inputStream
    )
    inputStream.close()
    path.close()

    val count = comicCoverDao.update(
      ComicCoverUpdate(
        id = cover.id,
        mdate = DatesUtils.nowFormatted(),
        fromUrl = cover.fromUrl,
        fileId = rowId
      )
    )
    Validation.dbUpdate(count, "ComicCover")

    return rowId
  }

  private fun delCoverFile(data: JSONObject): Boolean {
    val comicId = data.optLong("comicId")
    Validation.id(comicId, "comicId")

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
    Validation.dbUpdate(count, "ComicCover")

    return true
  }

  private fun getChaptersAll(data: JSONObject): JSONArray {
    val comicId = data.getLong("comicId")
    Validation.id(comicId, "comicId")

    return ChapterSerializer.toJSONArray(chapterDao.readWithPagesByComicAll(comicId))
  }

  private fun getChapter(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return ChapterSerializer.toJSON(chapterDao.readWithPages(rowId))
  }

  private fun addChapter(data: JSONObject): Long {
    val rowId = chapterDao.create(jsonIgnore.decodeFromString<ChapterCreate>(data.toString()))
    Validation.dbCreate(rowId, "Chapter")

    return rowId
  }

  private fun setChapter(data: JSONObject): Boolean {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    data.put("mdate", DatesUtils.nowFormatted())
    val count = chapterDao.update(jsonIgnore.decodeFromString<ChapterUpdate>(data.toString()))
    Validation.dbUpdate(count, "Chapter")

    return true
  }

  private fun delChapter(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    deleteChapter(rowId)
    return true
  }

  private fun getChapterPagesAll(data: JSONObject): JSONArray {
    val chapterId = data.getLong("chapterId")
    Validation.id(chapterId, "chapterId")

    return ChapterPageSerializer.toJSONArray(chapterPageDao.readByChapterAll(chapterId))
  }

  private fun getChapterPage(data: JSONObject): JSONObject {
    val rowId = data.optLong("id")
    Validation.id(rowId, "id")

    return ChapterPageSerializer.toJSON(chapterPageDao.readWithFile(rowId))
  }

  private fun addChapterPage(data: JSONObject): Long {
    val rowId =
      chapterPageDao.create(jsonIgnore.decodeFromString<ChapterPageCreate>(data.toString()))
    Validation.dbCreate(rowId, "ChapterPage")

    return rowId
  }

  private fun setChapterPage(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    val chapterPage = chapterPageDao.read(rowId)
    data.put("fileId", chapterPage.fileId)
    data.put("mdate", DatesUtils.nowFormatted())

    val count =
      chapterPageDao.update(jsonIgnore.decodeFromString<ChapterPageUpdate>(data.toString()))
    Validation.dbUpdate(count, "ChapterPage")

    return true
  }

  private fun delChapterPage(data: JSONObject): Boolean {
    val rowId = data.getLong("id")
    Validation.id(rowId, "id")

    deleteChapterPage(rowId)
    return true
  }

  private fun downloadChapterPageFile(data: JSONObject): Long {
    val chapterPageId = data.optLong("chapterPageId")
    Validation.id(chapterPageId, "chapterPageId")

    val link = data.optString("link", "")
    Validation.link(link, "link")

    val chapterPage = chapterPageDao.read(chapterPageId)

    if (chapterPage.fileId != null && chapterPage.fileId != 0.toLong())
      deleteImage(chapterPage.fileId)

    val connection = URL(link).openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.connect()

    if (connection.responseCode != 200)
      throw Exception("Error connection: ${connection.responseCode}")

    // Получаем MIME тип из заголовков
    val mimeType = connection.contentType

    val rowId = createComicImage(mimeType, connection.inputStream)
    connection.inputStream.close()
    connection.disconnect()

    val count = chapterPageDao.update(
      ChapterPageUpdate(
        id = chapterPage.id,
        mdate = DatesUtils.nowFormatted(),
        fromUrl = chapterPage.fromUrl,
        fileId = rowId,
        isRead = chapterPage.isRead,
      )
    )
    Validation.dbUpdate(count, "ComicCover")

    return rowId
  }

  private fun addChapterPageFile(data: JSONObject): Long {
    val chapterPageId = data.optLong("chapterPageId")
    Validation.id(chapterPageId, "chapterPageId")

    val uriStr = data.optString("uri")
    Validation.uri(uriStr, "uri")

    val chapterPage = chapterPageDao.read(chapterPageId)

    if (chapterPage.fileId != null && chapterPage.fileId != 0.toLong())
      deleteImage(chapterPage.fileId)

    val lastDotIndex = uriStr.lastIndexOf('.')
    val extension = if (lastDotIndex == -1 || lastDotIndex >= uriStr.length - 1) ""
    else uriStr.substring(lastDotIndex + 1) // No extension found

    val path = context.contentResolver.openFileDescriptor(uriStr.toUri(), "r") ?: return 0L
    val fileInputStream = FileInputStream(path.fileDescriptor)
    val rowId = createComicImage(
      FileUtils.getMimeFromExtension(extension),
      fileInputStream,
    )
    fileInputStream.close()
    path.close()

    val count = chapterPageDao.update(
      ChapterPageUpdate(
        id = chapterPage.id,
        mdate = DatesUtils.nowFormatted(),
        fromUrl = chapterPage.fromUrl,
        fileId = rowId,
        isRead = chapterPage.isRead,
      )
    )
    Validation.dbUpdate(count, "ChapterPage")

    return rowId
  }

  private fun delChapterPageFile(data: JSONObject): Boolean {
    val chapterPageId = data.optLong("chapterPageId")
    Validation.id(chapterPageId, "chapterPageId")

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

    Validation.dbUpdate(count, "ChapterPage")

    return true
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
