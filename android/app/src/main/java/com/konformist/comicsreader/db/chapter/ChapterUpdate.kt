package com.konformist.comicsreader.db.chapter

import androidx.room.ColumnInfo

data class ChapterUpdate(
  val id: Long,
  val mdate: String,
  @ColumnInfo val name: String?,
)
