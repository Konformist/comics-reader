package com.konformist.comicsreader.db.comic

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ComicOverrideDao {
  @Query("SELECT * FROM comic_overrides WHERE comic_id = :comicId")
  fun readByComic(comicId: Long): ComicOverride

  @Query("SELECT * FROM comic_overrides WHERE id = :id")
  fun read(id: Long): ComicOverride

  @Insert
  fun create(item: ComicOverride): Long

  @Update
  fun update(item: ComicOverride): Int

  @Delete
  fun delete(item: ComicOverride): Int
}