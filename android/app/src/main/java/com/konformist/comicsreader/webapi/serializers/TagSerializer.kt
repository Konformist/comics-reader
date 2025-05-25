package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.db.tag.Tag
import com.konformist.comicsreader.db.tag.TagCreate
import com.konformist.comicsreader.db.tag.TagDelete
import com.konformist.comicsreader.db.tag.TagUpdate
import com.konformist.comicsreader.exceptions.ValidationException
import org.json.JSONArray
import org.json.JSONObject

class TagSerializer : Serializer<Tag>() {
  @Throws(ValidationException::class)
  override fun createFromJSON(value: JSONObject): TagCreate {
    val name = value.optString("name", "").trim()
    if (name == "") throw ValidationException("Name is empty")

    return TagCreate(name = value.optString("name", ""))
  }

  @Throws(ValidationException::class)
  override fun updateFromJSON(value: JSONObject): TagUpdate {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("Id is empty")
    val name = value.optString("name", "").trim()
    if (name == "") throw ValidationException("Name is empty")

    return TagUpdate(id = id, mdate = getMDate(), name = name)
  }

  @Throws(ValidationException::class)
  override fun deleteFromJSON(value: JSONObject): TagDelete {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("Id is empty")

    return TagDelete(id = id)
  }

  override fun toJSON(item: Tag): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("cdate", item.cdate)
    data.put("mdate", item.mdate)
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
}