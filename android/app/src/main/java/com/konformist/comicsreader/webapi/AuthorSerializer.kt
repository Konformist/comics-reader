package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.author.Author
import com.konformist.comicsreader.db.author.AuthorCreate
import com.konformist.comicsreader.db.author.AuthorDelete
import com.konformist.comicsreader.db.author.AuthorUpdate
import com.konformist.comicsreader.utils.ValidationException
import org.json.JSONArray
import org.json.JSONObject

class AuthorSerializer : Serializer<Author>() {
  @Throws(ValidationException::class)
  override fun createFromJSON(value: JSONObject): AuthorCreate {
    val name = value.optString("name", "").trim()
    if (name == "") throw ValidationException("name")

    return AuthorCreate(name = name)
  }

  @Throws(ValidationException::class)
  override fun updateFromJSON(value: JSONObject): AuthorUpdate {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")
    val name = value.optString("name", "").trim()
    if (name == "") throw ValidationException("name")

    return AuthorUpdate(id = id, mdate = getMDate(), name = name)
  }

  @Throws(ValidationException::class)
  override fun deleteFromJSON(value: JSONObject): AuthorDelete {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")

    return AuthorDelete(id = id)
  }

  override fun toJSON(item: Author): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("cdate", item.cdate)
    data.put("mdate", item.mdate)
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
}