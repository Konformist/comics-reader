package com.konformist.comicsreader.data.author

import com.konformist.comicsreader.utils.DatesUtils

fun AuthorUpdate.merge(value: Author): AuthorUpdate {
  return this.copy(
    mdate = DatesUtils.nowFormatted(),
  )
}