package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.chapter.ChapterCreate
import com.konformist.comicsreader.db.chapter.ChapterUpdate
import com.konformist.comicsreader.db.chapter.ChapterWithPages
import com.konformist.comicsreader.utils.ValidationException
import org.json.JSONArray
import org.json.JSONObject

class ChapterSerializer : Serializer<ChapterWithPages>() {
  private val pageSerializer = ChapterPageSerializer()

  @Throws(ValidationException::class)
  override fun createFromJSON(value: JSONObject): ChapterCreate {
    val comicId = value.optLong("comicId", 0)
    if (comicId == 0.toLong()) throw ValidationException("comicId")

    return ChapterCreate(
      name = value.optString("name", ""),
      comicId = comicId,
    )
  }

  @Throws(ValidationException::class)
  override fun updateFromJSON(value: JSONObject): ChapterUpdate {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")

    return ChapterUpdate(
      id = id,
      mdate = getMDate(),
      name = value.optString("name", ""),
    )
  }

  @Throws(ValidationException::class)
  override fun deleteFromJSON(value: JSONObject) {
  }

  override fun toJSON(item: ChapterWithPages): JSONObject {
    val data = JSONObject()

    data.put("id", item.chapter.id)
    data.put("cdate", item.chapter.cdate)
    data.put("mdate", item.chapter.mdate)
    data.put("name", item.chapter.name)
    data.put("comicId", item.chapter.comicId)
    data.put("pages", pageSerializer.toJSONArray(item.pages))

    return data
  }

  override fun toJSONArray(items: List<ChapterWithPages>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }
}