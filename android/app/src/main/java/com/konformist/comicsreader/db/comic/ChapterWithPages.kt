package com.konformist.comicsreader.db.comic

import androidx.room.Embedded
import androidx.room.Relation

data class ChapterWithPages(
  @Embedded val chapter: Chapter,
  @Relation(
    entity = ChapterPage::class,
    parentColumn = "id",
    entityColumn = "chapter_id",
  )

  val pages: List<ChapterPageWithFile>
)
