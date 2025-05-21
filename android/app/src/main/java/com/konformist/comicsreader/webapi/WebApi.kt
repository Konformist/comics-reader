package com.konformist.comicsreader.webapi

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Room
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.db.author.Author
import com.konformist.comicsreader.db.comic.Chapter
import com.konformist.comicsreader.db.comic.ChapterPage
import com.konformist.comicsreader.db.comic.Comic
import com.konformist.comicsreader.db.comic.ComicCover
import com.konformist.comicsreader.db.comic.ComicOverride
import com.konformist.comicsreader.db.parser.Parser
import com.konformist.comicsreader.db.tag.Tag
import com.konformist.comicsreader.utils.AppDirectory
import com.konformist.comicsreader.utils.FileUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Date

class WebApi(private val context: Context) {
  private val db: AppDatabase = Room
    .databaseBuilder(context, AppDatabase::class.java, "app-database")
    .build()

  private val tagSerializer = TagSerializer()
  private val authorSerializer = AuthorSerializer()
  private val languageSerializer = LanguageSerializer()
  private val parserSerializer = ParserSerializer()
  private val chapterSerializer = ChapterSerializer()
  private val chapterWithPagesSerializer = ChapterWithPagesSerializer()
  private val chapterPageSerializer = ChapterPageSerializer()
  private val chapterPageWithFileSerializer = ChapterPageWithFileSerializer()
  private val comicCoverSerializer = ComicCoverSerializer()
  private val comicOverrideSerializer = ComicOverrideSerializer()
  private val comicSerializer = ComicSerializer()
  private val comicLiteSerializer = ComicLiteSerializer()

  private fun <T> arrayToJSON(items: List<T>, serializer: Serializer<T>): String {
    val result = JSONObject()

    result.put("items", serializer.toJSONArray(items))
    return result.toString()
  }

  @Throws(Error::class)
  private fun checkRowId(rowId: Long) {
    if (rowId == 0.toLong()) throw Error("Id is empty")
  }

  fun getTagsAll(): String {
    return arrayToJSON(db.tagDao().readAll(), tagSerializer)
  }

  @Throws(Error::class)
  fun getTag(rowId: Long): String {
    checkRowId(rowId)
    val item = db.tagDao().read(rowId)
    return tagSerializer.toJSON(item).toString()
  }

  @Throws(Error::class)
  fun addTag(data: JSONObject): String {
    val item = JSONObject(data.toString())
    item.put("id", 0)
    item.put("cdate", Date().time)
    item.put("mdate", Date().time)

    val rowId = db.tagDao().create(tagSerializer.fromJSON(item))
    if (rowId == 0.toLong()) throw Error("Tag not created")

    return JSONObject().put("id", rowId).toString()
  }

  @Throws(Error::class)
  fun setTag(data: JSONObject) {
    checkRowId(data.getLong("id"))
    val item = JSONObject(data.toString())
    item.put("cdate", 0)
    item.put("mdate", Date().time)

    val count = db.tagDao().update(tagSerializer.fromJSON(item))
    if (count == 0) throw Error("Tag not updated")
  }

  @Throws(Error::class)
  fun delTag(data: JSONObject) {
    checkRowId(data.getLong("id"))
    val item = JSONObject(data.toString())
    val count = db.tagDao().delete(tagSerializer.fromJSON(item))
    if (count == 0) throw Error("Tag not deleted")
  }

  fun getAuthorsAll(): String {
    return arrayToJSON(db.authorDao().readAll(), authorSerializer)
  }

  @Throws(Error::class)
  fun getAuthor(rowId: Long): String {
    checkRowId(rowId)
    val item = db.authorDao().read(rowId)
    return authorSerializer.toJSON(item).toString()
  }

  @Throws(Error::class)
  fun addAuthor(data: JSONObject): String {
    val item = JSONObject(data.toString())
    item.put("id", 0)
    item.put("cdate", Date().time)
    item.put("mdate", Date().time)

    val rowId = db.authorDao().create(authorSerializer.fromJSON(item))
    if (rowId == 0.toLong()) throw Error("Author not created")

    return JSONObject().put("id", rowId).toString()
  }

  @Throws(Error::class)
  fun setAuthor(data: JSONObject) {
    checkRowId(data.getLong("id"))
    val item = JSONObject(data.toString())
    item.put("cdate", 0)
    item.put("mdate", Date().time)

    val count = db.authorDao().update(authorSerializer.fromJSON(item))
    if (count == 0) throw Error("Author not updated")
  }

