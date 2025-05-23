package com.konformist.comicsreader.db.comiccover

import androidx.room.ColumnInfo

data class ComicCoverUpdate(
  val id: Long,
  val mdate: String,

  /* ID привязанного файла */
  @ColumnInfo(name = "file_id") val fileId: Long?,
  /* Ссылка на источник */
  @ColumnInfo(name = "from_url") val fromUrl: String?,
)