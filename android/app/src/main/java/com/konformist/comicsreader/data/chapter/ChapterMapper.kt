package com.konformist.comicsreader.data.chapter

import com.konformist.comicsreader.utils.DatesUtils

fun ChapterUpdate.merge(value: Chapter): ChapterUpdate {
  return this.copy(
    mdate = DatesUtils.nowFormatted(),
  )
}

fun ChapterUpdateComic.merge(value: Chapter): ChapterUpdateComic {
  return this.copy(
    mdate = DatesUtils.nowFormatted(),
  )
}
