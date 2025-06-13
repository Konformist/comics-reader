package com.konformist.comicsreader.data.author

import kotlinx.serialization.Serializable

@Serializable
data class AuthorUpdate(
  val id: Long,
  val mdate: String? = "",
  val name: String,
)