  @Throws(Error::class)
  fun delAuthor(data: JSONObject) {
    checkRowId(data.getLong("id"))
    val item = JSONObject(data.toString())
    val count = db.authorDao().delete(authorSerializer.fromJSON(item))

    if (count == 0) throw Error("Author not deleted")
  }

  fun getLanguagesAll(): String {
    return arrayToJSON(db.languageDao().readAll(), languageSerializer)
  }

  @Throws(Error::class)
  fun getLanguage(rowId: Long): String {
    checkRowId(rowId)
    val item = db.languageDao().read(rowId)
    return languageSerializer.toJSON(item).toString()
  }

  @Throws(Error::class)
  fun addLanguage(data: JSONObject): String {
    val item = JSONObject(data.toString())
    item.put("id", 0)
    item.put("cdate", Date().time)
    item.put("mdate", Date().time)

    val rowId = db.languageDao().create(languageSerializer.fromJSON(item))

    if (rowId == 0.toLong()) throw Error("Language not created")
    return JSONObject().put("id", rowId).toString()
  }

  @Throws(Error::class)
  fun setLanguage(data: JSONObject) {
    checkRowId(data.getLong("id"))
    val item = JSONObject(data.toString())
    item.put("cdate", 0)
    item.put("mdate", Date().time)

    val count = db.languageDao().update(languageSerializer.fromJSON(item))
    if (count == 0) throw Error("Language not updated")
  }

  @Throws(Error::class)
  fun delLanguage(data: JSONObject) {
    checkRowId(data.getLong("id"))
    val item = JSONObject(data.toString())
    val count = db.languageDao().delete(languageSerializer.fromJSON(item))

    if (count == 0) throw Error("Language not deleted")
  }

  fun getParsersAll(): String {
    return arrayToJSON(db.parserDao().readAll(), parserSerializer)
  }

  @Throws(Error::class)
  fun getParser(rowId: Long): String {
    checkRowId(rowId)
    val item = db.parserDao().read(rowId)
    return parserSerializer.toJSON(item).toString()
  }

  @Throws(Error::class)
  fun addParser(data: JSONObject): String {
    val item = JSONObject(data.toString())
    item.put("id", 0)
    item.put("cdate", Date().time)
    item.put("mdate", Date().time)

    val rowId = db.parserDao().create(parserSerializer.fromJSON(item))
    if (rowId == 0.toLong()) throw Error("Parser not created")

    return JSONObject().put("id", rowId).toString()
  }

  @Throws(Error::class)
  fun setParser(data: JSONObject) {
    checkRowId(data.getLong("id"))
    val item = JSONObject(data.toString())
    item.put("cdate", 0)
    item.put("mdate", Date().time)

    val count = db.parserDao().update(parserSerializer.fromJSON(item))

    if (count == 0) throw Error("Parser not updated")
  }

  @Throws(Error::class)
  fun delParser(data: JSONObject) {
    checkRowId(data.getLong("id"))
    val item = JSONObject(data.toString())
    val count = db.parserDao().delete(parserSerializer.fromJSON(item))

    if (count == 0) throw Error("Parser not deleted")
  }

  fun getComicsAll(): String {
    return arrayToJSON(db.comicDao().readAll(), comicLiteSerializer)
  }

  @Throws(Error::class)
  fun getComic(rowId: Long): String {
    checkRowId(rowId)
    val item = db.comicDao().read(rowId)
    return comicLiteSerializer.toJSON(item).toString()
  }

  @Throws(Error::class)
  fun addComic(data: JSONObject): String {
    val item = JSONObject(data.toString())
    item.put("id", 0)
    item.put("cdate", Date().time)
    item.put("mdate", Date().time)

    val rowId = db.comicDao().create(comicSerializer.fromJSON(item))
    if (rowId == 0.toLong()) throw Error("Comic not created")

    createCover(rowId)
    createOverride(rowId)

    return JSONObject().put("id", rowId).toString()
  }

  @Throws(Error::class)
  fun setComic(data: JSONObject) {
    checkRowId(data.getLong("id"))
    val item = JSONObject(data.toString())
    item.put("cdate", 0)
    item.put("mdate", Date().time)

    val coverJSON = item.optJSONObject("cover")
    if (coverJSON != null) {
      coverJSON.put("cdate", 0)
      coverJSON.put("mdate", Date().time)
      coverJSON.remove("fileId")

      val cover = comicCoverSerializer.fromJSON(coverJSON)
      updateCover(cover.cover)
    }

    val count = db.comicDao().update(comicSerializer.fromJSON(item))
    if (count == 0) throw Error("Comic not updated")
  }

