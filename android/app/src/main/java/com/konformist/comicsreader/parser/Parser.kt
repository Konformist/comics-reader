package com.konformist.comicsreader.parser

import android.net.Uri
import com.konformist.comicsreader.data.comicoverride.ComicOverride
import com.konformist.comicsreader.data.parserconfig.ParserConfig
import com.konformist.comicsreader.data.parserconfig.QueryData
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Parser {
  data class QueryElements(
    override var titleCSS: String = "",
    override var annotationCSS: String = "",
    override var coverCSS: String = "",
    override var authorsCSS: String = "",
    override var authorsTextCSS: String = "",
    override var languageCSS: String = "",
    override var tagsCSS: String = "",
    override var tagsTextCSS: String = "",
    override var chaptersCSS: String = "",
    override var chaptersTitleCSS: String = "",
    override var pagesTemplateUrl: String = "",
    override var pagesCSS: String = "",
    override var pagesImageCSS: String = "",
  ) : QueryData

  data class ResultChapter(
    val title: String = "",
    val pages: MutableList<String> = mutableListOf<String>(),
  )

  data class Result(
    var title: String = "",
    var annotation: String = "",
    var coverUrl: String = "",
    val tags: MutableList<String> = mutableListOf<String>(),
    val authors: MutableList<String> = mutableListOf<String>(),
    val languages: MutableList<String> = mutableListOf<String>(),
    val chapters: MutableList<ResultChapter> = mutableListOf<ResultChapter>(),
  )

  companion object {
    val indexPageTemp = "<PAGE_ID>"

    fun getDocument(html: String): Document {
      return Jsoup.parse(html)
    }
  }

  fun mergePageLink(link: String, index: Int): String {
    val pathTemp = queryElements.pagesTemplateUrl.replace(indexPageTemp, index.toString())

    return Uri.Builder()
      .path(link)
      .appendPath(pathTemp)
      .toString()
  }

  private fun getQuery(override: String, parser: String): String {
    return if (override.isNotBlank()) override
    else if (parser.isNotBlank()) parser
    else ""
  }

  var queryElements = QueryElements()

  fun mergeQuery(config: ParserConfig, override: ComicOverride): Parser {
    queryElements = QueryElements(
      titleCSS = getQuery(override.titleCSS, config.titleCSS),
      annotationCSS = getQuery(override.annotationCSS, config.annotationCSS),
      coverCSS = getQuery(override.coverCSS, config.coverCSS),
      authorsCSS = getQuery(override.authorsCSS, config.authorsCSS),
      authorsTextCSS = getQuery(override.authorsTextCSS, config.authorsTextCSS),
      languageCSS = getQuery(override.languageCSS, config.languageCSS),
      tagsCSS = getQuery(override.tagsCSS, config.tagsCSS),
      tagsTextCSS = getQuery(override.tagsTextCSS, config.tagsTextCSS),
      chaptersCSS = getQuery(override.chaptersCSS, config.chaptersCSS),
      chaptersTitleCSS = getQuery(override.chaptersTitleCSS, config.chaptersTitleCSS),
      pagesTemplateUrl = getQuery(override.pagesTemplateUrl, config.pagesTemplateUrl),
      pagesCSS = getQuery(override.pagesCSS, config.pagesCSS),
      pagesImageCSS = getQuery(override.pagesImageCSS, config.pagesImageCSS),
    )
    return this
  }

  private fun Document.parseElement(textQuery: String, action: (String) -> Unit) {
    val result = if (textQuery.isBlank()) ""
    else selectFirst(textQuery)?.text()?.trim()

    result?.takeIf { it.isNotBlank() }?.let { action(it) }
  }

  private fun <T> Document.parseElements(
    itemsQuery: String,
    textQuery: String,
    action: (String) -> T
  ): List<T> {
    val results = mutableListOf<T>()
    if (itemsQuery.isBlank()) return results

    select(itemsQuery).forEach { item ->
      val text = if (textQuery.isBlank()) item.text()
      else item.selectFirst(textQuery)?.text()

      text?.takeIf { it.isNotBlank() }?.let {
        results.add(action(it.trim()))
      }
    }
    return results
  }

  val result: Result = Result()
  val chapterLinks: MutableList<String> = mutableListOf()

  fun parseComic(document: Document): Parser {
    document.parseElement(queryElements.titleCSS) { text -> result.title = text }
    document.parseElement(queryElements.annotationCSS) { text -> result.annotation = text }
    return this
  }

  fun parseCover(document: Document): Parser {
    document.parseElement(queryElements.coverCSS) { text -> result.coverUrl = text }
    return this
  }

  fun parseTags(document: Document): Parser {
    result.tags.addAll(
      document.parseElements(queryElements.tagsCSS, queryElements.tagsTextCSS) { it }
    )

    return this
  }

  fun parseAuthors(document: Document): Parser {
    result.authors.addAll(
      document.parseElements(queryElements.authorsCSS, queryElements.authorsTextCSS) { it }
    )

    return this
  }

  fun parseLanguages(document: Document): Parser {
    document.parseElement(queryElements.languageCSS) { result.languages.add(it) }

    return this
  }

  fun parseChapters(document: Document): Parser {
    if (queryElements.chaptersCSS.isNotBlank()) {
      document.select(queryElements.chaptersCSS).forEach { element ->
        val link = element.selectFirst("a")?.attr("href")?.trim() ?: ""
        chapterLinks.add(link)

        document.parseElement(queryElements.chaptersTitleCSS) {
          result.chapters.add(ResultChapter(title = it))
        }
      }
    } else if (queryElements.pagesCSS.isNotBlank()) {
      val chapter = ResultChapter()
      result.chapters.add(chapter)

      document.select(queryElements.pagesCSS).forEach { _ ->
        chapter.pages.add("")
      }
    }

    return this
  }

  fun parseCountPages(document: Document, chapterIndex: Int): Parser {
    var count = 0
    document.parseElement(queryElements.pagesCSS) { count = it.toInt() }

    var i = 0
    while (i < count) {
      result.chapters[chapterIndex].pages.add("")
      i++
    }

    return this
  }

  fun parseChapterPage(document: Document, chapterIndex: Int, pageIndex: Int): Parser {
    document.parseElement(queryElements.pagesImageCSS) {
      result.chapters[chapterIndex].pages[pageIndex] = it
    }

    return this
  }
}