package com.konformist.comicsreader.db.parser

import kotlinx.serialization.Serializable

@Serializable
data class ParserDelete(
  val id: Long,
)