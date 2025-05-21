package com.konformist.comicsreader.db.comic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "chapter_pages")
data class ChapterPage(
  @PrimaryKey(autoGenerate = true) val id: Long?,

  val cdate: Date?,
  val mdate: Date?,

  /*
   * ID главы
   */
  @ColumnInfo(name = "chapter_id") val chapterId: Long,

  /*
   * ID привязанного файла
   */
  @ColumnInfo(name = "file_id") val fileId: Long?,

  /*
   * Ссылка на источник
   */
  @ColumnInfo(name = "from_url") val fromUrl: String,
)