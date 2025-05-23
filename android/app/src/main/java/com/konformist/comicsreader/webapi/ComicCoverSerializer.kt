package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.comiccover.ComicCoverUpdate
import com.konformist.comicsreader.db.comiccover.ComicCoverWithFile
import com.konformist.comicsreader.utils.ValidationException
import org.json.JSONArray
import org.json.JSONObject

class ComicCoverSerializer : Serializer<ComicCoverWithFile>() {
  private val fileSerializer = FileSerializer()

  @Throws(ValidationException::class)
  override fun createFromJSON(value: JSONObject) {}

  @Throws(ValidationException::class)
  override fun updateFromJSON(value: JSONObject): ComicCoverUpdate {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")

    return ComicCoverUpdate(
      id = id,
      mdate = getMDate(),
      fromUrl = value.optString("fromUrl", ""),
      fileId = value.optLong("fileId", 0)
    )
  }

  @Throws(ValidationException::class)
  override fun deleteFromJSON(value: JSONObject) {}

  override fun toJSON(item: ComicCoverWithFile): JSONObject {
    val data = JSONObject()
    data.put("id", item.cover.id)
    data.put("fromUrl", item.cover.fromUrl)

    if (item.file == null) data.put("file", null)
    else data.put("file", fileSerializer.toJSON(item.file))

    return data
  }

  override fun toJSONArray(items: List<ComicCoverWithFile>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }
}