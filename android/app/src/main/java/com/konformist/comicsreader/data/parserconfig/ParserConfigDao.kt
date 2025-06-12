package com.konformist.comicsreader.data.parserconfig

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ParserConfigDao {
  @Query("SELECT * FROM parsers")
  fun readAll(): List<ParserConfig>

  @Query("SELECT * FROM parsers WHERE id = :id")
  fun read(id: Long): ParserConfig?

  @Insert(entity = ParserConfig::class)
  fun create(item: ParserConfigCreate): Long

  @Update(entity = ParserConfig::class)
  fun update(item: ParserConfigUpdate): Int

  @Delete(entity = ParserConfig::class)
  fun delete(item: ParserConfigDelete): Int
}