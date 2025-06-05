package com.konformist.comicsreader.webapi.serializers

import com.konformist.comicsreader.db.comiccover.ComicCover
import com.konformist.comicsreader.db.comiccover.ComicCoverWithFile
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.File

class ComicCoverSerializer {
  companion object {
    fun toJSON(item: ComicCoverWithFile): JSONObject {
      return JSONObject(Json.encodeToString<ComicCover>(item.cover)).apply {
        put("file", item.file?.let { FileSerializer.toJSON(it) })
      }
    }
  }
}