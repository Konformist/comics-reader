package com.konformist.comicsreader.data.chapterpage

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ChapterPageFileUpdate(
  val id: Long,
  val mdate: String? = null,

  @ColumnInfo(name = "file_id") var fileId: Long,
)