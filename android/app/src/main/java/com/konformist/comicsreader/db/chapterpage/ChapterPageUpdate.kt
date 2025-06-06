package com.konformist.comicsreader.db.chapterpage

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ChapterPageUpdate(
  val id: Long,
  val mdate: String,

  @ColumnInfo(name = "is_read") val isRead: Boolean?,
  @ColumnInfo(name = "file_id") val fileId: Long?,
  @ColumnInfo(name = "from_url") val fromUrl: String?,
)