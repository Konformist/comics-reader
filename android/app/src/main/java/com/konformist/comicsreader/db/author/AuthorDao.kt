package com.konformist.comicsreader.db.author

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AuthorDao {
  @Query("SELECT * FROM authors")
  fun readAll(): List<Author>

  @Query("SELECT * FROM authors WHERE id = :id")
  fun read(id: Long): Author

  @Insert
  fun create(item: Author): Long

  @Update
  fun update(item: Author): Int

  @Delete
  fun delete(item: Author): Int
}