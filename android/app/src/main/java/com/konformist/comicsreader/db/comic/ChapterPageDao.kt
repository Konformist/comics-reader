package com.konformist.comicsreader.db.comic

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ChapterPageDao {
  @Transaction
  @Query("SELECT * FROM chapter_pages WHERE chapter_id = :chapterId")
  fun readByChapterAll(chapterId: Long): List<ChapterPageWithFile>

  @Transaction
  @Query("SELECT * FROM chapter_pages WHERE chapter_id = :chapterId")
  fun readByChapter(chapterId: Long): ChapterPageWithFile

  @Transaction
  @Query("SELECT * FROM chapter_pages WHERE id = :id")
  fun read(id: Long): ChapterPageWithFile

  @Insert
  fun create(item: ChapterPage): Long

  @Update
  fun update(item: ChapterPage): Int

  @Delete
  fun delete(item: ChapterPage): Int
}