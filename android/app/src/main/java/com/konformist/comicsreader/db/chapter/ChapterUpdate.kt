package com.konformist.comicsreader.db.chapter

import kotlinx.serialization.Serializable

@Serializable
data class ChapterUpdate(
  val id: Long,
  val mdate: String,
  val name: String?,
)
