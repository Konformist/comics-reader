package com.konformist.comicsreader.data.comic

import com.konformist.comicsreader.data.chapter.ChapterWithPages
import com.konformist.comicsreader.data.comiccover.ComicCoverWithFile
import com.konformist.comicsreader.data.comicoverride.ComicOverride

data class ComicFull(
  val comic: Comic,
  val cover: ComicCoverWithFile?,
  val override: ComicOverride?,
  val chapters: List<ChapterWithPages>,
)
