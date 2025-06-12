package com.konformist.comicsreader.data.appfile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "files")
data class AppFile(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(defaultValue = "(datetime('now'))") val cdate: String,
  @ColumnInfo(defaultValue = "(datetime('now'))") val mdate: String,
  @ColumnInfo() val name: String,
  @ColumnInfo() val mime: String,
  @ColumnInfo() val size: Long,
  @ColumnInfo() val path: String,
)