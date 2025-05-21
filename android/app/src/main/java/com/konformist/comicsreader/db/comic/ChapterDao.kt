package com.konformist.comicsreader.db.comic

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
  fun readByComicAll(comicId: Long): List<ChapterWithPages>

  @Transaction
  @Query("SELECT * FROM chapters WHERE id = :id")
  fun read(id: Long): ChapterWithPages

  @Insert
  fun create(item: Chapter): Long

  @Update
  fun update(item: Chapter): Int

  @Delete
  fun delete(item: Chapter): Int
}