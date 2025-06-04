package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.db.chapter.Chapter
import com.konformist.comicsreader.db.chapter.ChapterCreate
import com.konformist.comicsreader.db.chapter.ChapterUpdate
import com.konformist.comicsreader.db.chapter.ChapterWithPages
import com.konformist.comicsreader.exceptions.ValidationException
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ChapterSerializer {
  companion object {
    fun toJSON(item: ChapterWithPages, filesDir: File): JSONObject {
      return JSONObject(Json.encodeToString<Chapter>(item.chapter)).apply {
        put("pages", ChapterPageSerializer.toJSONArray(item.pages, filesDir))
      }
    }

    fun toJSONArray(items: List<ChapterWithPages>, filesDir: File): JSONArray {
      return JSONArray().apply {
        items.forEachIndexed { index, item -> put(index, toJSON(items[index], filesDir)) }
      }
    }
  }
}