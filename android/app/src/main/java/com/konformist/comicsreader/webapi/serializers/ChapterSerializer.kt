package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.db.chapter.Chapter
import com.konformist.comicsreader.db.chapter.ChapterWithPages
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ChapterSerializer {
  companion object {
    fun toJSON(item: ChapterWithPages): JSONObject {
      return JSONObject(Json.encodeToString<Chapter>(item.chapter)).apply {
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