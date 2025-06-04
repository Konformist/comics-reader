package com.konformist.comicsreader.db.tag

import kotlinx.serialization.Serializable

@Serializable
data class TagCreate(
  val name: String,
)