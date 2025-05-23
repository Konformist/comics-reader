package com.konformist.comicsreader.db.comiccover

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ComicCoverDao {
  @Query("SELECT * FROM comic_covers WHERE comic_id = :comicId")
  fun readByComic(comicId: Long): ComicCover

  @Query("SELECT * FROM comic_covers WHERE id = :id")
  fun read(id: Long): ComicCover

  @Insert(entity = ComicCover::class)
  fun create(item: ComicCoverCreate): Long

  @Update(entity = ComicCover::class)
  fun update(item: ComicCoverUpdate): Int

  @Delete(entity = ComicCover::class)
  fun delete(item: ComicCoverDelete): Int
}