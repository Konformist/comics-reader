package com.konformist.comicsreader.db.appfile

data class AppFileCreate(
  val name: String,
  val mime: String,
  val size: Long,
  val path: String,
)