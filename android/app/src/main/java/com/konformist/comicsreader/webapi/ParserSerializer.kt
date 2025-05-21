package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.db.parser.Parser
import org.json.JSONArray
import org.json.JSONObject

class ParserSerializer : Serializer<Parser>() {
  override fun toJSON(item: Parser): JSONObject {
    val data = JSONObject()

    data.put("id", item.id)
    data.put("cdate", item.cdate?.time)
    data.put("mdate", item.mdate?.time)
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

  override fun fromJSON(item: JSONObject): Parser {

    return Parser(
      id = getId(item.optLong("id")),
      cdate = getDate(item.optLong("cdate")),
      mdate = getDate(item.optLong("mdate")),
      name = item.getString("name"),
      siteUrl = item.getString("siteUrl"),
      titleCSS = item.getString("titleCSS"),
      coverCSS = item.getString("coverCSS"),
      pagesCSS = item.getString("pagesCSS"),
      authorsCSS = item.getString("authorsCSS"),
      authorsTextCSS = item.getString("authorsTextCSS"),
      languageCSS = item.getString("languageCSS"),
      tagsCSS = item.getString("tagsCSS"),
      tagsTextCSS = item.getString("tagsTextCSS"),
    )
  }
}