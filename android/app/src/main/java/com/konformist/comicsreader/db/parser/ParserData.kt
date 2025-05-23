package com.konformist.comicsreader.db.parser

interface ParserData {
  /** CSS указатель на название */
  val titleCSS: String?

  /** CSS указатель на изображение */
  val coverCSS: String?

  /** CSS указатель на страницы */
  val pagesCSS: String?

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
}