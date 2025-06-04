package com.konformist.comicsreader.db.chapterpage

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ChapterPageUpdate(
  val id: Long,
  val mdate: String,

  /* Прочитано */
  @ColumnInfo(name = "is_read") val isRead: Boolean?,
  /* ID привязанного файла */
  @ColumnInfo(name = "file_id") val fileId: Long?,
  /* Ссылка на источник */
  @ColumnInfo(name = "from_url") val fromUrl: String?,
)