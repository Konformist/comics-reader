package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.tag.Tag
import org.json.JSONArray
import org.json.JSONObject

class TagSerializer : Serializer<Tag>() {
  override fun toJSON(item: Tag): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("cdate", item.cdate?.time)
    data.put("mdate", item.mdate?.time)
    data.put("name", item.name)

    return data
  }

  override fun toJSONArray(items: List<Tag>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }

  override fun fromJSON(item: JSONObject): Tag {
    return Tag(
      id = getId(item.optLong("id")),
      cdate = getDate(item.optLong("cdate")),
      mdate = getDate(item.optLong("mdate")),
      name = item.getString("name"),
    )
  }
}