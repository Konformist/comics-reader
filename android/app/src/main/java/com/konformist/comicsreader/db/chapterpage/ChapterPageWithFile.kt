package com.konformist.comicsreader.db.chapterpage

import androidx.room.Embedded
import androidx.room.Relation
import com.konformist.comicsreader.db.appfile.AppFile

data class ChapterPageWithFile(
  @Embedded val page: ChapterPage,
  @Relation(
    parentColumn = "file_id",
    entityColumn = "id",
  )

  val file: AppFile?
)
