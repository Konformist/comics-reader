package com.konformist.comicsreader.db.chapter

import kotlinx.serialization.Serializable

@Serializable
data class ChapterDelete(
  val id: Long,
)
