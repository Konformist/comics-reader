package com.konformist.comicsreader.db.chapter

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ChapterDao {
  @Transaction
  @Query("SELECT * FROM chapters WHERE comic_id = :comicId")
  fun readWithPagesByComicAll(comicId: Long): List<ChapterWithPages>

  @Transaction
  @Query("SELECT * FROM chapters WHERE id = :id")
  fun readWithPages(id: Long): ChapterWithPages

  @Query("SELECT * FROM chapters WHERE comic_id = :comicId")
  fun readByComicAll(comicId: Long): List<Chapter>

  @Query("SELECT * FROM chapters WHERE id = :id")
  fun read(id: Long): Chapter

  @Insert(entity = Chapter::class)
  fun create(item: ChapterCreate): Long

  @Update(entity = Chapter::class)
  fun update(item: ChapterUpdate): Int

  @Delete(entity = Chapter::class)
  fun delete(item: ChapterDelete): Int
}