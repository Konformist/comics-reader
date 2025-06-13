package com.konformist.comicsreader.db

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
  /** Авто перелистывание */
  var autoReading: Boolean = false,
  /** Таймер для авто перелистывания */
  var autoReadingTimeout: Int = 20000,
  /** Режим чтения */
  var readingMode: String = "vertical",
  /** Сжимать картинки при загрузке */
  var isCompress: Boolean = true
)