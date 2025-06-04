package com.konformist.comicsreader.db.comiccover

import kotlinx.serialization.Serializable

@Serializable
data class ComicCoverDelete(
  val id: Long,
)