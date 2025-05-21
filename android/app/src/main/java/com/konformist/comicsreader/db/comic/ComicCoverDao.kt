package com.konformist.comicsreader.db.comic

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ComicCoverDao {
  @Transaction
  @Query("SELECT * FROM comic_covers WHERE comic_id = :comicId")
  fun readByComic(comicId: Long): ComicCoverWithFile

  @Transaction
  @Query("SELECT * FROM comic_covers WHERE id = :id")
  fun read(id: Long): ComicCoverWithFile

  @Insert
  fun create(item: ComicCover): Long

  @Update
  fun update(item: ComicCover): Int

  @Delete
  fun delete(item: ComicCover): Int
}