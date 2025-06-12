package com.konformist.comicsreader.data.parserconfig

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "parsers")
data class ParserConfig(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(defaultValue = "(datetime('now'))") val cdate: String,
  @ColumnInfo(defaultValue = "(datetime('now'))") val mdate: String,
  @ColumnInfo() val name: String,

  /** Ссылка на сайт */
  @ColumnInfo(name = "site_url") val siteUrl: String,

  @ColumnInfo(name = "title_css") override val titleCSS: String,
  @ColumnInfo(name = "annotation_css") override val annotationCSS: String,
  @ColumnInfo(name = "cover_css") override val coverCSS: String,
  @ColumnInfo(name = "authors_css") override val authorsCSS: String,
  @ColumnInfo(name = "authors_text_css") override val authorsTextCSS: String,
  @ColumnInfo(name = "language_css") override val languageCSS: String,
  @ColumnInfo(name = "tags_css") override val tagsCSS: String,
  @ColumnInfo(name = "tags_text_css") override val tagsTextCSS: String,
  @ColumnInfo(name = "chapters_css") override val chaptersCSS: String,
  @ColumnInfo(name = "chapters_title_css") override val chaptersTitleCSS: String,
  @ColumnInfo(name = "pages_template_url") override val pagesTemplateUrl: String,
  @ColumnInfo(name = "pages_css") override val pagesCSS: String,
  @ColumnInfo(name = "pages_image_css") override val pagesImageCSS: String,
) : QueryData