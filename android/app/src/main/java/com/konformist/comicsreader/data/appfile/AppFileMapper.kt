package com.konformist.comicsreader.data.appfile

import com.konformist.comicsreader.utils.DatesUtils

fun AppFileUpdate.merge(value: AppFile): AppFileUpdate {
  return this.copy(
    mdate = DatesUtils.nowFormatted(),
  )
}