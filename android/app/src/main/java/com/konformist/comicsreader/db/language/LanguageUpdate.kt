package com.konformist.comicsreader.db.language

import kotlinx.serialization.Serializable

@Serializable
data class LanguageUpdate(
  val id: Long,
  val mdate: String,
  val name: String,
)