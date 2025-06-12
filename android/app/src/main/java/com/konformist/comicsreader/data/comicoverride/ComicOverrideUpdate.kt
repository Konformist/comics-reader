package com.konformist.comicsreader.data.comicoverride

import androidx.room.ColumnInfo
import com.konformist.comicsreader.data.parserconfig.QueryData
import kotlinx.serialization.Serializable

@Serializable
data class ComicOverrideUpdate(
  val id: Long,
  val mdate: String? = null,

  @ColumnInfo(name = "title_css") override val titleCSS: String? = null,
  @ColumnInfo(name = "annotation_css") override val annotationCSS: String? = null,
  @ColumnInfo(name = "cover_css") override val coverCSS: String? = null,
  @ColumnInfo(name = "authors_css") override val authorsCSS: String? = null,
  @ColumnInfo(name = "authors_text_css") override val authorsTextCSS: String? = null,
  @ColumnInfo(name = "language_css") override val languageCSS: String? = null,
  @ColumnInfo(name = "tags_css") override val tagsCSS: String? = null,
  @ColumnInfo(name = "tags_text_css") override val tagsTextCSS: String? = null,
  @ColumnInfo(name = "chapters_css") override val chaptersCSS: String? = null,
  @ColumnInfo(name = "chapters_title_css") override val chaptersTitleCSS: String? = null,
  @ColumnInfo(name = "pages_template_url") override val pagesTemplateUrl: String? = null,
  @ColumnInfo(name = "pages_css") override val pagesCSS: String? = null,
  @ColumnInfo(name = "pages_image_css") override val pagesImageCSS: String? = null,
) : QueryData