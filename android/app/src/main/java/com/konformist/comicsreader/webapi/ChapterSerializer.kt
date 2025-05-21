package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.comic.Chapter
import org.json.JSONArray
import org.json.JSONObject

class ChapterSerializer : Serializer<Chapter>() {
  override fun toJSON(item: Chapter): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("cdate", item.cdate?.time)
    data.put("mdate", item.mdate?.time)
    data.put("name", item.name)

    return data
  }

  override fun toJSONArray(items: List<Chapter>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }

  override fun fromJSON(item: JSONObject): Chapter {
    return Chapter(
      id = getId(item.optLong("id", 0)),
      cdate = getDate(item.optLong("cdate")),
      mdate = getDate(item.optLong("mdate")),
      name = item.optString("name", ""),
      comicId = 0
    )
  }
}