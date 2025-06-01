package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.db.comic.ComicCreate
import com.konformist.comicsreader.db.comic.ComicDelete
import com.konformist.comicsreader.db.comic.ComicLite
import com.konformist.comicsreader.db.comic.ComicUpdate
import com.konformist.comicsreader.exceptions.ValidationException
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ComicSerializer(filesDir: File) : Serializer<ComicLite>() {
  private val coverSerializer = ComicCoverSerializer(filesDir)

  @Throws(ValidationException::class)
  override fun createFromJSON(value: JSONObject): ComicCreate {
    return ComicCreate(
      name = value.optString("name", ""),
      parserId = value.optLong("parserId", 0),
      fromUrl = value.optString("fromUrl", ""),
      annotation = value.optString("annotation", ""),
      languageId = value.optLong("languageId", 0),
      authors = listFromJSONArray(value.optJSONArray("authors")),
      tags = listFromJSONArray(value.optJSONArray("tags")),
    )
  }

  @Throws(ValidationException::class)
  override fun updateFromJSON(value: JSONObject): ComicUpdate {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")

    return ComicUpdate(
      id = id,
      mdate = getMDate(),
      name = value.optString("name", ""),
      parserId = value.optLong("parserId", 0),
      fromUrl = value.optString("fromUrl", ""),
      annotation = value.optString("annotation", ""),
      languageId = value.optLong("languageId", 0),
      authors = listFromJSONArray(value.optJSONArray("authors")),
      tags = listFromJSONArray(value.optJSONArray("tags")),
    )
  }

  @Throws(ValidationException::class)
  override fun deleteFromJSON(value: JSONObject): ComicDelete {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")

    return ComicDelete(id = id)
  }

  override fun toJSON(item: ComicLite): JSONObject {
    val data = JSONObject()

    data.put("id", item.comic.id)
    data.put("cdate", item.comic.cdate)
    data.put("mdate", item.comic.mdate)
    data.put("name", item.comic.name)
    data.put("parserId", item.comic.parserId)
    data.put("fromUrl", item.comic.fromUrl)
    data.put("annotation", item.comic.annotation)
    data.put("languageId", item.comic.languageId)
    data.put("authors", listToJSONArray(item.comic.authors))
    data.put("tags", listToJSONArray(item.comic.tags))

    if (item.cover == null) data.put("cover", null)
    else data.put("cover", coverSerializer.toJSON(item.cover))

    return data
  }

  override fun toJSONArray(items: List<ComicLite>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }
}