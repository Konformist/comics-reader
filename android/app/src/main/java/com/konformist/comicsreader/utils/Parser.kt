package com.konformist.comicsreader.utils

import com.konformist.comicsreader.db.author.Author
import com.konformist.comicsreader.db.chapter.Chapter
import com.konformist.comicsreader.db.chapterpage.ChapterPage
import com.konformist.comicsreader.db.comic.Comic
import com.konformist.comicsreader.db.comiccover.ComicCover
import com.konformist.comicsreader.db.comicoverride.ComicOverride
import com.konformist.comicsreader.db.language.Language
import com.konformist.comicsreader.db.parser.ParserConfig
import com.konformist.comicsreader.db.parser.ParserData
import com.konformist.comicsreader.db.tag.Tag
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
  ) : ParserData

  companion object {
    fun getDocument(html: String): Document {
      return Jsoup.parse(html)
    }
  }

  private fun getQuery(override: String?, parser: String?): String {
    return when {
      override?.isNotBlank() == true -> override
      parser?.isNotBlank() == true -> parser
      else -> ""
    }
  }

  private var queryElements = QueryElements()

  fun mergeQuery(parser: ParserConfig, override: ComicOverride): Parser {
    queryElements = QueryElements(
      titleCSS = getQuery(override.titleCSS, parser.titleCSS),
      annotationCSS = getQuery(override.annotationCSS, parser.annotationCSS),
      coverCSS = getQuery(override.coverCSS, parser.coverCSS),
      authorsCSS = getQuery(override.authorsCSS, parser.authorsCSS),
      authorsTextCSS = getQuery(override.authorsTextCSS, parser.authorsTextCSS),
      languageCSS = getQuery(override.languageCSS, parser.languageCSS),
      tagsCSS = getQuery(override.tagsCSS, parser.tagsCSS),
      tagsTextCSS = getQuery(override.tagsTextCSS, parser.tagsTextCSS),
      chaptersCSS = getQuery(override.chaptersCSS, parser.chaptersCSS),
      chaptersTitleCSS = getQuery(override.chaptersTitleCSS, parser.chaptersTitleCSS),
      pagesTemplateUrl = getQuery(override.pagesTemplateUrl, parser.pagesTemplateUrl),
      pagesCSS = getQuery(override.pagesCSS, parser.pagesCSS),
      pagesImageCSS = getQuery(override.pagesImageCSS, parser.pagesImageCSS),
    )
    return this
  }

  private fun Document.parseElement(textQuery: String, action: (String) -> Unit) {
    val result = selectFirst(textQuery)?.let { text().trim() }
    result?.takeIf { it.isNotBlank() }?.let {
      action(it.trim())
    }
  }

  private fun <T> Document.parseElements(
    itemsQuery: String,
    textQuery: String,
    action: (String) -> T
  ): List<T> {
    val results = mutableListOf<T>()
    select(itemsQuery).forEach { item ->
      val text = if (textQuery.isBlank()) item.text()
      else item.selectFirst(textQuery)?.text()

      text?.takeIf { it.isNotBlank() }?.let {
        results.add(action(it.trim()))
      }
    }
    return results
  }

  val comic: Comic = Comic(
    id = 0L,
    cdate = "",
    mdate = "",
    name = "",
  )

  fun parseComic(document: Document): Parser {
    document.parseElement(queryElements.titleCSS) { text -> comic.name = text }
    document.parseElement(queryElements.annotationCSS) { text -> comic.annotation = text }
    return this
  }

  val cover: ComicCover = ComicCover(
    id = 0L,
    cdate = "",
    mdate = "",
    comicId = 0L,
  )

  fun parseCover(document: Document): Parser {
    document.parseElement(queryElements.coverCSS) { text -> cover.fromUrl = text }
    return this
  }

  val tags: MutableList<Tag> = mutableListOf()

  fun parseTags(document: Document): Parser {
    tags.addAll(
      document.parseElements(queryElements.tagsCSS, queryElements.tagsTextCSS) { text ->
        Tag(
          id = 0L,
          cdate = "",
          mdate = "",
          name = text,
        )
      }
    )

    return this
  }

  val authors: MutableList<Author> = mutableListOf()

  fun parseAuthors(document: Document): Parser {
    authors.addAll(
      document.parseElements(queryElements.authorsCSS, queryElements.authorsTextCSS) { text ->
        Author(
          id = 0L,
          cdate = "",
          mdate = "",
          name = text,
        )
      }
    )

    return this
  }

  val languages: MutableList<Language> = mutableListOf()

  fun parseLanguages(document: Document): Parser {
    document.parseElement(queryElements.languageCSS) { text ->
      languages.add(
        Language(
          id = 0L,
          cdate = "",
          mdate = "",
          name = text,
        )
      )
    }

    return this
  }

  val chapterLinks: MutableList<String> = mutableListOf()
  val chapters: MutableList<Chapter> = mutableListOf()
  val pages: MutableList<MutableList<ChapterPage>> = mutableListOf()

  private fun addChapter(title: String? = "") {
    chapters.add(
      Chapter(
        id = 0L,
        cdate = "",
        mdate = "",
        name = title,
        comicId = 0L,
      )
    )
  }

  fun parseChapters(document: Document): Parser {
    if (queryElements.chaptersCSS.isNotBlank()) {
      document.select(queryElements.chaptersCSS).forEach { element ->
        val link = element.selectFirst("a")?.attr("href")?.trim() ?: ""
        chapterLinks.add(link)

        val title = element.selectFirst(queryElements.chaptersTitleCSS)?.text()?.trim() ?: ""
        addChapter(title)
        pages.add(mutableListOf())
      }
    } else if (queryElements.pagesCSS.isNotBlank()) {
      addChapter()

      val pagesList = mutableListOf<ChapterPage>()

      document.select(queryElements.pagesCSS).forEach { _ ->
        pagesList.add(
          ChapterPage(
            id = 0L,
            cdate = "",
            mdate = "",
            chapterId = 0L,
            isRead = false,
            fileId = 0L,
            fromUrl = "",
          )
        )
      }

      pages.add(pagesList)
    }

    return this
  }

  fun parseChapterPage() {

  }
}