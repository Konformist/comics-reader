package com.konformist.comicsreader.db.comic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "comic_covers")
data class ComicCover(
  @PrimaryKey(autoGenerate = true) val id: Long?,

  val cdate: Date?,
  val mdate: Date?,

  /*
   * ID комикса
   */
  @ColumnInfo(name = "comic_id") val comicId: Long,

  /*
   * ID привязанного файла
   */
  @ColumnInfo(name = "file_id") val fileId: Long?,

  /*
   * Ссылка на источник
   */
  @ColumnInfo(name = "from_url") val fromUrl: String,
)