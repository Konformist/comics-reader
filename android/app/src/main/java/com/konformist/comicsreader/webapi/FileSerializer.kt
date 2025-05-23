package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.appfile.AppFile
import com.konformist.comicsreader.db.appfile.AppFileCreate
import com.konformist.comicsreader.db.appfile.AppFileDelete
import com.konformist.comicsreader.utils.ValidationException
import org.json.JSONArray
import org.json.JSONObject

class FileSerializer : Serializer<AppFile>() {
  @Throws(ValidationException::class)
  override fun createFromJSON(value: JSONObject): AppFileCreate {
    return AppFileCreate(
      name = value.getString("name"),
      mime = value.getString("mime"),
      size = value.getLong("size"),
      path = value.getString("path"),
    )
  }

  @Throws(ValidationException::class)
  override fun updateFromJSON(value: JSONObject) {
  }

  @Throws(ValidationException::class)
  override fun deleteFromJSON(value: JSONObject): AppFileDelete {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")

    return AppFileDelete(id = id)
  }

  override fun toJSON(item: AppFile): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("cdate", item.cdate)
    data.put("mdate", item.mdate)
    data.put("name", item.name)
    data.put("size", item.size)
    data.put("path", item.path)
    data.put("mime", item.mime)

    return data
  }

  override fun toJSONArray(items: List<AppFile>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }
}