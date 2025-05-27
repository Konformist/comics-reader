package com.konformist.comicsreader.webapi

data class Settings(
  /** Авто перелистывание */
  var autoReading: Boolean = false,
  /** Таймер для авто перелистывания */
  var autoReadingTimeout: Int = 20,
  /** Режим чтения */
  var readingMode: String = "vertical",
  /** Сжимать картинки при загрузке */
  var isCompress: Boolean = true
)