package com.konformist.comicsreader.parser

import com.konformist.comicsreader.data.author.AuthorController
import com.konformist.comicsreader.data.author.AuthorCreate
import com.konformist.comicsreader.data.chapter.ChapterController
import com.konformist.comicsreader.data.chapter.ChapterCreate
import com.konformist.comicsreader.data.chapter.ChapterUpdate
import com.konformist.comicsreader.data.chapter.ChapterWithPages
import com.konformist.comicsreader.data.chapterpage.ChapterPageController
import com.konformist.comicsreader.data.chapterpage.ChapterPageCreate
import com.konformist.comicsreader.data.chapterpage.ChapterPageUpdate
import com.konformist.comicsreader.data.chapterpage.ChapterPageWithFile
import com.konformist.comicsreader.data.comic.ComicController
import com.konformist.comicsreader.data.comic.ComicUpdate
import com.konformist.comicsreader.data.comiccover.ComicCoverController
import com.konformist.comicsreader.data.comiccover.ComicCoverUpdate
import com.konformist.comicsreader.data.comicoverride.ComicOverride
import com.konformist.comicsreader.data.language.LanguageController
import com.konformist.comicsreader.data.language.LanguageCreate
import com.konformist.comicsreader.data.parserconfig.ParserConfig
import com.konformist.comicsreader.data.tag.TagController
import com.konformist.comicsreader.data.tag.TagCreate
import com.konformist.comicsreader.utils.RequestUtils
import org.jsoup.nodes.Document
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class ParserController(
  private val tagController: TagController,
  private val authorController: AuthorController,
  private val languageController: LanguageController,
  private val comicController: ComicController,
  private val comicCoverController: ComicCoverController,
  private val chapterController: ChapterController,
  private val chapterPageController: ChapterPageController,
) {
  companion object {
    fun createParser() = Parser()
  }

  private fun downloadHTML(url: String, cookie: String): Document {
    val connection = RequestUtils.getConnection(URL(url), cookie)

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

    return Parser.getDocument(stringBuilder.toString())
  }

  fun parse(
    url: String,
    cookie: String,
    config: ParserConfig,
    override: ComicOverride
  ): Parser.Result {
    val parser = createParser().mergeQuery(config, override)

    val comicHTML = downloadHTML(url, cookie)
    parser.parseComic(comicHTML)
      .parseCover(comicHTML)
      .parseTags(comicHTML)
      .parseAuthors(comicHTML)
      .parseLanguages(comicHTML)
      .parseChapters(comicHTML)

    if (parser.queryElements.pagesTemplateUrl.isNotBlank()) {
      parser.chapterLinks.forEachIndexed { iChapter, chapter ->
        if (chapter.isNotBlank()) {
          val linkFirstPage = parser.mergePageLink(chapter, 1)
          val firstPageHTML = downloadHTML(linkFirstPage, cookie)
          parser.parseCountPages(firstPageHTML, iChapter)

          parser.result.chapters[iChapter].pages.forEachIndexed { iPage, page ->
            val linkPage = parser.mergePageLink(chapter, iPage + 1)
            val pageHTML = downloadHTML(linkPage, cookie)
            parser.parseChapterPage(pageHTML, iChapter, iPage)
          }
        }
      }
    }

    return parser.result
  }

  private fun <T> setNotBlank(first: List<T>, last: List<T>): List<T> {
    return if (first.isNotEmpty()) first else last
  }

  private fun setNotBlank(first: Long, last: Long): Long {
    return if (first != 0L) first else last
  }

  private fun setNotBlank(first: String, last: String): String {
    return if (first.isNotBlank()) first else last
  }

  private fun updateTags(data: Parser.Result): List<Long> {
    val tags = tagController.readAll()
    val tagsNames = tags.map { it.name.lowercase() }
    val result = mutableListOf<Long>()

    data.tags.forEach { item ->
      if (!tagsNames.contains(item.lowercase())) {
        val rowId = tagController.create(TagCreate(name = item))
        result.add(rowId)
      } else {
        tags.find { it.name.lowercase() == item.lowercase() }?.let {
          result.add(it.id)
        }
      }
    }

    return result
  }

  private fun updateAuthors(data: Parser.Result): List<Long> {
    val authors = authorController.readAll()
    val authorsNames = authors.map { it.name.lowercase() }
    val result = mutableListOf<Long>()

    data.authors.forEach { item ->
      if (!authorsNames.contains(item.lowercase())) {
        val rowId = authorController.create(AuthorCreate(name = item))
        result.add(rowId)
      } else {
        authors.find { it.name.lowercase() == item.lowercase() }?.let {
          result.add(it.id)
        }
      }
    }

    return result
  }

  private fun updateLanguages(data: Parser.Result): List<Long> {
    val languages = languageController.readAll()
    val languagesNames = languages.map { it.name.lowercase() }
    val result = mutableListOf<Long>()

    data.languages.forEach { item ->
      if (!languagesNames.contains(item.lowercase())) {
        val rowId = languageController.create(LanguageCreate(name = item))
        result.add(rowId)
      } else {
        languages.find { it.name.lowercase() == item.lowercase() }?.let {
          result.add(it.id)
        }
      }
    }

    return result
  }

  private fun updateCover(data: Parser.Result, comicId: Long) {
    if (data.coverUrl.isNotBlank()) {
      comicCoverController.readByComic(comicId)?.let { cover ->
        comicCoverController.update(
          ComicCoverUpdate(
            id = cover.id,
            fromUrl = data.coverUrl,
          )
        )
      }
    }
  }

  private fun updatePage(data: String, chapterId: Long, page: ChapterPageWithFile?) {
    if (page?.page == null) {
      chapterPageController.create(
        ChapterPageCreate(
          chapterId = chapterId,
          fromUrl = data,
        )
      )
    } else if (data.isNotBlank()) {
      chapterPageController.update(
        ChapterPageUpdate(
          id = page.page.id,
          fromUrl = data,
        )
      )
    }
  }

  private fun updateChapter(data: Parser.ResultChapter, comicId: Long, chapter: ChapterWithPages?) {
    var chapterId = 0L

    if (chapter?.chapter == null) {
      chapterId = chapterController.create(
        ChapterCreate(comicId = comicId, name = data.title)
      )
    } else {
      chapterId = chapter.chapter.id
      chapterController.update(
        ChapterUpdate(
          id = chapterId,
          name = setNotBlank(data.title, chapter.chapter.name),
        )
      )
    }

    data.pages.forEachIndexed { iPage, page ->
      updatePage(page, chapterId, chapter?.pages?.getOrNull(iPage))
    }
  }

  private fun updateChapters(data: Parser.Result, comicId: Long) {
    if (data.chapters.isEmpty()) return

    val chapters = chapterController.readByComicAll(comicId)

    data.chapters.forEachIndexed { iChapter, chapter ->
      updateChapter(chapter, comicId, chapters.getOrNull(iChapter))
    }
  }

  fun saveData(comicId: Long, data: Parser.Result): Boolean {
    val comic = comicController.read(comicId) ?: return false

    val tagsComic = updateTags(data)
    val authorsComic = updateAuthors(data)
    val languagesComic = updateLanguages(data)

    comicController.update(
      ComicUpdate(
        id = comicId,
        name = setNotBlank(data.title, comic.name),
        annotation = setNotBlank(data.annotation, comic.annotation),
        languageId = setNotBlank(languagesComic[0], comic.languageId),
        authors = setNotBlank(authorsComic, comic.authors),
        tags = setNotBlank(tagsComic, comic.tags),
      )
    )

    updateCover(data, comicId)
    updateChapters(data, comicId)

    return true
  }
}