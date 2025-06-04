package com.konformist.comicsreader.db.comic

import kotlinx.serialization.Serializable

@Serializable
data class ComicDelete(
  val id: Long,
)
