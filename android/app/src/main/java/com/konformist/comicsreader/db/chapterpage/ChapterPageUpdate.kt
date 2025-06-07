package com.konformist.comicsreader.db.chapterpage

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ChapterPageUpdate(
  val id: Long,
  var mdate: String,

  @ColumnInfo(name = "is_read") val isRead: Boolean? = false,
  @ColumnInfo(name = "file_id") var fileId: Long? = 0L,
  @ColumnInfo(name = "from_url") val fromUrl: String? = "",
)