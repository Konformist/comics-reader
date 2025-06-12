package com.konformist.comicsreader.data.chapterpage

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ChapterPageUpdate(
  val id: Long,
  val mdate: String? = null,

  @ColumnInfo(name = "is_read") val isRead: Boolean? = null,
  @ColumnInfo(name = "from_url") val fromUrl: String? = null,
)