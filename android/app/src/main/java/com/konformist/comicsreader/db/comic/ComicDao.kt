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
  fun readLiteAll(): List<ComicLite>

  @Transaction
  @Query("SELECT * FROM comics WHERE id = :id")
  fun readLite(id: Long): ComicLite

  @Query("SELECT * FROM comics WHERE id = :id")
  fun read(id: Long): Comic

  @Insert(entity = Comic::class)
  fun create(item: ComicCreate): Long

  @Update(entity = Comic::class)
  fun update(item: ComicUpdate): Int

  @Delete(entity = Comic::class)
  fun delete(item: ComicDelete): Int
}