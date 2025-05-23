package com.konformist.comicsreader.db.appfile

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AppFileDao {
  @Query("SELECT * FROM files WHERE id = :id")
  fun read(id: Long): AppFile

  @Insert(entity = AppFile::class)
  fun create(item: AppFileCreate): Long

  @Delete(entity = AppFile::class)
  fun delete(item: AppFileDelete): Int
}