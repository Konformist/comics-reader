package com.konformist.comicsreader.db

import android.content.Context
import android.util.Log
import com.konformist.comicsreader.utils.AppDirectory
import com.konformist.comicsreader.utils.DatesUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.FileChannel
import java.time.LocalDate


class DBBackup(val context: Context) {
  fun backup(db: AppDatabase) {
    db.close()

    val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
    val saveDir = File("${context.filesDir}${File.separator}${AppDirectory.BACKUPS}")
    val fileName = "backup-${DatesUtils.dateFormatted(LocalDate.now())}"
    val saveFilePath = "${saveDir.path}${File.separator}${fileName}"

    if (!saveDir.exists()) {
      saveDir.mkdirs()
    }

    val saveFile = File(saveFilePath)
    if (saveFile.exists()) {
      Log.d("Backup", "File exists. Deleting it and then creating new file.")
      saveFile.delete()
    }

    try {
      if (saveFile.createNewFile()) {
        val bufferSize = 8 * 1024
        val buffer = ByteArray(bufferSize)
        val saveDB: OutputStream = FileOutputStream(saveFilePath)
        val inputDB: InputStream = FileInputStream(dbFile)

        var bytesRead: Int

        while ((inputDB.read(buffer, 0, bufferSize).also { bytesRead = it }) > 0) {
          saveDB.write(buffer, 0, bytesRead)
        }

        saveDB.flush()
        inputDB.close()
        saveDB.close()
      }
    } catch (e: Exception) {
      e.printStackTrace()
      Log.d("Backup", "ex: $e")
    }
  }

  @Throws(IOException::class)
  private fun copyFile(fromFile: FileInputStream, toFile: FileOutputStream) {
    var fromChannel: FileChannel? = null
    var toChannel: FileChannel? = null
    try {
      fromChannel = fromFile.channel
      toChannel = toFile.channel
      fromChannel.transferTo(0, fromChannel.size(), toChannel)
    } finally {
      try {
        fromChannel?.close()
      } finally {
        toChannel?.close()
      }
    }
  }

  fun restore(db: AppDatabase, path: String) {
    val file =
      File("${context.filesDir}${File.separator}${AppDirectory.BACKUPS}${File.separator}${path}")
    val inputStream = FileInputStream(file)

    db.close()

    val oldDB: File = context.getDatabasePath(AppDatabase.DATABASE_NAME)

    try {
      copyFile(inputStream, FileOutputStream(oldDB))
    } catch (e: IOException) {
      Log.d("Database", "ex for is of restore: $e")
      e.printStackTrace()
    }
  }
}