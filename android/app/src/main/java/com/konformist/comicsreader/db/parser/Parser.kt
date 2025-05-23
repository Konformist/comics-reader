package com.konformist.comicsreader.db.parser

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parsers")
data class Parser(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(defaultValue = "(datetime('now'))") val cdate: String,
  @ColumnInfo(defaultValue = "(datetime('now'))") val mdate: String,
  @ColumnInfo val name: String,

  /** Ссылка на сайт */
  @ColumnInfo(name = "site_url", defaultValue = "") val siteUrl: String?,
  /** CSS указатель на название */
  @ColumnInfo(name = "title_css", defaultValue = "") override val titleCSS: String?,
  /** CSS указатель на изображение */
  @ColumnInfo(name = "cover_css", defaultValue = "") override val coverCSS: String?,
  /** CSS указатель на страницы */
  @ColumnInfo(name = "pages_css", defaultValue = "") override val pagesCSS: String?,
  /** CSS указатель на авторов */
  @ColumnInfo(name = "authors_css", defaultValue = "") override val authorsCSS: String?,
  /** CSS указатель на текст авторов */
  @ColumnInfo(name = "authors_text_css", defaultValue = "") override val authorsTextCSS: String?,
  /** CSS указатель на текст язык */
  @ColumnInfo(name = "language_css", defaultValue = "") override val languageCSS: String?,
  /** CSS указатель на теги */
  @ColumnInfo(name = "tags_css", defaultValue = "") override val tagsCSS: String?,
  /** CSS указатель на текст тегов */
  @ColumnInfo(name = "tags_text_css", defaultValue = "") override val tagsTextCSS: String?,
) : ParserData