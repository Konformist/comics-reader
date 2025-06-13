package com.konformist.comicsreader.data.comic

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ComicUpdate(
  val id: Long,
  val mdate: String? = null,
  val name: String? = null,

  @ColumnInfo(name = "parser_id") val parserId: Long? = null,
  @ColumnInfo(name = "from_url") val fromUrl: String? = null,
  val annotation: String? = null,
  @ColumnInfo(name = "language_id") val languageId: Long? = null,
  val tags: List<Long>? = null,
  val authors: List<Long>? = null,
)
