package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.language.Language
import org.json.JSONArray
import org.json.JSONObject

class LanguageSerializer : Serializer<Language>() {
  override fun toJSON(item: Language): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("cdate", item.cdate?.time)
    data.put("mdate", item.mdate?.time)
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


  override fun fromJSON(item: JSONObject): Language {
    return Language(
      id = getId(item.optLong("id")),
      cdate = getDate(item.optLong("cdate")),
      mdate = getDate(item.optLong("mdate")),
      name = item.getString("name"),
    )
  }
}