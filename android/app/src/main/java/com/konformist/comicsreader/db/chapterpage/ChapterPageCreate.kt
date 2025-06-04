package com.konformist.comicsreader.db.chapterpage

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ChapterPageCreate(
  /* ID главы */
  @ColumnInfo(name = "chapter_id") val chapterId: Long,
  /* ID привязанного файла */
  @ColumnInfo(name = "file_id") val fileId: Long?,
  /* Ссылка на источник */
  @ColumnInfo(name = "from_url") val fromUrl: String?,
)