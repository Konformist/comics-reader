package com.konformist.comicsreader.webapi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.konformist.comicsreader.db.appfile.AppFileCreate
import com.konformist.comicsreader.db.appfile.AppFileDao
import com.konformist.comicsreader.db.appfile.AppFileDelete
import com.konformist.comicsreader.db.appfile.AppFileUpdate
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.utils.FileUtils
import com.konformist.comicsreader.utils.ImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AppFilesController(
  private val appFileDao: AppFileDao,
  private val comicsImagesDir: File,
) {
  @Throws(DatabaseException::class)
  fun deleteImage(id: Long?): Boolean {
    if (id == null || id == 0L) return false
    val row = appFileDao.read(id) ?: return false

    val fileOut = File(row.path)
    if (fileOut.exists()) fileOut.delete()

    val count = appFileDao.delete(AppFileDelete(id = row.id))
    Validation.dbDelete(count, "AppFile")
    return true
  }

  @Throws(DatabaseException::class)
  fun createImage(mime: String, file: InputStream): Long {
    val rowId = appFileDao.create(
      AppFileCreate(
        name = "",
        mime = "",
        size = 0,
        path = "",
      )
    )
    Validation.dbCreate(rowId, "AppFile")

    if (!comicsImagesDir.exists()) comicsImagesDir.mkdirs()

    val extension = if (AppDataStore.settings.isCompress) "webp"
    else FileUtils.getExtensionFromMime(mime)

    val fileOut = File("${comicsImagesDir}${File.separator}${rowId}.${extension}")

    try {
      if (AppDataStore.settings.isCompress) {
        val decodedImage: Bitmap = BitmapFactory.decodeStream(file)
        FileOutputStream(fileOut).use { item ->
          ImageUtils.writeStream(item, decodedImage, 80)
        }
        decodedImage.recycle()
      } else {
        FileOutputStream(fileOut).use { item ->
          FileUtils.writeStream(item, file)
        }
      }

      val count = appFileDao.update(
        AppFileUpdate(
          id = rowId,
          mdate = DatesUtils.nowFormatted(),
          name = fileOut.name,
          mime = mime,
          size = fileOut.length(),
          path = fileOut.path,
        )
      )
      Validation.dbUpdate(count, "AppFile")

      return rowId
    } catch (e: Error) {
      if (fileOut.exists()) fileOut.delete()
      appFileDao.delete(AppFileDelete(id = rowId))
      throw e
    }
  }
}