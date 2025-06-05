package com.konformist.comicsreader.db.appfile

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "files")
data class AppFileUpdate(
  val id: Long,
  val mdate: String,
  val name: String,
  val mime: String,
  val size: Long,
  val path: String,
)