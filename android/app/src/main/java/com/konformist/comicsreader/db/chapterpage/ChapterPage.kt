package com.konformist.comicsreader.db.chapterpage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapter_pages")
data class ChapterPage(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(defaultValue = "(datetime('now'))") val cdate: String,
  @ColumnInfo(defaultValue = "(datetime('now'))") val mdate: String,

  /* ID главы */
  @ColumnInfo(name = "chapter_id") val chapterId: Long,
  /* Прочитано */
  @ColumnInfo(name = "is_read", defaultValue = "0") val isRead: Boolean?,
  /* ID привязанного файла */
  @ColumnInfo(name = "file_id", defaultValue = "0") val fileId: Long?,
  /* Ссылка на источник */
  @ColumnInfo(name = "from_url", defaultValue = "") val fromUrl: String?,
)