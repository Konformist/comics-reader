package com.konformist.comicsreader.db.comic

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ComicUpdate(
  val id: Long,
  var mdate: String,
  val name: String,

  @ColumnInfo(name = "parser_id") val parserId: Long?,
  @ColumnInfo(name = "from_url") val fromUrl: String?,
  val annotation: String?,
  @ColumnInfo(name = "language_id") val languageId: Long?,
  val tags: List<Long>?,
  val authors: List<Long>?,
)
