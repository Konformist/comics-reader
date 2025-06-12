package com.konformist.comicsreader.data.comiccover

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "comic_covers")
data class ComicCover(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(defaultValue = "(datetime('now'))") val cdate: String,
  @ColumnInfo(defaultValue = "(datetime('now'))") val mdate: String,

  /* ID комикса */
  @ColumnInfo(name = "comic_id") val comicId: Long,
  /* ID привязанного файла */
  @ColumnInfo(name = "file_id") val fileId: Long,
  /* Ссылка на источник */
  @ColumnInfo(name = "from_url") val fromUrl: String,
)