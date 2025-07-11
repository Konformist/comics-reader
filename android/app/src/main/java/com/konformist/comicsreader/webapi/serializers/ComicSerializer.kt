package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.data.comic.Comic
import com.konformist.comicsreader.data.comic.ComicFull
import com.konformist.comicsreader.data.comicoverride.ComicOverride
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject

class ComicSerializer {
  companion object {
    private val jsonEncode = Json { encodeDefaults = true }

    fun toJSON(item: ComicFull?): JSONObject? {
      return item?.let { it ->
        JSONObject(Json.encodeToString<Comic>(item.comic)).apply {
          put("cover", item.cover?.let { cIt -> ComicCoverSerializer.toJSON(cIt) })
          put("override", jsonEncode.encodeToString<ComicOverride?>(item.override))
          put("chapters", ChapterSerializer.toJSONArray(item.chapters))
        }
      }
    }

    fun toJSONArray(items: List<ComicFull>): JSONArray {
      return JSONArray().apply {
        items.forEachIndexed { index, item -> put(index, toJSON(item)) }
      }
    }
  }
}