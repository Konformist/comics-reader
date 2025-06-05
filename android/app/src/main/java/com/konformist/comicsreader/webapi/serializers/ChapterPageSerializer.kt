package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.db.chapterpage.ChapterPageWithFile
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject

class ChapterPageSerializer {
  companion object {
    fun toJSON(item: ChapterPageWithFile): JSONObject {
      return JSONObject(Json.encodeToString(item.page)).apply {
        put("file", item.file?.let { FileSerializer.toJSON(it) })
      }
    }

    fun toJSONArray(items: List<ChapterPageWithFile>): JSONArray {
      return JSONArray().apply {
        items.forEachIndexed { index, item ->
          put(index, toJSON(item))
        }
      }
    }
  }
}