package com.konformist.comicsreader.data.chapter

import kotlinx.serialization.Serializable

@Serializable
data class ChapterUpdate(
  val id: Long,
  val mdate: String? = "",
  val name: String,
)
