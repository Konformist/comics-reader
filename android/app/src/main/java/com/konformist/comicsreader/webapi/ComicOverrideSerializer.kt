package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.comic.ComicOverride
import org.json.JSONArray
import org.json.JSONObject

class ComicOverrideSerializer : Serializer<ComicOverride>() {
  override fun toJSON(item: ComicOverride): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("titleCSS", item.titleCSS)
    data.put("coverCSS", item.coverCSS)
    data.put("pagesCSS", item.pagesCSS)
    data.put("languageCSS", item.languageCSS)
    data.put("tagsCSS", item.tagsCSS)
    data.put("tagsTextCSS", item.tagsTextCSS)
    data.put("authorsCSS", item.authorsCSS)
    data.put("authorsTextCSS", item.authorsTextCSS)

    return data
  }

  override fun toJSONArray(items: List<ComicOverride>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }

  override fun fromJSON(item: JSONObject): ComicOverride {
    return ComicOverride(
      id = item.optLong("id"),
      comicId = 0,
      cdate = null,
      mdate = null,
      titleCSS = item.optString("titleCSS", ""),
      coverCSS = item.optString("coverCSS", ""),
      pagesCSS = item.optString("pagesCSS", ""),
      languageCSS = item.optString("languageCSS", ""),
      tagsCSS = item.optString("tagsCSS", ""),
      tagsTextCSS = item.optString("tagsTextCSS", ""),
      authorsCSS = item.optString("authorsCSS", ""),
      authorsTextCSS = item.optString("authorsTextCSS", ""),
    )
  }
}