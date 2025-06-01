package com.konformist.comicsreader.db.comic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comics")
data class Comic(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(defaultValue = "(datetime('now'))") val cdate: String,
  @ColumnInfo(defaultValue = "(datetime('now'))") val mdate: String,
  @ColumnInfo(defaultValue = "") val name: String,

  /* Парсер */
  @ColumnInfo(name = "parser_id", defaultValue = "0") val parserId: Long?,

  /* Ссылка на источник */
  @ColumnInfo(name = "from_url", defaultValue = "") val fromUrl: String?,
  /* Аннотация */
  @ColumnInfo(defaultValue = "") val annotation: String?,
  /* ID языка */
  @ColumnInfo(name = "language_id", defaultValue = "0") val languageId: Long?,
  /** ID тегов */
  @ColumnInfo(defaultValue = "[]") val tags: List<Long>?,
  /** ID авторов */
  @ColumnInfo(defaultValue = "[]") val authors: List<Long>?,
)
