package com.konformist.comicsreader.db.comiccover

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
  @ColumnInfo(name = "comic_id") var comicId: Long,
  /* ID привязанного файла */
  @ColumnInfo(name = "file_id", defaultValue = "0") var fileId: Long? = 0L,
  /* Ссылка на источник */
  @ColumnInfo(name = "from_url", defaultValue = "") var fromUrl: String? = "",
)