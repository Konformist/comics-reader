package com.konformist.comicsreader.db.file

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FileDao {
  @Query("SELECT * FROM files WHERE id = :id")
  fun read(id: Long): File

  @Insert
  fun create(item: File): Long

  @Delete
  fun delete(item: File): Int
}