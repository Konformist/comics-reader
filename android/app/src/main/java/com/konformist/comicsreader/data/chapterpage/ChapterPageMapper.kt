package com.konformist.comicsreader.data.chapterpage

import com.konformist.comicsreader.utils.DatesUtils

fun ChapterPageUpdate.merge(value: ChapterPage): ChapterPageUpdate {
  return this.copy(
    mdate = DatesUtils.nowFormatted(),
    isRead = this.isRead ?: value.isRead,
    fromUrl = this.fromUrl ?: value.fromUrl,
  )
}

fun ChapterPageFileUpdate.merge(value: ChapterPage): ChapterPageFileUpdate {
  return this.copy(
    mdate = DatesUtils.nowFormatted()
  )
}
