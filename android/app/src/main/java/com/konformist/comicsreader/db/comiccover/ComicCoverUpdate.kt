package com.konformist.comicsreader.db.comiccover

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ComicCoverUpdate(
  val id: Long,
  var mdate: String,

  /* ID привязанного файла */
  @ColumnInfo(name = "file_id") var fileId: Long? = 0L,
  /* Ссылка на источник */
  @ColumnInfo(name = "from_url") val fromUrl: String? = "",
)