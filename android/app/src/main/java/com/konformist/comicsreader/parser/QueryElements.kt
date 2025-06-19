package com.konformist.comicsreader.parser

import com.konformist.comicsreader.data.comicoverride.ComicOverride
import com.konformist.comicsreader.data.parserconfig.ParserConfig
import com.konformist.comicsreader.data.parserconfig.QueryData

data class QueryElements(
  override var titleCSS: String,
  override var annotationCSS: String,
  override var coverCSS: String,
  override var authorsCSS: String,
  override var authorsTextCSS: String,
  override var languageCSS: String,
  override var tagsCSS: String,
  override var tagsTextCSS: String,
  override var chaptersCSS: String,
  override var chaptersTitleCSS: String,
  override var pagesTemplateUrl: String,
  override var pagesCSS: String,
  override var pagesImageCSS: String,
) : QueryData {
  companion object {
    private fun getQuery(override: String, parser: String): String = when {
      override.isNotBlank() -> override
      parser.isNotBlank() -> parser
      else -> ""
    }

    fun from(config: ParserConfig, override: ComicOverride): QueryElements = QueryElements(
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
  }
}
