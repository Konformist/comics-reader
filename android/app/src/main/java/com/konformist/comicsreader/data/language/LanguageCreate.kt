package com.konformist.comicsreader.data.language

import kotlinx.serialization.Serializable

@Serializable
data class LanguageCreate(
  val name: String? = "",
)