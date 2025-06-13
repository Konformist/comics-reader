package com.konformist.comicsreader.data.appfile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.konformist.comicsreader.db.AppDataStore
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.FileManager
import com.konformist.comicsreader.utils.ImageUtils
import com.konformist.comicsreader.webapi.Validation
import java.io.File
import java.io.InputStream

class AppFileController(private val appFileDao: AppFileDao) {
  private val entityName = "AppFile"

  fun readAll(): List<AppFile> {
    return appFileDao.readAll()
  }

  fun read(id: Long): AppFile? {
    return appFileDao.read(id)
  }

  @Throws(DatabaseException::class)
  fun create(value: AppFileCreate): Long {
    val rowId = appFileDao.create(value)
    Validation.dbCreate(rowId, entityName)
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(value: AppFileUpdate): Boolean {
    val row = read(value.id) ?: return false
    val count = appFileDao.update(value.merge(row))
    Validation.dbUpdate(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun delete(value: AppFileDelete): Boolean {
    val count = appFileDao.delete(value)
    Validation.dbDelete(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun deleteImage(id: Long?): Boolean {
    if (id == null || id == 0L) return false
    val row = read(id) ?: return false

    val fileOut = File(FileManager.filesDir, row.path)
    if (fileOut.exists()) fileOut.delete()

    return delete(AppFileDelete(id = row.id))
  }

  @Throws(DatabaseException::class)
  fun createImage(parentDir: File, mime: String, file: InputStream): Long {
    val rowId = create(
      AppFileCreate(
        name = "",
        mime = "",
        size = 0,
        path = "",
      )
    )

    if (!parentDir.exists()) parentDir.mkdirs()
    val extension = if (AppDataStore.settings.isCompress) "webp"
    else FileManager.getExtensionFromMime(mime)

    val fileOut = File(parentDir, "$rowId.$extension")

    try {
      if (AppDataStore.settings.isCompress) {
        val decodedImage: Bitmap = BitmapFactory.decodeStream(file)
        ImageUtils.writeStream(fileOut, decodedImage, 80)
        decodedImage.recycle()
      } else {
        FileManager.writeStream(fileOut, file)
      }

      Log.d("AppFile", fileOut.relativeTo(FileManager.filesDir).path)

      update(
        AppFileUpdate(
          id = rowId,
          mdate = "",
          name = fileOut.name,
          mime = mime,
          size = fileOut.length(),
          path = fileOut.relativeTo(FileManager.filesDir).path,
        )
      )

      return rowId
    } catch (e: Error) {
      if (fileOut.exists()) fileOut.delete()
      appFileDao.delete(AppFileDelete(id = rowId))
      throw e
    }
  }
}