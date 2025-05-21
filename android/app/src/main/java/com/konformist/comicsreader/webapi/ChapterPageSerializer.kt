package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.comic.ChapterPage
import org.json.JSONArray
import org.json.JSONObject

class ChapterPageSerializer : Serializer<ChapterPage>() {
  override fun toJSON(item: ChapterPage): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("fromUrl", item.fromUrl)
    data.put("chapterId", item.chapterId)

    return data
  }

  override fun toJSONArray(items: List<ChapterPage>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }

  override fun fromJSON(item: JSONObject): ChapterPage {
    return ChapterPage(
      id = getId(item.optLong("id")),
      chapterId = item.optLong("chapterId"),
      cdate = null,
      mdate = null,
      fromUrl = item.optString("fromUrl", ""),
      fileId = null,
    )
  }

  fun fromJSONArray(value: JSONArray): List<ChapterPage> {
    val result = arrayListOf<ChapterPage>()

    for (index in 0 until value.length()) {
      result.add(fromJSON(value.getJSONObject(index)))
    }

    return result
  }
}