  @Throws(Error::class)
  fun delComic(data: JSONObject) {
    val id = data.getLong("id")
    checkRowId(id)

    deleteCover(id)
    deleteOverride(id)

    val chapters = db.chapterDao().readByComicAll(id)
    for (chapter in chapters) {
      deleteChapter(chapter.chapter.id!!)
    }

    val item = JSONObject(data.toString())
    val count = db.comicDao().delete(comicSerializer.fromJSON(item))

    if (count == 0) throw Error("Comic not deleted")
  }

  @Throws(Error::class)
  private fun createOverride(comicId: Long) {
    val override = ComicOverride(
      id = null,
      cdate = Date(),
      mdate = Date(),
      comicId = comicId,
      titleCSS = null,
      coverCSS = null,
      pagesCSS = null,
      authorsCSS = null,
      authorsTextCSS = null,
      languageCSS = null,
      tagsCSS = null,
      tagsTextCSS = null,
    )
    val rowId = db.comicOverrideDao().create(override)
    if (rowId == 0.toLong()) throw Error("Comic override not created")
  }

  @Throws(Error::class)
  fun getComicOverride(rowId: Long): String {
    checkRowId(rowId)
    val item = db.comicOverrideDao().read(rowId)
    return comicOverrideSerializer.toJSON(item).toString()
  }

  @Throws(Error::class)
  fun setComicOverride(data: JSONObject) {
    val rowId = data.getLong("id")
    checkRowId(rowId)
    val override = db.comicOverrideDao().read(rowId)
    val item = JSONObject(data.toString())
    item.put("cdate", 0)
    item.put("mdate", Date().time)
    item.put("comicId", override.comicId)

    val count = db.comicOverrideDao().update(comicOverrideSerializer.fromJSON(item))
    if (count == 0) throw Error("Comic override not updated")
  }

  @Throws(Error::class)
  private fun deleteOverride(comicId: Long) {
    val row = db.comicOverrideDao().readByComic(comicId)

    val count = db.comicOverrideDao().delete(row)
    if (count == 0) throw Error("Comic override not deleted")
  }

  @Throws(Error::class)
  private fun createCover(comicId: Long) {
    val cover = ComicCover(
      id = null,
      cdate = Date(),
      mdate = Date(),
      comicId = comicId,
      fileId = null,
      fromUrl = "",
    )

    val rowId = db.comicCoverDao().create(cover)
    if (rowId == 0.toLong()) throw Error("Comic cover not created")
  }

  @Throws(Error::class)
  private fun updateCover(cover: ComicCover) {
    val count = db.comicCoverDao().update(cover)
    if (count == 0) throw Error("Comic cover not updated")
  }

  @Throws(Error::class)
  private fun deleteCover(comicId: Long) {
    val row = db.comicCoverDao().readByComic(comicId)

    if (row.cover.fileId != null)
      deleteImage(row.cover.fileId)

    val count = db.comicCoverDao().delete(row.cover)
    if (count == 0) throw Error("Comic cover not deleted")
  }

  @Throws(Error::class)
  fun addCoverFile(data: JSONObject): String {
    val comicId = data.optLong("comicId")
    checkRowId(comicId)
    val rowId =
      createImage("${AppDirectory.Comics.value}/$comicId", "cover.webp", data.getString("file"))
    if (rowId == 0.toLong()) throw Error("Cover file not added")

    val cover = db.comicCoverDao().readByComic(comicId)
    val newCover = ComicCover(
      id = cover.cover.id,
      cdate = cover.cover.cdate,
      mdate = Date(),
      comicId = cover.cover.comicId,
      fromUrl = cover.cover.fromUrl,
      fileId = rowId
    )

    val count = db.comicCoverDao().update(newCover)
    if (count == 0) throw Error("Comic cover not updated")

    return JSONObject().put("id", rowId).toString()
  }

  @Throws(Error::class)
  fun delCoverFile(data: JSONObject) {
    val comicId = data.optLong("comicId")
    checkRowId(comicId)
    val cover = db.comicCoverDao().readByComic(comicId)
    deleteImage(cover.cover.fileId!!)
    db.comicCoverDao().update(
      ComicCover(
        id = cover.cover.id,
        cdate = cover.cover.cdate,
        mdate = Date(),
        fromUrl = cover.cover.fromUrl,
        fileId = 0,
        comicId = cover.cover.comicId,
      )
    )
  }

