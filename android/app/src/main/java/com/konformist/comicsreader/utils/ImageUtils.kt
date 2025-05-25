package com.konformist.comicsreader.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.File
import java.io.FileOutputStream

class ImageUtils {
  companion object {
    private const val SIZE_ZERO = 0

    fun write(filePath: File, bitmap: Bitmap, quality: Int = 100) {
      var outStream: FileOutputStream? = null

      try {
        outStream = FileOutputStream(filePath)
        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, quality, outStream)
      } finally {
        if (outStream != null) {
          outStream.flush()
          outStream.close()
        }
      }
    }

    fun base64ToBitmap(value: String): Bitmap {
      val cleanedImage = FileUtils.cleanBase64(value)
      val decodedBytes: ByteArray = Base64.decode(cleanedImage, Base64.DEFAULT)
      return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    /**
     * @return true - размер изменён, false - осталось как есть
     */
    fun resize(file: File, maxWidth: Int, maxHeight: Int): Boolean {
      if (maxWidth == SIZE_ZERO && maxHeight == SIZE_ZERO) return false

      val bitmap = BitmapFactory.decodeFile(file.path)

      if (bitmap.width < maxWidth && bitmap.height < maxHeight) return false

      val imgWidthFloat = bitmap.width.toFloat()
      val imgHeightFloat = bitmap.height.toFloat()
      val maxWidthFloat = maxWidth.toFloat()
      val maxHeightFloat = maxHeight.toFloat()

      val coef = if (maxWidth != SIZE_ZERO && maxHeight != SIZE_ZERO) {
        if ((imgHeightFloat / imgWidthFloat) < (maxHeightFloat / maxWidthFloat)) {
          maxWidthFloat / imgWidthFloat
        } else {
          maxHeightFloat / imgHeightFloat
        }
      } else if (maxWidth != SIZE_ZERO) {
        maxWidthFloat / imgWidthFloat
      } else {
        maxHeightFloat / imgHeightFloat
      }

      write(
        file, Bitmap.createScaledBitmap(
          bitmap,
          (imgWidthFloat * coef).toInt(),
          (imgHeightFloat * coef).toInt(),
          true
        )
      )

      return true
    }
  }
}