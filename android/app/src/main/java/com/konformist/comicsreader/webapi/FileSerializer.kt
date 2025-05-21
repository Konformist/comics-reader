package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.file.File
import org.json.JSONArray
import org.json.JSONObject

class FileSerializer : Serializer<File>() {
  override fun toJSON(item: File): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("cdate", item.cdate?.time)
    data.put("mdate", item.mdate?.time)
    data.put("name", item.name)
    data.put("size", item.size)
    data.put("path", item.path)
    data.put("mime", item.mime)

    return data
  }

  override fun toJSONArray(items: List<File>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }

  override fun fromJSON(item: JSONObject): File {
    return File(
      id = getId(item.optLong("id")),
      cdate = getDate(item.optLong("cdate")),
      mdate = getDate(item.optLong("mdate")),
      name = item.getString("name"),
      mime = item.optString("mime", ""),
      size = item.optLong("size", 0),
      path = item.optString("path", ""),
    )
  }
}