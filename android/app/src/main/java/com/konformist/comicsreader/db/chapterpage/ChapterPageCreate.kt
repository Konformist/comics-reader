package com.konformist.comicsreader.db.chapterpage

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ChapterPageCreate(
  @ColumnInfo(name = "chapter_id") val chapterId: Long,
  @ColumnInfo(name = "is_read") val isRead: Boolean? = false,
  @ColumnInfo(name = "file_id") val fileId: Long? = 0L,
  @ColumnInfo(name = "from_url") val fromUrl: String? = "",
)