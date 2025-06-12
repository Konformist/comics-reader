package com.konformist.comicsreader.data.chapter

import androidx.room.ColumnInfo

data class ChapterUpdateComic (
  val id: Long,
  val mdate: String? = "",
  @ColumnInfo(name = "comic_id") val comicId: Long,
)