package com.konformist.comicsreader.db.author

import kotlinx.serialization.Serializable

@Serializable
data class AuthorDelete(
  val id: Long,
)