  @Throws(Error::class)
  fun getChaptersAll(data: JSONObject): String {
    val comicId = data.getLong("comicId")
    checkRowId(comicId)
    return arrayToJSON(db.chapterDao().readByComicAll(comicId), chapterWithPagesSerializer)
  }

  @Throws(Error::class)
  fun getChapter(rowId: Long): String {
    checkRowId(rowId)
    val item = db.chapterDao().read(rowId)
    return chapterWithPagesSerializer.toJSON(item).toString()
  }

  @Throws(Error::class)
  fun addChapter(data: JSONObject): String {
    val item = JSONObject(data.toString())
    item.put("id", 0)
    item.put("cdate", Date().time)
    item.put("mdate", Date().time)

    val rowId = db.chapterDao().create(chapterSerializer.fromJSON(item))
    if (rowId == 0.toLong()) throw Error("Chapter not created")

    return JSONObject().put("id", rowId).toString()
  }

  @Throws(Error::class)
  fun setChapter(data: JSONObject) {
    checkRowId(data.getLong("id"))
    val item = JSONObject(data.toString())
    item.put("cdate", 0)
    item.put("mdate", Date().time)

    val count = db.chapterDao().update(chapterSerializer.fromJSON(item))

    if (count == 0) throw Error("Chapter not updated")
  }

  @Throws(Error::class)
  private fun deleteChapter(chapterId: Long) {
    val row = db.chapterDao().read(chapterId)

    val pages = db.chapterPageDao().readByChapterAll(chapterId)
    for (page in pages) {
      deleteChapterPage(page.page.id!!)
    }

    val count = db.chapterDao().delete(row.chapter)
    if (count == 0) throw Error("Chapter not deleted")
  }

  @Throws(Error::class)
  fun delChapter(data: JSONObject) {
    val id = data.getLong("id")
    checkRowId(id)
    deleteChapter(id)
  }

  @Throws(Error::class)
  fun getChapterPagesAll(data: JSONObject): String {
    val chapterId = data.getLong("chapterId")
    checkRowId(chapterId)
    return arrayToJSON(
      db.chapterPageDao().readByChapterAll(chapterId),
      chapterPageWithFileSerializer
    )
  }

  @Throws(Error::class)
  fun getChapterPage(rowId: Long): String {
    checkRowId(rowId)
    val item = db.chapterPageDao().read(rowId)
    return chapterPageWithFileSerializer.toJSON(item).toString()
  }

  @Throws(Error::class)
  fun addChapterPage(data: JSONObject): String {
    checkRowId(data.getLong("chapterId"))
    val item = JSONObject(data.toString())
    item.put("id", 0)
    item.put("cdate", Date().time)
    item.put("mdate", Date().time)
    item.remove("fileId")

    val rowId = db.chapterPageDao().create(chapterPageSerializer.fromJSON(item))
    if (rowId == 0.toLong()) throw Error("Chapter page not created")

    return JSONObject().put("id", rowId).toString()
  }

  @Throws(Error::class)
  fun setChapterPage(data: JSONObject) {
    checkRowId(data.getLong("id"))
    val item = JSONObject(data.toString())
    item.put("cdate", 0)
    item.put("mdate", Date().time)
    item.remove("fileId")

    val count = db.chapterDao().update(chapterSerializer.fromJSON(item))

    if (count == 0) throw Error("Chapter not updated")
  }

  @Throws(Error::class)
  private fun deleteChapterPage(chapterId: Long) {
    val row = db.chapterPageDao().readByChapter(chapterId)

    if (row.page.fileId != null)
      deleteImage(row.page.fileId)

    val count = db.chapterPageDao().delete(row.page)
    if (count == 0) throw Error("Chapter page not deleted")
  }

  @Throws(Error::class)
  fun delChapterPage(data: JSONObject) {
    val id = data.getLong("id")
    checkRowId(id)
    deleteChapterPage(id)
  }

