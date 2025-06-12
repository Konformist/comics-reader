package com.konformist.comicsreader.data.chapter

import androidx.room.Embedded
import androidx.room.Relation
import com.konformist.comicsreader.data.chapterpage.ChapterPage
import com.konformist.comicsreader.data.chapterpage.ChapterPageWithFile

data class ChapterWithPages(
  @Embedded val chapter: Chapter?,
  @Relation(
    entity = ChapterPage::class,
    parentColumn = "id",
    entityColumn = "chapter_id",
  )

  val pages: List<ChapterPageWithFile>
)
