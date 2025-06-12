package com.konformist.comicsreader.data.parserconfig

interface QueryData {
  /** CSS указатель на название */
  val titleCSS: String?

  /** CSS аннотации */
  val annotationCSS: String?

  /** CSS указатель на изображение */
  val coverCSS: String?

  /** CSS указатель на авторов */
  val authorsCSS: String?

  /** CSS указатель на текст авторов */
  val authorsTextCSS: String?

  /** CSS указатель на текст язык */
  val languageCSS: String?

  /** CSS указатель на теги */
  val tagsCSS: String?

  /** CSS указатель на текст тегов */
  val tagsTextCSS: String?

  /** CSS указатель на главы */
  val chaptersCSS: String?

  /** CSS указатель на названия главы */
  val chaptersTitleCSS: String?

  /** Шаблон страницы */
  val pagesTemplateUrl: String?

  /** CSS указатель на страницы */
  val pagesCSS: String?

  /** CSS указатель на изображение страницы */
  val pagesImageCSS: String?
}