package com.konformist.comicsreader.db.chapterpage

import kotlinx.serialization.Serializable

@Serializable
data class ChapterPageDelete(
  val id: Long,
)