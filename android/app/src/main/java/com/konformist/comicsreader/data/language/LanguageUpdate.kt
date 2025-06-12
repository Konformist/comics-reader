package com.konformist.comicsreader.data.language

import kotlinx.serialization.Serializable

@Serializable
data class LanguageUpdate(
  val id: Long,
  val mdate: String? = null,
  val name: String,
)