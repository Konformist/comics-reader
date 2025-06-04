package com.konformist.comicsreader.db.appfile

import kotlinx.serialization.Serializable

@Serializable
data class AppFileDelete(
  val id: Long,
)