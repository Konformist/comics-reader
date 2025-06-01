package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.db.comicoverride.ComicOverride
import com.konformist.comicsreader.db.comicoverride.ComicOverrideUpdate
import com.konformist.comicsreader.exceptions.ValidationException
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
      annotationCSS = value.optString("annotationCSS"),
      coverCSS = value.optString("coverCSS"),
      authorsCSS = value.optString("authorsCSS"),
      authorsTextCSS = value.optString("authorsTextCSS"),
      languageCSS = value.optString("languageCSS"),
      tagsCSS = value.optString("tagsCSS"),
      tagsTextCSS = value.optString("tagsTextCSS"),
      chaptersCSS = value.optString("chaptersCSS"),
      chaptersTitleCSS = value.optString("chaptersTitleCSS"),
      pagesTemplateUrl = value.optString("pagesTemplateUrl"),
      pagesCSS = value.optString("pagesCSS"),
      pagesImageCSS = value.optString("pagesImageCSS"),
    )
  }

  @Throws(ValidationException::class)
  override fun deleteFromJSON(value: JSONObject) {
  }

  override fun toJSON(item: ComicOverride): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("titleCSS", item.titleCSS)
    data.put("annotationCSS", item.annotationCSS)
    data.put("coverCSS", item.coverCSS)
    data.put("authorsCSS", item.authorsCSS)
    data.put("authorsTextCSS", item.authorsTextCSS)
    data.put("languageCSS", item.languageCSS)
    data.put("tagsCSS", item.tagsCSS)
    data.put("tagsTextCSS", item.tagsTextCSS)
    data.put("chaptersCSS", item.chaptersCSS)
    data.put("chaptersTitleCSS", item.chaptersTitleCSS)
    data.put("pagesTemplateUrl", item.pagesTemplateUrl)
    data.put("pagesCSS", item.pagesCSS)
    data.put("pagesImageCSS", item.pagesImageCSS)

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