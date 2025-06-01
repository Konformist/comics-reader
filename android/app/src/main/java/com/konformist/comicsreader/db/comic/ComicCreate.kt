package com.konformist.comicsreader.db.comic

import androidx.room.ColumnInfo

data class ComicCreate(
  val name: String,

  @ColumnInfo(name = "parser_id") val parserId: Long?,
  @ColumnInfo(name = "from_url") val fromUrl: String?,

  val annotation: String?,
  @ColumnInfo(name = "language_id") val languageId: Long?,
  val tags: List<Long>?,
  val authors: List<Long>?,
)
