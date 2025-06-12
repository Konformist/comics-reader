package com.konformist.comicsreader.data.tag

import kotlinx.serialization.Serializable

@Serializable
data class TagCreate(
  val name: String? = "",
)