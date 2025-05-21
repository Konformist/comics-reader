package com.konformist.comicsreader.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.File
import java.io.FileOutputStream

class FileUtils {
  companion object {
    fun cleanImageData(value: String): String {
      return value
        .replace("data:image/jpeg;base64,", "")
        .replace("data:image/webp;base64,", "")
        .replace("data:image/png;base64,", "")
        .replace("data:image/gif;base64,", "")
    }

    fun base64ToBitmap(value: String): Bitmap {
      val decodedBytes: ByteArray = Base64.decode(value, Base64.DEFAULT)
      return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun writeImage(file: File, bitmap: Bitmap) {
      var outStream: FileOutputStream? = null

      try {
        outStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, outStream)
      } finally {
        if (outStream != null) {
          outStream.flush()
          outStream.close()
        }
      }
    }
  }
}