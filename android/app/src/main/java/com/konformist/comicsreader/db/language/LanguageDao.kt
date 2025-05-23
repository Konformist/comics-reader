package com.konformist.comicsreader.db.language

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LanguageDao {
  @Query("SELECT * FROM languages")
  fun readAll(): List<Language>

  @Query("SELECT * FROM languages WHERE id = :id")
  fun read(id: Long): Language

  @Insert(entity = Language::class)
  fun create(item: LanguageCreate): Long

  @Update(entity = Language::class)
  fun update(item: LanguageUpdate): Int

  @Delete(entity = Language::class)
  fun delete(item: LanguageDelete): Int
}