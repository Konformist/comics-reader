package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.db.chapterpage.ChapterPageWithFile
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ChapterPageSerializer {
  companion object {
    fun toJSON(item: ChapterPageWithFile, filesDir: File): JSONObject {
      return JSONObject(Json.encodeToString(item.page)).apply {
        put("file", item.file?.let { FileSerializer.toJSON(it, filesDir) })
      }
    }

    fun toJSONArray(items: List<ChapterPageWithFile>, filesDir: File): JSONArray {
      return JSONArray().apply {
        items.forEachIndexed { index, item ->
          put(index, toJSON(item, filesDir))
        }
      }
    }
  }
}