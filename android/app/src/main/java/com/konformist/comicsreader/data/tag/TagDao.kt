package com.konformist.comicsreader.data.tag

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
  fun read(id: Long): Tag?

  @Insert(entity = Tag::class)
  fun create(item: TagCreate): Long

  @Update(entity = Tag::class)
  fun update(item: TagUpdate): Int

  @Delete(entity = Tag::class)
  fun delete(item: TagDelete): Int
}