package com.konformist.comicsreader.data.author

import kotlinx.serialization.Serializable

@Serializable
data class AuthorCreate(
  val name: String? = "",
)