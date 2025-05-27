package com.konformist.comicsreader.utils

import android.net.Uri
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException


class FileUtils {
  companion object {
    fun cleanBase64(value: String): String {
      return value
        .replace("data:image/jpeg;base64,", "")
        .replace("data:image/webp;base64,", "")
        .replace("data:image/png;base64,", "")
        .replace("data:image/gif;base64,", "")
        .replace("data:application/octet-stream;base64,", "")
    }

    fun writeBase64(fileOut: File, data: String): Boolean {
      val decodedFile = Base64.decode(cleanBase64(data), Base64.DEFAULT)
      val fileOutStream = FileOutputStream(fileOut)
      val bufferedOutputStream = BufferedOutputStream(fileOutStream)
      bufferedOutputStream.write(decodedFile)

      return true
    }

    fun write(file: File, data: String): Boolean {
      val parentDir = file.parent ?: return false
      val parentFile = File(parentDir)

      if (!parentFile.exists()) parentFile.mkdirs()

      try {
        val outputStreamWriter = FileWriter(file)
        outputStreamWriter.write(data)
        outputStreamWriter.close()
        return true
      } catch (e: IOException) {
        return false
      }
    }

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