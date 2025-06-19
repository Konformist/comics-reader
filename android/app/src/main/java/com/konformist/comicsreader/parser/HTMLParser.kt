package com.konformist.comicsreader.parser

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import java.net.URL

class HTMLParser(val queryElements: QueryElements) {
  companion object {
    private const val PAGE_ID = "<PAGE_ID>"

    fun getDocument(html: String): Document = Ksoup.parse(html)
  }

  private fun getFullDomain(url: String): String? {
    return try {
      val parsedUrl = URL(url)
      "${parsedUrl.protocol}://${parsedUrl.host}"
    } catch (e: Exception) {
      null
    }
  }

  fun setDomain(baseUrl: String, relativeUrl: String): String {
    val baseDomain = getFullDomain(baseUrl) ?: ""
    return when {
      relativeUrl.contains("://localhost") -> relativeUrl.replace(
        Regex("""http://localhost(:\d+)?"""),
        baseDomain
      )

      relativeUrl.contains("https://") || relativeUrl.contains("http://") -> relativeUrl
      else -> mergeLink(baseDomain, relativeUrl)
    }
  }

  fun mergeLink(baseUrl: String, part: String): String {
    return baseUrl.trimEnd('/') + "/" + part.trimStart('/')
  }

  fun mergePageLink(baseUrl: String, pageIndex: Int): String {
    val pagePart = queryElements.pagesTemplateUrl.replace(PAGE_ID, pageIndex.toString())
    return mergeLink(baseUrl, pagePart)
  }

  // Приватные хелперы для безопасного парсинга элементов
  private fun Document.selectSrc(cssQuery: String): String? {
    return if (cssQuery.isBlank()) null
    else selectFirst(cssQuery)?.attr("src")?.trim()?.takeIf { it.isNotBlank() }
  }

  // Приватные хелперы для безопасного парсинга элементов
  private fun Document.selectHref(cssQuery: String): String? {
    return if (cssQuery.isBlank()) null
    else selectFirst(cssQuery)?.attr("href")?.trim()?.takeIf { it.isNotBlank() }
  }

  // Приватные хелперы для безопасного парсинга элементов
  private fun Document.selectFirstText(cssQuery: String): String? {
    return if (cssQuery.isBlank()) null
    else selectFirst(cssQuery)?.text()?.trim()?.takeIf { it.isNotBlank() }
  }

  private fun Document.selectElementsTexts(itemsQuery: String, textQuery: String): List<String> {
    if (itemsQuery.isBlank()) return emptyList()

    return select(itemsQuery).mapNotNull { elem ->
      val text = if (textQuery.isBlank()) elem.text()
      else elem.selectFirst(textQuery)?.text()
      text?.trim()?.takeIf { it.isNotBlank() }
    }
  }

  fun parseComic(document: Document, result: ParserResult = ParserResult()): ParserResult {
    result.title = document.selectFirstText(queryElements.titleCSS) ?: ""
    result.annotation = document.selectFirstText(queryElements.annotationCSS) ?: ""
    return result
  }

  fun parseCover(document: Document, result: ParserResult): ParserResult {
    result.coverUrl = document.selectSrc(queryElements.coverCSS) ?: ""
    return result
  }

  fun parseTags(document: Document, result: ParserResult): ParserResult {
    result.tags.addAll(
      document.selectElementsTexts(
        queryElements.tagsCSS,
        queryElements.tagsTextCSS
      )
    )
    return result
  }

  fun parseAuthors(document: Document, result: ParserResult): ParserResult {
    result.authors.addAll(
      document.selectElementsTexts(
        queryElements.authorsCSS,
        queryElements.authorsTextCSS
      )
    )
    return result
  }

  fun parseLanguages(document: Document, result: ParserResult): ParserResult {
    document.selectFirstText(queryElements.languageCSS)?.let {
      result.languages.add(it)
    }
    return result
  }

  fun parseChapters(document: Document, result: ParserResult): ParserResult {
    if (queryElements.chaptersCSS.isNotBlank()) {
      val chapters = document.select(queryElements.chaptersCSS)
      chapters.forEach { element ->
        val linkSelector = if (queryElements.chaptersTitleCSS.isBlank()) "a"
        else queryElements.chaptersTitleCSS

        val link = element.selectFirst(linkSelector)?.attr("href")?.trim().orEmpty()
        val title = element.selectFirst(queryElements.chaptersTitleCSS)?.text()?.trim().orEmpty()

        result.chapters.add(
          ParserChapterResult(
            title = title,
            link = link
          )
        )
      }
    } else if (queryElements.pagesCSS.isNotBlank()) {
      val chapter = ParserChapterResult()
      result.chapters.add(chapter)

      val pages = document.select(queryElements.pagesCSS)
      pages.forEach { element ->
        val link = element.selectFirst("a")?.attr("href").orEmpty()
        chapter.pages.add(link)
      }
    }

    return result
  }

  fun parseCountPages(document: Document): Int {
    val countText = document.selectFirstText(queryElements.pagesCSS)
    return countText?.toIntOrNull() ?: 0
  }

  fun parseChapterPage(document: Document): String {
    return document.selectSrc(queryElements.pagesImageCSS).orEmpty()
  }
}