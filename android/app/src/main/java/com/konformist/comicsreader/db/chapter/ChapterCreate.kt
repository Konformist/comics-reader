package com.konformist.comicsreader.db.chapter

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class ChapterCreate(
  val name: String?,
  @ColumnInfo(name = "comic_id") val comicId: Long
)
