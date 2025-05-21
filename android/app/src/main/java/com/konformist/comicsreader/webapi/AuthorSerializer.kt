package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.author.Author
import org.json.JSONArray
import org.json.JSONObject

class AuthorSerializer : Serializer<Author>() {
  override fun toJSON(item: Author): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("cdate", item.cdate?.time)
    data.put("mdate", item.mdate?.time)
    data.put("name", item.name)

    return data
  }

  override fun toJSONArray(items: List<Author>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }

  override fun fromJSON(item: JSONObject): Author {

    return Author(
      id = getId(item.optLong("id", 0)),
      cdate = getDate(item.optLong("cdate")),
      mdate = getDate(item.optLong("mdate")),
      name = item.getString("name"),
    )
  }
}