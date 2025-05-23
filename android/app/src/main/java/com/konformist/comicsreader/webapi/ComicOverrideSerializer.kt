package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.comicoverride.ComicOverride
import com.konformist.comicsreader.db.comicoverride.ComicOverrideUpdate
import com.konformist.comicsreader.utils.ValidationException
import org.json.JSONArray
import org.json.JSONObject

class ComicOverrideSerializer : Serializer<ComicOverride>() {
  @Throws(ValidationException::class)
  override fun createFromJSON(value: JSONObject) {
  }

  @Throws(ValidationException::class)
  override fun updateFromJSON(value: JSONObject): ComicOverrideUpdate {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")

    return ComicOverrideUpdate(
      id = id,
      mdate = getMDate(),
      titleCSS = value.optString("titleCSS"),
      coverCSS = value.optString("coverCSS"),
      pagesCSS = value.optString("pagesCSS"),
      authorsCSS = value.optString("authorsCSS"),
      authorsTextCSS = value.optString("authorsTextCSS"),
      languageCSS = value.optString("languageCSS"),
      tagsCSS = value.optString("tagsCSS"),
      tagsTextCSS = value.optString("tagsTextCSS"),
    )
  }

  @Throws(ValidationException::class)
  override fun deleteFromJSON(value: JSONObject) {
  }

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
}