  @Throws(Error::class)
  fun addChapterPageFile(data: JSONObject): String {
    val comicId = data.optLong("comicId")
    checkRowId(comicId)
    val chapterPageId = data.optLong("chapterPageId")
    checkRowId(chapterPageId)

    val rowId =
      createImage(
        "${AppDirectory.Comics.value}/$comicId",
        "$chapterPageId.webp",
        data.getString("file")
      )
    if (rowId == 0.toLong()) throw Error("Cover file not added")

    val chapterPage = db.chapterPageDao().read(chapterPageId)
    val newCover = ChapterPage(
      id = chapterPage.page.id,
      cdate = chapterPage.page.cdate,
      mdate = Date(),
      chapterId = chapterPage.page.chapterId,
      fromUrl = chapterPage.page.fromUrl,
      fileId = rowId
    )

    val count = db.chapterPageDao().update(newCover)
    if (count == 0) throw Error("Chapter page not updated")

    return JSONObject().put("id", rowId).toString()
  }

  @Throws(Error::class)
  fun delChapterPageFile(data: JSONObject) {
    val chapterPageId = data.optLong("chapterPageId")
    checkRowId(chapterPageId)
    val page = db.chapterPageDao().read(chapterPageId)
    deleteImage(page.page.fileId!!)
    val count = db.chapterPageDao().update(
      ChapterPage(
        id = page.page.id,
        cdate = page.page.cdate,
        mdate = Date(),
        chapterId = page.page.chapterId,
        fromUrl = page.page.fromUrl,
        fileId = 0,
      )
    )

    if (count == 0) throw Error("Chapter page not updated")
  }

  @Throws(Error::class)
  private fun deleteImage(id: Long) {
    val row = db.fileDao().read(id)

    val fileOut = File("${context.filesDir}/${row.path}")
    if (fileOut.exists()) fileOut.delete()

    val count = db.fileDao().delete(row)
    if (count == 0) throw Error("File not deleted")
  }

