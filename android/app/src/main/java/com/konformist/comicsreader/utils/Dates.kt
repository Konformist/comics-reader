package com.konformist.comicsreader.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Dates {
  companion object {
    private val formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun dateTimeFormatted(value: LocalDateTime): String {
      return value.format(formatterDateTime)
    }

    private val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun dateFormatted(value: LocalDate): String {
      return value.format(formatterDate)
    }
  }
}