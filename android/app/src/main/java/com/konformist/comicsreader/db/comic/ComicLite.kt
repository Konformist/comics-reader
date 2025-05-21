package com.konformist.comicsreader.db.comic

import androidx.room.Embedded
import androidx.room.Relation

data class ComicLite(
  @Embedded val comic: Comic,
  @Relation(
    entity = ComicCover::class,
    parentColumn = "id",
    entityColumn = "comic_id"
  )
  val cover: ComicCoverWithFile?,
)
