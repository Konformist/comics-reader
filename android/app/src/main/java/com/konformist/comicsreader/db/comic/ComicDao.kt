package com.konformist.comicsreader.db.comic

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ComicDao {
  @Transaction
  @Query("SELECT * FROM comics")
  fun readAll(): List<ComicLite>

  @Transaction
  @Query("SELECT * FROM comics WHERE id = :id")
  fun read(id: Long): ComicLite

  @Insert
  fun create(item: Comic): Long

  @Update
  fun update(item: Comic): Int

  @Delete
  fun delete(item: Comic): Int
}