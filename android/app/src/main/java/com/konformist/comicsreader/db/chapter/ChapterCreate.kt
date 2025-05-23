package com.konformist.comicsreader.db.chapter

import androidx.room.ColumnInfo

data class ChapterCreate(
  @ColumnInfo val name: String?,
  @ColumnInfo(name = "comic_id") val comicId: Long
)
