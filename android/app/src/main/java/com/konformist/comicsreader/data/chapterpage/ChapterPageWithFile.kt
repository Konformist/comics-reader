package com.konformist.comicsreader.data.chapterpage

import androidx.room.Embedded
import androidx.room.Relation
import com.konformist.comicsreader.data.appfile.AppFile

data class ChapterPageWithFile(
  @Embedded val page: ChapterPage?,
  @Relation(
    parentColumn = "file_id",
    entityColumn = "id",
  )

  val file: AppFile?
)
