package com.konformist.comicsreader.db

import androidx.room.TypeConverter
import org.json.JSONArray

class Converters {
  @TypeConverter
  fun fromArrayString(value: String): List<Long> {
    val result = arrayListOf<Long>()
    val data = JSONArray(value)

    for (index in 0 until data.length()) {
      result.add(data.getLong(index))
    }

    return result
  }

  @TypeConverter
  fun toArrayString(list: List<Long>): String {
    val result = JSONArray()

    for (item in list) {
      result.put(item)
    }

    return list.toString()
  }
}