  @Throws(Error::class)
  private fun createImage(directory: String, name: String, file: String): Long {
    val cleaned = FileUtils.cleanImageData(file)
    val decodedImage: Bitmap = FileUtils.base64ToBitmap(cleaned)
    val dirOut = File("${context.filesDir}/${directory}")
    val fileOut = File("${dirOut.path}/${name}")

    if (!dirOut.exists()) {
      dirOut.mkdirs()
    }

    FileUtils.writeImage(fileOut, decodedImage)

    try {
      val newFile = com.konformist.comicsreader.db.file.File(
        id = null,
        cdate = Date(),
        mdate = Date(),
        name = name,
        mime = "image/${fileOut.extension}",
        size = fileOut.length(),
        path = "$directory/$name",
      )
      val rowId = db.fileDao().create(newFile)
      if (rowId == 0.toLong()) throw Error("File not created")
      return rowId
    } catch (e: Error) {
      if (fileOut.exists()) fileOut.delete()
      throw e
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

  fun migrate(data: JSONObject) {
    val mapTags: MutableMap<Long, Long> = mutableMapOf()
    val tagsJSON = data.getJSONArray("parsers")
    val tagsDao = db.tagDao()

    for (index in 0 until tagsJSON.length()) {
      val itemJSON = tagsJSON.getJSONObject(index)
      val itemId = tagsDao.create(
        Tag(
          id = null,
          cdate = Date(),
          mdate = Date(),
          name = itemJSON.getString("name"),
        )
      )
      mapTags[itemJSON.getLong("id")] = itemId
    }

    val mapAuthors: MutableMap<Long, Long> = mutableMapOf()
    val authorsJSON = data.getJSONArray("parsers")
    val authorsDao = db.authorDao()

    for (index in 0 until authorsJSON.length()) {
      val itemJSON = authorsJSON.getJSONObject(index)
      val itemId = authorsDao.create(
        Author(
          id = null,
          cdate = Date(),
          mdate = Date(),
          name = itemJSON.getString("name"),
        )
      )
      mapAuthors[itemJSON.getLong("id")] = itemId
    }

    val mapLanguages: MutableMap<Long, Long> = mutableMapOf()
    val languagesJSON = data.getJSONArray("parsers")
    val languagesDao = db.authorDao()

    for (index in 0 until languagesJSON.length()) {
      val itemJSON = languagesJSON.getJSONObject(index)
      val itemId = languagesDao.create(
        Author(
          id = null,
          cdate = Date(),
          mdate = Date(),
          name = itemJSON.getString("name"),
        )
      )
      mapLanguages[itemJSON.getLong("id")] = itemId
    }

    val mapParsers: MutableMap<Long, Long> = mutableMapOf()
    val parsersJSON = data.getJSONArray("parsers")
    val parserDao = db.parserDao()

    for (index in 0 until parsersJSON.length()) {
      val itemJSON = parsersJSON.getJSONObject(index)
      val itemId = parserDao.create(
        Parser(
          id = null,
          cdate = Date(),
          mdate = Date(),
          name = itemJSON.getString("name"),
          siteUrl = itemJSON.getString("site"),
          titleCSS = itemJSON.getString("title"),
          coverCSS = itemJSON.getString("image"),
          pagesCSS = itemJSON.getString("images"),
          authorsCSS = itemJSON.getString("authors"),
          authorsTextCSS = itemJSON.getString("authorsText"),
          languageCSS = itemJSON.getString("language"),
          tagsCSS = itemJSON.getString("tags"),
          tagsTextCSS = itemJSON.getString("tagsText"),
        )
      )
      mapParsers[itemJSON.getLong("id")] = itemId
    }

    val mapFiles: MutableMap<Long, Long> = mutableMapOf()
    val filesJSON = data.getJSONArray("parsers")
    val filesDao = db.fileDao()

    for (index in 0 until filesJSON.length()) {
      val itemJSON = filesJSON.getJSONObject(index)
      val itemId = filesDao.create(
        com.konformist.comicsreader.db.file.File(
          id = null,
          cdate = Date(),
          mdate = Date(),
          name = itemJSON.getString("name"),
          mime = itemJSON.getString("mime"),
          size = itemJSON.getLong("size"),
          path = itemJSON.getString("path"),
        )
      )
      mapFiles[itemJSON.getLong("id")] = itemId
    }

    val comicsJSON = data.getJSONArray("parsers")
    val comicDao = db.comicDao()
    val overrideDao = db.comicOverrideDao()
    val coverDao = db.comicCoverDao()
    val chapterDao = db.chapterDao()
    val pageDao = db.chapterPageDao()

    for (comicIndex in 0 until comicsJSON.length()) {
      val comicJSON = comicsJSON.getJSONObject(comicIndex)
      val tags: List<Long> = fromArrayString(comicJSON.getString("tags"))
      val tagsSave = mutableListOf<Long>()
      for (item in tags) tagsSave.add(mapTags[item]!!)
      val authors: List<Long> = fromArrayString(comicJSON.getString("authors"))
      val authorsSave = mutableListOf<Long>()
      for (item in authors) authorsSave.add(mapAuthors[item]!!)

      val comicId = comicDao.create(
        Comic(
          id = null,
          cdate = Date(),
          mdate = Date(),
          name = comicJSON.getString("name"),
          fromUrl = comicJSON.getString("url"),
          parserId = mapParsers[comicJSON.getLong("parser")],
          languageId = mapLanguages[comicJSON.getLong("language")],
          tags = tagsSave,
          authors = authorsSave,
        )
      )

      val overrideJSON = comicJSON.getJSONObject("override")

      overrideDao.create(
        ComicOverride(
          id = null,
          cdate = Date(),
          mdate = Date(),
          comicId = comicId,
          titleCSS = overrideJSON.getString("title"),
          coverCSS = overrideJSON.getString("image"),
          pagesCSS = overrideJSON.getString("images"),
          authorsCSS = overrideJSON.getString("authors"),
          authorsTextCSS = overrideJSON.getString("authorsText"),
          languageCSS = overrideJSON.getString("language"),
          tagsCSS = overrideJSON.getString("tags"),
          tagsTextCSS = overrideJSON.getString("tagsText"),
        )
      )

      val coverJSON = comicJSON.getJSONObject("image")

      coverDao.create(
        ComicCover(
          id = null,
          cdate = Date(),
          mdate = Date(),
          comicId = comicId,
          fileId = mapFiles[coverJSON.getLong("fileId")],
          fromUrl = coverJSON.getString("url"),
        )
      )

      val chapterId = chapterDao.create(
        Chapter(
          id = null,
          cdate = Date(),
          mdate = Date(),
          name = "",
          comicId = comicId,
        )
      )

      val pagesJSON = comicJSON.getJSONArray("images")

      for (pageIndex in 0 until pagesJSON.length()) {
        val pageJSON = pagesJSON.getJSONObject(pageIndex)

        pageDao.create(
          ChapterPage(
            id = null,
            cdate = Date(),
            mdate = Date(),
            chapterId = chapterId,
            fileId = mapFiles[pageJSON.getLong("fileId")],
            fromUrl = pageJSON.getString("url"),
          )
        )
      }
    }
  }
}