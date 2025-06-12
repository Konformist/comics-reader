package com.konformist.comicsreader.data.comic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "comics")
data class Comic(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(defaultValue = "(datetime('now'))") val cdate: String,
  @ColumnInfo(defaultValue = "(datetime('now'))") val mdate: String,
  @ColumnInfo(defaultValue = "") val name: String,

  /* Парсер */
  @ColumnInfo(name = "parser_id") val parserId: Long,

  /* Ссылка на источник */
  @ColumnInfo(name = "from_url") val fromUrl: String,
  /* Аннотация */
  @ColumnInfo() val annotation: String,
  /* ID языка */
  @ColumnInfo(name = "language_id") val languageId: Long,
  /** ID тегов */
  @ColumnInfo() val tags: List<Long>,
  /** ID авторов */
  @ColumnInfo() val authors: List<Long>,
)
