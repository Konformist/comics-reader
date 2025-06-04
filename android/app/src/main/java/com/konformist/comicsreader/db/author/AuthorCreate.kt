package com.konformist.comicsreader.db.author

import kotlinx.serialization.Serializable

@Serializable
data class AuthorCreate(
  val name: String,
)