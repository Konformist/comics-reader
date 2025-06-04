package com.konformist.comicsreader.db.comicoverride

import kotlinx.serialization.Serializable

@Serializable
data class ComicOverrideDelete(
  val id: Long,
)