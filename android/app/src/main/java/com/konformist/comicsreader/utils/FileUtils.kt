package com.konformist.comicsreader.utils

import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class FileUtils {
  companion object {
    fun tree(path: File): JSONObject {
      val result = JSONObject()

      if (path.isFile) {
        result.put("type", "file")
        result.put("name", path.name)
        result.put("extension", path.extension)
        result.put("size", path.length())
        result.put("lastModified", path.lastModified())
        result.put("path", Uri.fromFile(path))
      } else {
        result.put("type", "directory")
        result.put("name", path.name)
        val childes = JSONArray()

        if (path.isDirectory) {
          val list = path.listFiles()

          if (list != null) {
            for (file in list) childes.put(tree(file))
          }
        }

        result.put("count", childes.length())
        result.put("childes", childes)
      }

      return result
    }
  }
}