package com.konformist.comicsreader.data.author

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
  fun read(id: Long): Author?

  @Insert(entity = Author::class)
  fun create(item: AuthorCreate): Long

  @Update(entity = Author::class)
  fun update(item: AuthorUpdate): Int

  @Delete(entity = Author::class)
  fun delete(item: AuthorDelete): Int
}