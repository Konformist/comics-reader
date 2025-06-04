package com.konformist.comicsreader.db.tag

import kotlinx.serialization.Serializable

@Serializable
data class TagDelete(
  val id: Long,
)