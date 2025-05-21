package com.konformist.comicsreader.db.parser

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ParserDao {
  @Query("SELECT * FROM parsers")
  fun readAll(): List<Parser>

  @Query("SELECT * FROM parsers WHERE id = :id")
  fun read(id: Long): Parser

  @Insert
  fun create(item: Parser): Long

  @Update
  fun update(item: Parser): Int

  @Delete
  fun delete(item: Parser): Int
}