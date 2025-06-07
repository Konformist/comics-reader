package com.konformist.comicsreader.db.comiccover

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ComicCoverCreate(
  /* ID комикса */
  @ColumnInfo(name = "comic_id") val comicId: Long,
  /* ID привязанного файла */
  @ColumnInfo(name = "file_id") val fileId: Long? = 0L,
  /* Ссылка на источник */
  @ColumnInfo(name = "from_url") val fromUrl: String? = "",
)