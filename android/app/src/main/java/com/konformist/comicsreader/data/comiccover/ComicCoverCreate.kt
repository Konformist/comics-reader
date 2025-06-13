package com.konformist.comicsreader.data.comiccover

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ComicCoverCreate(
  @ColumnInfo(name = "comic_id") val comicId: Long,
  @ColumnInfo(name = "file_id") val fileId: Long? = 0L,
  @ColumnInfo(name = "from_url") val fromUrl: String? = "",
)