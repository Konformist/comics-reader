package com.konformist.comicsreader.db.parser

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "parsers")
data class Parser(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(defaultValue = "(datetime('now'))") val cdate: String,
  @ColumnInfo(defaultValue = "(datetime('now'))") val mdate: String,
  @ColumnInfo val name: String,

  /** Ссылка на сайт */
  @ColumnInfo(name = "site_url", defaultValue = "") val siteUrl: String?,

  @ColumnInfo(name = "title_css", defaultValue = "") override val titleCSS: String?,
  @ColumnInfo(name = "annotation_css", defaultValue = "") override val annotationCSS: String?,
  @ColumnInfo(name = "cover_css", defaultValue = "") override val coverCSS: String?,
  @ColumnInfo(name = "authors_css", defaultValue = "") override val authorsCSS: String?,
  @ColumnInfo(name = "authors_text_css", defaultValue = "") override val authorsTextCSS: String?,
  @ColumnInfo(name = "language_css", defaultValue = "") override val languageCSS: String?,
  @ColumnInfo(name = "tags_css", defaultValue = "") override val tagsCSS: String?,
  @ColumnInfo(name = "tags_text_css", defaultValue = "") override val tagsTextCSS: String?,
  @ColumnInfo(name = "chapters_css", defaultValue = "") override val chaptersCSS: String?,
  @ColumnInfo(
    name = "chapters_title_css",
    defaultValue = ""
  ) override val chaptersTitleCSS: String?,
  @ColumnInfo(
    name = "pages_template_url",
    defaultValue = ""
  ) override val pagesTemplateUrl: String?,
  @ColumnInfo(name = "pages_css", defaultValue = "") override val pagesCSS: String?,
  @ColumnInfo(name = "pages_image_css", defaultValue = "") override val pagesImageCSS: String?,
) : ParserData