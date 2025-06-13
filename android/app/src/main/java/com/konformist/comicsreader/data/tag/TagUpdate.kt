package com.konformist.comicsreader.data.tag

import kotlinx.serialization.Serializable

@Serializable
data class TagUpdate(
  val id: Long,
  val mdate: String? = null,
  val name: String,
)