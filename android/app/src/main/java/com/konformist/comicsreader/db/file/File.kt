package com.konformist.comicsreader.db.file

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "files")
data class File(
  @PrimaryKey(autoGenerate = true) val id: Long?,

  val cdate: Date?,
  val mdate: Date?,

  val name: String,
  val mime: String,
  val size: Long,
  val path: String,
)