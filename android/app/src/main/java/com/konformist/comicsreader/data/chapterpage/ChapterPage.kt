package com.konformist.comicsreader.data.chapterpage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "chapter_pages")
data class ChapterPage(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(defaultValue = "(datetime('now'))") val cdate: String,
  @ColumnInfo(defaultValue = "(datetime('now'))") val mdate: String,

  /* ID главы */
  @ColumnInfo(name = "chapter_id") val chapterId: Long,
  /* Прочитано */
  @ColumnInfo(name = "is_read") val isRead: Boolean,
  /* ID привязанного файла */
  @ColumnInfo(name = "file_id") val fileId: Long,
  /* Ссылка на источник */
  @ColumnInfo(name = "from_url") val fromUrl: String,
)