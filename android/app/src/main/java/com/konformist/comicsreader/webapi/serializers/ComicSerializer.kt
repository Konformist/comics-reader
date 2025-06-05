package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.db.comic.Comic
import com.konformist.comicsreader.db.comic.ComicLite
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ComicSerializer {
  companion object {
    fun toJSON(item: ComicLite): JSONObject {
      return JSONObject(Json.encodeToString<Comic>(item.comic)).apply {
        put("cover", item.cover?.let { ComicCoverSerializer.toJSON(it) })
      }
    }

    fun toJSONArray(items: List<ComicLite>): JSONArray {
      return JSONArray().apply {
        items.forEachIndexed { index, item ->
          put(index, toJSON(item))
        }
      }
    }
  }
}