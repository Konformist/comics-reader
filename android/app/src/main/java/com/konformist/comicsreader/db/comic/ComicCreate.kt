package com.konformist.comicsreader.db.comic

import androidx.room.ColumnInfo

data class ComicCreate(
  val name: String,

  /* Парсер */
  @ColumnInfo(name = "parser_id") val parserId: Long?,
  /* Ссылка на источник */
  @ColumnInfo(name = "from_url") val fromUrl: String?,
  /* ID языка */
  @ColumnInfo(name = "language_id") val languageId: Long?,
  /** ID тегов */
  val tags: List<Long>?,
  /** ID авторов */
  val authors: List<Long>?,
)
