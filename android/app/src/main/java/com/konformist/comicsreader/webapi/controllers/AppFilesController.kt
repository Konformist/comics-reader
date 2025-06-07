package com.konformist.comicsreader.webapi.controllers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.konformist.comicsreader.db.appfile.AppFile
import com.konformist.comicsreader.db.appfile.AppFileCreate
import com.konformist.comicsreader.db.appfile.AppFileDao
import com.konformist.comicsreader.db.appfile.AppFileDelete
import com.konformist.comicsreader.db.appfile.AppFileUpdate
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.utils.FileManager
import com.konformist.comicsreader.utils.ImageUtils
import com.konformist.comicsreader.webapi.AppDataStore
import com.konformist.comicsreader.webapi.Validation
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AppFilesController(private val appFileDao: AppFileDao) {
  private val entityName = "AppFile"

  fun readAll(): List<AppFile> {
    return appFileDao.readAll()
  }

  fun read(id: Long): AppFile? {
    return appFileDao.read(id)
  }

  @Throws(DatabaseException::class)
  fun create(file: AppFileCreate): Long {
    val rowId = appFileDao.create(file)
    Validation.dbCreate(rowId, entityName)
    return rowId
  }

  @Throws(DatabaseException::class)
  fun update(file: AppFileUpdate): Boolean {
    file.mdate = DatesUtils.nowFormatted()
    val count = appFileDao.update(file)
    Validation.dbUpdate(count, entityName)

    return true
  }

  @Throws(DatabaseException::class)
  fun deleteImage(id: Long?): Boolean {
    if (id == null || id == 0L) return false
    val row = read(id) ?: return false

    val fileOut = File(FileManager.filesDir, row.path)
    if (fileOut.exists()) fileOut.delete()

    val count = appFileDao.delete(AppFileDelete(id = row.id))
    Validation.dbDelete(count, entityName)
    return true
  }

  @Throws(DatabaseException::class)
  fun createImage(mime: String, file: InputStream): Long {
    val rowId = create(
      AppFileCreate(
        name = "",
        mime = "",
        size = 0,
        path = "",
      )
    )

    if (!FileManager.comicsImagesDir.exists())
      FileManager.comicsImagesDir.mkdirs()

    val extension = if (AppDataStore.settings.isCompress) "webp"
    else FileManager.getExtensionFromMime(mime)

    val fileOut = File(FileManager.comicsImagesDir, "$rowId.$extension")

    try {
      if (AppDataStore.settings.isCompress) {
        val decodedImage: Bitmap = BitmapFactory.decodeStream(file)
        FileOutputStream(fileOut).use { item ->
          ImageUtils.writeStream(item, decodedImage, 80)
        }
        decodedImage.recycle()
      } else {
        FileOutputStream(fileOut).use { item ->
          FileManager.writeStream(item, file)
        }
      }

      update(
        AppFileUpdate(
          id = rowId,
          mdate = "",
          name = fileOut.name,
          mime = mime,
          size = fileOut.length(),
          path = "${FileManager.COMICS_DIR_NAME}/${fileOut.name}",
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