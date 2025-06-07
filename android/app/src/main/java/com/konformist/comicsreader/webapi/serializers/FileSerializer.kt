package com.konformist.comicsreader.webapi.serializers

import android.net.Uri
import com.konformist.comicsreader.db.appfile.AppFile
import com.konformist.comicsreader.utils.FileManager
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.File

class FileSerializer {
  companion object {
    fun toJSON(item: AppFile): JSONObject {
      return JSONObject(Json.encodeToString(item)).apply {
        val path = if (item.path.isBlank()) item.path
        else Uri.fromFile(File(FileManager.filesDir, item.path)).path
        put("path", path)
      }
    }
  }
}