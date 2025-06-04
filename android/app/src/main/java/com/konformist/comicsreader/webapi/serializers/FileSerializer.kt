package com.konformist.comicsreader.webapi.serializers

import android.net.Uri
import com.konformist.comicsreader.db.appfile.AppFile
import com.konformist.comicsreader.db.appfile.AppFileCreate
import com.konformist.comicsreader.db.appfile.AppFileDelete
import com.konformist.comicsreader.exceptions.ValidationException
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class FileSerializer {
  companion object {
    fun toJSON(item: AppFile, filesDir: File): JSONObject {
      return JSONObject(Json.encodeToString(item)).apply {
        put("path", if (item.path.isEmpty()) item.path
                          else Uri.fromFile(File("${filesDir.path}${File.separator}${item.path}")).path)
      }
    }
  }
}