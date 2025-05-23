package com.konformist.comicsreader.db.chapter

import androidx.room.Embedded
import androidx.room.Relation
import com.konformist.comicsreader.db.chapterpage.ChapterPage
import com.konformist.comicsreader.db.chapterpage.ChapterPageWithFile

data class ChapterWithPages(
  @Embedded val chapter: Chapter,
  @Relation(
    entity = ChapterPage::class,
    parentColumn = "id",
    entityColumn = "chapter_id",
  )

  val pages: List<ChapterPageWithFile>
)
