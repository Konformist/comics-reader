package com.konformist.comicsreader.db.tag

import kotlinx.serialization.Serializable

@Serializable
data class TagUpdate(
  val id: Long,
  var mdate: String,
  val name: String,
)