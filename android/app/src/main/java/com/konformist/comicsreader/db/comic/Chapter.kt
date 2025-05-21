package com.konformist.comicsreader.db.comic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "chapters")
data class Chapter(
  @PrimaryKey(autoGenerate = true) val id: Long?,

  val cdate: Date?,
  val mdate: Date?,

  val name: String,
  @ColumnInfo(name = "comic_id") val comicId: Long
)
