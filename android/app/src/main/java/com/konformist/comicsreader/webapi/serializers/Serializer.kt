package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.exceptions.ValidationException
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime

abstract class Serializer<T> {
  protected fun getMDate(): String {
    return DatesUtils.dateTimeFormatted(LocalDateTime.now())
  }

  protected fun listFromJSONArray(value: JSONArray?): List<Long> {
    val result = arrayListOf<Long>()
    if (value == null) return result

    for (index in 0 until value.length()) {
      result.add(value.getLong(index))
    }

    return result
  }

  protected fun listToJSONArray(value: List<Long>?): JSONArray {
    val result = JSONArray()
    if (value == null) return result

    for (item in value) {
      result.put(item)
    }

    return result
  }

  @Throws(ValidationException::class)
  abstract fun createFromJSON(value: JSONObject): Any

  @Throws(ValidationException::class)
  abstract fun updateFromJSON(value: JSONObject): Any

  @Throws(ValidationException::class)
  abstract fun deleteFromJSON(value: JSONObject): Any

  abstract fun toJSON(item: T): JSONObject
  abstract fun toJSONArray(items: List<T>): JSONArray
}