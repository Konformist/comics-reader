package com.konformist.comicsreader.db.parser

import androidx.room.ColumnInfo

data class ParserCreate(
  val name: String,
  /** Ссылка на сайт */
  @ColumnInfo(name = "site_url") val siteUrl: String,
  /** CSS указатель на название */
  @ColumnInfo(name = "title_css") override val titleCSS: String?,
  /** CSS указатель на изображение */
  @ColumnInfo(name = "cover_css") override val coverCSS: String?,
  /** CSS указатель на страницы */
  @ColumnInfo(name = "pages_css") override val pagesCSS: String?,
  /** CSS указатель на авторов */
  @ColumnInfo(name = "authors_css") override val authorsCSS: String?,
  /** CSS указатель на текст авторов */
  @ColumnInfo(name = "authors_text_css") override val authorsTextCSS: String?,
  /** CSS указатель на текст язык */
  @ColumnInfo(name = "language_css") override val languageCSS: String?,
  /** CSS указатель на теги */
  @ColumnInfo(name = "tags_css") override val tagsCSS: String?,
  /** CSS указатель на текст тегов */
  @ColumnInfo(name = "tags_text_css") override val tagsTextCSS: String?,
) : ParserData