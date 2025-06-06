package com.konformist.comicsreader.db.comicoverride

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ComicOverrideDao {
  @Query("SELECT * FROM comic_overrides WHERE comic_id = :comicId")
  fun readByComic(comicId: Long): ComicOverride?

  @Query("SELECT * FROM comic_overrides WHERE id = :id")
  fun read(id: Long): ComicOverride?

  @Insert(entity = ComicOverride::class)
  fun create(item: ComicOverrideCreate): Long

  @Update(entity = ComicOverride::class)
  fun update(item: ComicOverrideUpdate): Int

  @Delete(entity = ComicOverride::class)
  fun delete(item: ComicOverrideDelete): Int
}