package com.konformist.comicsreader.db.comiccover

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ComicCoverUpdate(
  val id: Long,
  var mdate: String,

  @ColumnInfo(name = "file_id") var fileId: Long? = 0L,
  @ColumnInfo(name = "from_url") val fromUrl: String? = "",
)