package com.konformist.comicsreader.db.comiccover

import androidx.room.Embedded
import androidx.room.Relation
import com.konformist.comicsreader.db.appfile.AppFile

data class ComicCoverWithFile(
  @Embedded val cover: ComicCover,
  @Relation(
    parentColumn = "file_id",
    entityColumn = "id"
  )

  val file: AppFile?
)
