package com.konformist.comicsreader.db.comic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "comic_overrides")
data class ComicOverride(
  @PrimaryKey(autoGenerate = true) val id: Long?,

  val cdate: Date?,
  val mdate: Date?,

  /*
   * ID комикса
   */
  @ColumnInfo(name = "comic_id") val comicId: Long,

  /**
   * CSS указатель на название
   */
  @ColumnInfo(name = "title_css", defaultValue = "") val titleCSS: String?,
  /**
   * CSS указатель на изображение
   */
  @ColumnInfo(name = "cover_css", defaultValue = "") val coverCSS: String?,
  /**
   * CSS указатель на страницы
   */
  @ColumnInfo(name = "pages_css", defaultValue = "") val pagesCSS: String?,
  /**
   * CSS указатель на авторов
   */
  @ColumnInfo(name = "authors_css", defaultValue = "") val authorsCSS: String?,
  /**
   * CSS указатель на текст авторов
   */
  @ColumnInfo(name = "authors_text_css", defaultValue = "") val authorsTextCSS: String?,
  /**
   * CSS указатель на текст язык
   */
  @ColumnInfo(name = "language_css", defaultValue = "") val languageCSS: String?,
  /**
   * CSS указатель на теги
   */
  @ColumnInfo(name = "tags_css", defaultValue = "") val tagsCSS: String?,
  /**
   * CSS указатель на текст тегов
   */
  @ColumnInfo(name = "tags_text_css", defaultValue = "") val tagsTextCSS: String?,
)