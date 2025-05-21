package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.comic.Comic
import org.json.JSONArray
import org.json.JSONObject

class ComicSerializer : Serializer<Comic>() {
  override fun toJSON(item: Comic): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("cdate", item.cdate?.time)
    data.put("mdate", item.mdate?.time)
    data.put("name", item.name)
    data.put("parser", item.parserId)
    data.put("fromUrl", item.fromUrl)
    data.put("language", item.languageId)
    data.put("authors", toArrayString(item.authors))
    data.put("tags", toArrayString(item.tags))

    return data
  }

  override fun toJSONArray(items: List<Comic>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }

  override fun fromJSON(item: JSONObject): Comic {
    return Comic(
      id = getId(item.optLong("id")),
      cdate = getDate(item.optLong("cdate")),
      mdate = getDate(item.optLong("mdate")),
      name = item.optString("name", ""),
      parserId = item.optLong("parser", 0),
      fromUrl = item.optString("fromUrl", ""),
      languageId = item.optLong("language", 0),
      authors = fromArrayString(item.optString("authors")),
      tags = fromArrayString(item.optString("tags"))
    )
  }
}