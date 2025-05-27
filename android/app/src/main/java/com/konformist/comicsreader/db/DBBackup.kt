package com.konformist.comicsreader.db

import android.content.Context
import com.konformist.comicsreader.utils.AppDirectory
import com.konformist.comicsreader.utils.DatesUtils
import java.io.File
import java.time.LocalDate


class DBBackup(private val context: Context) {
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
    dbFile.copyTo(target = saveFile, overwrite = true)
  }

  fun restore(db: AppDatabase, path: String) {
    val file =
      File("${context.filesDir}${File.separator}${AppDirectory.BACKUPS}${File.separator}${path}")

    db.close()

    val oldDB: File = context.getDatabasePath(AppDatabase.DATABASE_NAME)
    file.copyTo(target = oldDB, overwrite = true)
  }
}