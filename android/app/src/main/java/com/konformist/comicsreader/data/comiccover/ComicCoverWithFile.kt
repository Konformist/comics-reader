package com.konformist.comicsreader.data.comiccover

import androidx.room.Embedded
import androidx.room.Relation
import com.konformist.comicsreader.data.appfile.AppFile

data class ComicCoverWithFile(
  @Embedded val cover: ComicCover,
  @Relation(
    parentColumn = "file_id",
    entityColumn = "id"
  )

  val file: AppFile?
)
