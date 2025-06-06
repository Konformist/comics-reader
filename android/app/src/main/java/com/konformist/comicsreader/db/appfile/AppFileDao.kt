package com.konformist.comicsreader.db.appfile

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppFileDao {
  @Query("SELECT * FROM files")
  fun readAll(): List<AppFile>

  @Query("SELECT * FROM files WHERE id = :id")
  fun read(id: Long): AppFile?

  @Insert(entity = AppFile::class)
  fun create(item: AppFileCreate): Long

  @Update(entity = AppFile::class)
  fun update(item: AppFileUpdate): Int

  @Delete(entity = AppFile::class)
  fun delete(item: AppFileDelete): Int
}