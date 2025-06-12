package com.konformist.comicsreader.data.comic

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ComicCreate(
  val name: String? = "",

  @ColumnInfo(name = "parser_id") val parserId: Long? = 0L,
  @ColumnInfo(name = "from_url") val fromUrl: String? = "",

  val annotation: String? = "",
  @ColumnInfo(name = "language_id") val languageId: Long? = 0L,
  val tags: List<Long>? = listOf(),
  val authors: List<Long>? = listOf(),
)
