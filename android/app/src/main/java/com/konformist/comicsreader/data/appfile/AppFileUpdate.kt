package com.konformist.comicsreader.data.appfile

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "files")
data class AppFileUpdate(
  val id: Long,
  val mdate: String? = null,
  val name: String,
  val mime: String,
  val size: Long,
  val path: String,
)