package com.konformist.comicsreader.db.chapter

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapters")
data class Chapter(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(defaultValue = "(datetime('now'))") val cdate: String,
  @ColumnInfo(defaultValue = "(datetime('now'))") val mdate: String,
  @ColumnInfo(defaultValue = "") val name: String?,
  @ColumnInfo(name = "comic_id") val comicId: Long
)
