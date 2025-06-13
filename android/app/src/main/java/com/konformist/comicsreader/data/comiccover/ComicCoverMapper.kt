package com.konformist.comicsreader.data.comiccover

import com.konformist.comicsreader.utils.DatesUtils

fun ComicCoverUpdate.merge(value: ComicCover): ComicCoverUpdate {
  return this.copy(
    mdate = DatesUtils.nowFormatted()
  )
}

fun ComicCoverFileUpdate.merge(value: ComicCover): ComicCoverFileUpdate {
  return this.copy(
    mdate = DatesUtils.nowFormatted()
  )
}
