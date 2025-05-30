package com.konformist.comicsreader.db.comiccover

import androidx.room.ColumnInfo

data class ComicCoverCreate(
  /* ID комикса */
  @ColumnInfo(name = "comic_id") val comicId: Long,
  /* ID привязанного файла */
  @ColumnInfo(name = "file_id") val fileId: Long?,
  /* Ссылка на источник */
  @ColumnInfo(name = "from_url") val fromUrl: String?,
)