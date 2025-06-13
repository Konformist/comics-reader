package com.konformist.comicsreader.data.comic

import androidx.room.Embedded
import androidx.room.Relation
import com.konformist.comicsreader.data.comiccover.ComicCover
import com.konformist.comicsreader.data.comiccover.ComicCoverWithFile

data class ComicLite(
  @Embedded val comic: Comic?,
  @Relation(
    entity = ComicCover::class,
    parentColumn = "id",
    entityColumn = "comic_id"
  )
  val cover: ComicCoverWithFile?,
)
