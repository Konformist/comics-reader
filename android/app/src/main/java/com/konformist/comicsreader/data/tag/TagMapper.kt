package com.konformist.comicsreader.data.tag

import com.konformist.comicsreader.utils.DatesUtils

fun TagUpdate.merge(value: Tag): TagUpdate {
  return this.copy(
    mdate = DatesUtils.nowFormatted(),
  )
}
