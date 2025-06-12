package com.konformist.comicsreader.data.comiccover

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ComicCoverUpdate(
  val id: Long,
  val mdate: String? = null,

  /* Ссылка на источник */
  @ColumnInfo(name = "from_url") val fromUrl: String,
)