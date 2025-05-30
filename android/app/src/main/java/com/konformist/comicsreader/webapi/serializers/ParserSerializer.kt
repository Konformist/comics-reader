package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.db.parser.Parser
import com.konformist.comicsreader.db.parser.ParserCreate
import com.konformist.comicsreader.db.parser.ParserDelete
import com.konformist.comicsreader.db.parser.ParserUpdate
import com.konformist.comicsreader.exceptions.ValidationException
import org.json.JSONArray
import org.json.JSONObject

class ParserSerializer : Serializer<Parser>() {
  @Throws(ValidationException::class)
  override fun createFromJSON(value: JSONObject): ParserCreate {
    return ParserCreate(
      name = value.optString("name", ""),
      siteUrl = value.optString("siteUrl", ""),
      titleCSS = value.optString("titleCSS", ""),
      coverCSS = value.optString("coverCSS", ""),
      pagesCSS = value.optString("pagesCSS", ""),
      authorsCSS = value.optString("authorsCSS", ""),
      authorsTextCSS = value.optString("authorsTextCSS", ""),
      languageCSS = value.optString("languageCSS", ""),
      tagsCSS = value.optString("tagsCSS", ""),
      tagsTextCSS = value.optString("tagsTextCSS", ""),
    )
  }

  @Throws(ValidationException::class)
  override fun updateFromJSON(value: JSONObject): ParserUpdate {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")
    val name = value.optString("name", "").trim()
    if (name == "") throw ValidationException("name")

    return ParserUpdate(
      id = id,
      mdate = getMDate(),
      name = name,
      siteUrl = value.optString("siteUrl", ""),
      titleCSS = value.optString("titleCSS", ""),
      coverCSS = value.optString("coverCSS", ""),
      pagesCSS = value.optString("pagesCSS", ""),
      authorsCSS = value.optString("authorsCSS", ""),
      authorsTextCSS = value.optString("authorsTextCSS", ""),
      languageCSS = value.optString("languageCSS", ""),
      tagsCSS = value.optString("tagsCSS", ""),
      tagsTextCSS = value.optString("tagsTextCSS", ""),
    )
  }

  @Throws(ValidationException::class)
  override fun deleteFromJSON(value: JSONObject): ParserDelete {
    val id = value.optLong("id", 0)
    if (id == 0.toLong()) throw ValidationException("id")

    return ParserDelete(id = id)
  }

  override fun toJSON(item: Parser): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("cdate", item.cdate)
    data.put("mdate", item.mdate)
    data.put("name", item.name)
    data.put("siteUrl", item.siteUrl)
    data.put("titleCSS", item.titleCSS)
    data.put("coverCSS", item.coverCSS)
    data.put("pagesCSS", item.pagesCSS)
    data.put("authorsCSS", item.authorsCSS)
    data.put("authorsTextCSS", item.authorsTextCSS)
    data.put("languageCSS", item.languageCSS)
    data.put("tagsCSS", item.tagsCSS)
    data.put("tagsTextCSS", item.tagsTextCSS)

    return data
  }

  override fun toJSONArray(items: List<Parser>): JSONArray {
    val result = JSONArray()

    for (i in items.indices) {
      result.put(i, toJSON(items[i]))
    }

    return result
  }
}