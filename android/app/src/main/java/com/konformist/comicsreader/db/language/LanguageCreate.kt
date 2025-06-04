package com.konformist.comicsreader.db.language

import kotlinx.serialization.Serializable

@Serializable
data class LanguageCreate(
  val name: String,
)