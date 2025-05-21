package com.konformist.comicsreader.db.comic

import androidx.room.Embedded
import androidx.room.Relation
import com.konformist.comicsreader.db.file.File

data class ComicCoverWithFile(
  @Embedded val cover: ComicCover,
  @Relation(
    parentColumn = "file_id",
    entityColumn = "id"
  )

  val file: File?
)
