package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.data.comic.Comic
import com.konformist.comicsreader.data.comic.ComicLite
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject

class ComicSerializer {
  companion object {
    fun toJSON(item: ComicLite): JSONObject? {
      return item.comic?.let {
        JSONObject(Json.encodeToString<Comic>(it)).apply {
          put("cover", item.cover?.let { cIt -> ComicCoverSerializer.toJSON(cIt) })
        }
      }
    }

    fun toJSONArray(items: List<ComicLite>): JSONArray {
      return JSONArray().apply {
        items
          .filter { item -> item.comic != null }
          .forEachIndexed { index, item -> put(index, toJSON(item)) }
      }
    }
  }
}