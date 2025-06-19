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
    fun createParser(config: ParserConfig, override: ComicOverride): HTMLParser {
      val queryElements = QueryElements.from(config, override)
      return HTMLParser(queryElements)
    }
  }

  fun parse(
    url: String,
    cookie: String,
    config: ParserConfig,
    override: ComicOverride
  ): ParserResult {
    val parser = createParser(config, override)
    val mainDoc = HtmlLoader.loadHTML(url, cookie)
    val result = ParserResult()

    parser.parseComic(mainDoc, result)
      .let { parser.parseCover(mainDoc, it) }
      .let { parser.parseTags(mainDoc, it) }
      .let { parser.parseAuthors(mainDoc, it) }
      .let { parser.parseLanguages(mainDoc, it) }
      .let { parser.parseChapters(mainDoc, it) }

    if (parser.queryElements.pagesTemplateUrl.isNotBlank()) {
      result.chapters.forEachIndexed { iChapter, chapter ->
        val baseUrl = if (chapter.link.isNotBlank()) chapter.link else url
        val updatedUrl = parser.setDomain(url, baseUrl)

        val firstPageUrl = parser.mergePageLink(updatedUrl, 1)
        val firstPageDoc = HtmlLoader.loadHTML(firstPageUrl, cookie)

        if (chapter.pages.isNotEmpty()) {
          for (pageIndex in 1..chapter.pages.size) {
            val page = chapter.pages[pageIndex - 1]
            val pageUrl = if (page.isNotBlank()) parser.setDomain(url, page)
            else parser.mergePageLink(updatedUrl, pageIndex)

            val pageDoc = HtmlLoader.loadHTML(pageUrl, cookie)
            chapter.pages[pageIndex - 1] = parser.parseChapterPage(pageDoc)
          }
        } else {
          val count = parser.parseCountPages(firstPageDoc)
          repeat(count) { chapter.pages.add("") }

          for (pageIndex in 1..count) {
            val pageUrl = parser.mergePageLink(updatedUrl, pageIndex)
            val pageDoc = HtmlLoader.loadHTML(pageUrl, cookie)
            chapter.pages[pageIndex - 1] = parser.parseChapterPage(pageDoc)
          }
        }
      }
    }

    return result
  }

  fun <T> setNotBlank(newValue: T, oldValue: T): T = when (newValue) {
    is String -> if (newValue.isNotBlank()) newValue else oldValue
    is Collection<*> -> if (newValue.isNotEmpty()) newValue else oldValue
    is Long -> if (newValue != 0L) newValue else oldValue
    else -> newValue ?: oldValue
  }

  private fun updateTags(data: ParserResult): List<Long> {
    val items = tagController.readAll()
    val itemsLower = items.map { it.name.lowercase() }
    val result = mutableListOf<Long>()

    data.tags.forEach { name ->
      val idx = itemsLower.indexOf(name.lowercase())
      if (idx == -1) {
        val rowId = tagController.create(TagCreate(name = name))
        result.add(rowId)
      } else {
        result.add(items[idx].id)
      }
    }

    return result
  }

  private fun updateAuthors(data: ParserResult): List<Long> {
    val items = authorController.readAll()
    val itemsLower = items.map { it.name.lowercase() }
    val result = mutableListOf<Long>()

    data.authors.forEach { name ->
      val idx = itemsLower.indexOf(name.lowercase())
      if (idx == -1) {
        val rowId = authorController.create(AuthorCreate(name = name))
        result.add(rowId)
      } else {
        result.add(items[idx].id)
      }
    }

    return result
  }

  private fun updateLanguages(data: ParserResult): List<Long> {
    val items = languageController.readAll()
    val itemsLower = items.map { it.name.lowercase() }
    val result = mutableListOf<Long>()

    data.languages.forEach { name ->
      val idx = itemsLower.indexOf(name.lowercase())
      if (idx == -1) {
        val rowId = languageController.create(LanguageCreate(name = name))
        result.add(rowId)
      } else {
        result.add(items[idx].id)
      }
    }

    return result
  }

  private fun updateCover(data: ParserResult, comicId: Long) {
    if (data.coverUrl.isNotBlank()) {
      comicCoverController.readByComic(comicId)?.let { cover ->
        comicCoverController.update(
          ComicCoverUpdate(id = cover.id, fromUrl = data.coverUrl)
        )
      }
    }
  }

  private fun updatePage(imageUrl: String, chapterId: Long, existingPage: ChapterPageWithFile?) {
    if (existingPage?.page == null) {
      chapterPageController.create(
        ChapterPageCreate(chapterId = chapterId, fromUrl = imageUrl)
      )
    } else if (imageUrl.isNotBlank()) {
      chapterPageController.update(
        ChapterPageUpdate(id = existingPage.page.id, fromUrl = imageUrl)
      )
    }
  }

  private fun updateChapter(
    chapterData: ParserChapterResult,
    comicId: Long,
    existingChapter: ChapterWithPages?
  ) {
    var chapterId = if (existingChapter?.chapter == null) {
      chapterController.create(ChapterCreate(comicId = comicId, name = chapterData.title))
    } else {
      chapterController.update(
        ChapterUpdate(
          id = existingChapter.chapter.id,
          name = setNotBlank(chapterData.title, existingChapter.chapter.name),
        )
      )
      existingChapter.chapter.id
    }

    chapterData.pages.forEachIndexed { i, pageUrl ->
      val existingPage = existingChapter?.pages?.getOrNull(i)
      updatePage(pageUrl, chapterId, existingPage)
    }
  }

  private fun updateChapters(data: ParserResult, comicId: Long) {
    if (data.chapters.isEmpty()) return

    val existingChapters = chapterController.readByComicAll(comicId)
    data.chapters.forEachIndexed { i, chapterData ->
      val existingChapter = existingChapters.getOrNull(i)
      updateChapter(chapterData, comicId, existingChapter)
    }
  }

  fun saveData(comicId: Long, data: ParserResult): Boolean {
    val comic = comicController.read(comicId) ?: return false

    val tagsComic = updateTags(data)
    val authorsComic = updateAuthors(data)
    val languagesComic = updateLanguages(data)

    comicController.update(
      ComicUpdate(
        id = comicId,
        name = setNotBlank(data.title, comic.name),
        annotation = setNotBlank(data.annotation, comic.annotation),
        languageId = setNotBlank(languagesComic.getOrNull(0) ?: 0L, comic.languageId),
        authors = setNotBlank(authorsComic, comic.authors),
        tags = setNotBlank(tagsComic, comic.tags),
      )
    )

    updateCover(data, comicId)
    updateChapters(data, comicId)

    return true
  }
}