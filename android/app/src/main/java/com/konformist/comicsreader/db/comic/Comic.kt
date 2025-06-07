package com.konformist.comicsreader.db.comic

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
  @ColumnInfo(defaultValue = "") var name: String,

  /* Парсер */
  @ColumnInfo(name = "parser_id", defaultValue = "0") var parserId: Long? = 0L,

  /* Ссылка на источник */
  @ColumnInfo(name = "from_url", defaultValue = "") var fromUrl: String? = "",
  /* Аннотация */
  @ColumnInfo(defaultValue = "") var annotation: String? = "",
  /* ID языка */
  @ColumnInfo(name = "language_id", defaultValue = "0") var languageId: Long? = 0L,
  /** ID тегов */
  @ColumnInfo(defaultValue = "[]") var tags: List<Long>? = listOf(),
  /** ID авторов */
  @ColumnInfo(defaultValue = "[]") var authors: List<Long>? = listOf(),
)
