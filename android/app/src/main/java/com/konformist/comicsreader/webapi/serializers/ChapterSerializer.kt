package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.data.chapter.Chapter
import com.konformist.comicsreader.data.chapter.ChapterWithPages
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject

class ChapterSerializer {
  companion object {
    fun toJSON(item: ChapterWithPages): JSONObject {
      return JSONObject(Json.encodeToString<Chapter?>(item.chapter)).apply {
        put("pages", ChapterPageSerializer.toJSONArray(item.pages))
      }
    }

    fun toJSONArray(items: List<ChapterWithPages>): JSONArray {
      return JSONArray().apply {
        items.forEachIndexed { index, item -> put(index, toJSON(items[index])) }
      }
    }
  }
}