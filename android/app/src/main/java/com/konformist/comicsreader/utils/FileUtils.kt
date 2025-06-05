package com.konformist.comicsreader.utils

import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream

class FileUtils {
  companion object {
    fun getExtensionFromMime(mimeType: String?): String {
      if (mimeType == null) return "bin" // Если MIME не найден, даём расширение по умолчанию

      return when (mimeType) {
        "image/jpeg" -> "jpg"
        "image/webp" -> "webp"
        "image/png" -> "png"
        "image/gif" -> "gif"
        else -> "bin" // Для всех других типов возвращаем расширение по умолчанию
      }
    }

    fun getMimeFromExtension(extension: String?): String {
      if (extension == null) return "" // Если MIME не найден, даём расширение по умолчанию

      return when (extension) {
        "jpg" -> "image/jpeg"
        "webp" -> "image/webp"
        "png" -> "image/png"
        "gif" -> "image/gif"
        else -> "" // Для всех других типов возвращаем расширение по умолчанию
      }
    }

    fun writeStream(outputStream: FileOutputStream, inputStream: InputStream) {
      val buffer = ByteArray(4096)
      var bytesRead: Int
      while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        outputStream.write(buffer, 0, bytesRead)
      }

      inputStream.close()
      outputStream.close()
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