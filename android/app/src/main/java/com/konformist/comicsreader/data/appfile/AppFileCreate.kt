package com.konformist.comicsreader.data.appfile

import kotlinx.serialization.Serializable

@Serializable
data class AppFileCreate(
  val name: String,
  val mime: String,
  val size: Long,
  val path: String,
)