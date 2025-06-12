package com.konformist.comicsreader.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
import java.io.File
import java.io.FileOutputStream

class ImageUtils {
  companion object {
    private const val SIZE_ZERO = 0

    fun writeStream(filePath: File, bitmap: Bitmap, quality: Int = 100) {
      FileOutputStream(filePath).use {
        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, quality, it)
      }
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

      writeStream(
        file,
        bitmap.scale((imgWidthFloat * coef).toInt(), (imgHeightFloat * coef).toInt()),
      )

      bitmap.recycle()

      return true
    }
  }
}