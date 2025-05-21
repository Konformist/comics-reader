package com.konformist.comicsreader.db.author

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "authors")
data class Author(
  @PrimaryKey(autoGenerate = true) val id: Long?,

  val cdate: Date?,
  val mdate: Date?,

  val name: String,
)