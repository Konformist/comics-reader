package com.konformist.comicsreader.data.language

import com.konformist.comicsreader.utils.DatesUtils

fun LanguageUpdate.merge(value: Language): LanguageUpdate {
  return this.copy(
    mdate = DatesUtils.nowFormatted(),
  )
}
