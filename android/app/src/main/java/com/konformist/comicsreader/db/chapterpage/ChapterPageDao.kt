package com.konformist.comicsreader.db.chapterpage

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
  fun readWithFile(id: Long): ChapterPageWithFile

  @Query("SELECT * FROM chapter_pages WHERE id = :id")
  fun read(id: Long): ChapterPage

  @Insert(entity = ChapterPage::class)
  fun create(item: ChapterPageCreate): Long

  @Update(entity = ChapterPage::class)
  fun update(item: ChapterPageUpdate): Int

  @Delete(entity = ChapterPage::class)
  fun delete(item: ChapterPageDelete): Int
}