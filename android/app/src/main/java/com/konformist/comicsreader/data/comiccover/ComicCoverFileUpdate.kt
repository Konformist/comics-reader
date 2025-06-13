package com.konformist.comicsreader.data.comiccover

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ComicCoverFileUpdate(
  val id: Long,
  val mdate: String? = null,

  /* ID привязанного файла */
  @ColumnInfo(name = "file_id") val fileId: Long,
)