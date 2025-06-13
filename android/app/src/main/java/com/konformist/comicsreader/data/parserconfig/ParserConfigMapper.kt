package com.konformist.comicsreader.data.parserconfig

import com.konformist.comicsreader.utils.DatesUtils

fun ParserConfigUpdate.merge(value: ParserConfig): ParserConfigUpdate {
  return this.copy(
    mdate = DatesUtils.nowFormatted(),
    name = this.name ?: value.name,
    siteUrl = this.siteUrl ?: value.siteUrl,
    titleCSS = this.titleCSS ?: value.titleCSS,
    annotationCSS = this.annotationCSS ?: value.annotationCSS,
    coverCSS = this.coverCSS ?: value.coverCSS,
    authorsCSS = this.authorsCSS ?: value.authorsCSS,
    authorsTextCSS = this.authorsTextCSS ?: value.authorsTextCSS,
    languageCSS = this.languageCSS ?: value.languageCSS,
    tagsCSS = this.tagsCSS ?: value.tagsCSS,
    tagsTextCSS = this.tagsTextCSS ?: value.tagsTextCSS,
    chaptersCSS = this.chaptersCSS ?: value.chaptersCSS,
    chaptersTitleCSS = this.chaptersTitleCSS ?: value.chaptersTitleCSS,
    pagesTemplateUrl = this.pagesTemplateUrl ?: value.pagesTemplateUrl,
    pagesCSS = this.pagesCSS ?: value.pagesCSS,
    pagesImageCSS = this.pagesImageCSS ?: value.pagesImageCSS,
  )
}