package com.konformist.comicsreader.db.language

import kotlinx.serialization.Serializable

@Serializable
data class LanguageUpdate(
  val id: Long,
  var mdate: String,
  val name: String,
)