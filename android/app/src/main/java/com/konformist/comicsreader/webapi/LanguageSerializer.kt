package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.language.Language
import com.konformist.comicsreader.db.language.LanguageCreate
import com.konformist.comicsreader.db.language.LanguageDelete
import com.konformist.comicsreader.db.language.LanguageUpdate
import com.konformist.comicsreader.utils.ValidationException
import org.json.JSONArray
import org.json.JSONObject

class LanguageSerializer : Serializer<Language>() {
  @Throws(ValidationException::class)
  override fun createFromJSON(value: JSONObject): LanguageCreate {
    val name = value.optString("name", "").trim()
    if (name == "") throw ValidationException("name")

    return LanguageCreate(name = name)
  }

  @Throws(ValidationException::class)
  override fun updateFromJSON(value: JSONObject): LanguageUpdate {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")
    val name = value.optString("name", "").trim()
    if (name == "") throw ValidationException("name")

    return LanguageUpdate(id = id, mdate = getMDate(), name = name)
  }

  @Throws(ValidationException::class)
  override fun deleteFromJSON(value: JSONObject): LanguageDelete {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")

    return LanguageDelete(id = id)
  }

  override fun toJSON(item: Language): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("cdate", item.cdate)
    data.put("mdate", item.mdate)
    data.put("name", item.name)

    return data
  }


  override fun toJSONArray(items: List<Language>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }
}