package com.konformist.comicsreader.db.author

import kotlinx.serialization.Serializable

@Serializable
data class AuthorUpdate(
  val id: Long,
  var mdate: String,
  val name: String,
)