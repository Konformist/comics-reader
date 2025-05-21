package com.konformist.comicsreader.webapi

import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

abstract class Serializer<T> {
  protected fun fromArrayString(value: String): List<Long> {
    val result = arrayListOf<Long>()
    val data = JSONArray(value)

    for (index in 0 until data.length()) {
      result.add(data.getLong(index))
    }

    return result
  }

  protected fun toArrayString(list: List<Long>): String {
    val result = JSONArray()

    for (item in list) {
      result.put(item)
    }

    return list.toString()
  }

  protected fun getId(value: Long): Long? {
    return if (value == 0.toLong()) null
    else value
  }

  protected fun getDate(value: Long): Date? {
    return if (value == 0.toLong()) null
    else Date(value)
  }

  abstract fun toJSON(item: T): JSONObject
  abstract fun toJSONArray(items: List<T>): JSONArray
  abstract fun fromJSON(item: JSONObject): T
}