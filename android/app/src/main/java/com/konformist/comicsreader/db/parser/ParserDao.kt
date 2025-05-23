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

  @Insert(entity = Parser::class)
  fun create(item: ParserCreate): Long

  @Update(entity = Parser::class)
  fun update(item: ParserUpdate): Int

  @Delete(entity = Parser::class)
  fun delete(item: ParserDelete): Int
}