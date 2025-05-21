package com.konformist.comicsreader.db.tag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TagDao {
  @Query("SELECT * FROM tags")
  fun readAll(): List<Tag>

  @Query("SELECT * FROM tags WHERE id = :id")
  fun read(id: Long): Tag

  @Insert
  fun create(item: Tag): Long

  @Update
  fun update(item: Tag): Int

  @Delete
  fun delete(item: Tag): Int
}