package com.konformist.comicsreader

import android.content.Context
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.utils.AppDirectory
import com.konformist.comicsreader.utils.ArchiveUtils
import com.konformist.comicsreader.utils.DatesUtils
import java.io.File
import java.time.LocalDate


class AppBackup(private val context: Context) {
  private val backupFileName = "backup-db"
  private val tmpDir = File("${context.cacheDir}${File.separator}tmp")

  fun backup(db: AppDatabase) {
    if (tmpDir.exists()) tmpDir.delete()
    tmpDir.mkdirs()

    db.close()

    val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
    val saveFilePath = File("${tmpDir}${File.separator}${backupFileName}")
    dbFile.copyTo(target = saveFilePath, overwrite = true)

    val tarFile = File("${tmpDir}${File.separator}backup.tar")
    if (!tarFile.exists()) tarFile.createNewFile()
    val compress = ArchiveUtils.compressFactory()
    compress.addFile(saveFilePath)
    compress.addFile(File("${context.filesDir}${File.separator}${AppDirectory.COMICS_IMAGES}"))
    compress.compress(tarFile)

    val backupFile = File(
      "${context.filesDir}${File.separator}${AppDirectory.BACKUPS}${File.separator}backup-${
        DatesUtils.dateFormatted(LocalDate.now())
      }.tar"
    )
    tarFile.copyTo(target = backupFile, overwrite = true)
    tmpDir.delete()
  }

  fun restore(db: AppDatabase, path: String) {
    if (tmpDir.exists()) tmpDir.delete()
    tmpDir.mkdirs()

    val tarFile =
      File("${context.filesDir}${File.separator}${AppDirectory.BACKUPS}${File.separator}${path}")
    if (!tarFile.exists()) tarFile.createNewFile()
    val extract = ArchiveUtils.extractFactory()
    extract.extract(tarFile, tmpDir)

    db.close()

    val newDB = File("${tmpDir}${File.separator}${backupFileName}")
    val oldDB: File = context.getDatabasePath(AppDatabase.DATABASE_NAME)
    newDB.copyTo(target = oldDB, overwrite = true)

    val oldComics = File("${context.filesDir}${File.separator}${AppDirectory.COMICS_IMAGES}")
    val newComics = File("${tmpDir}${File.separator}${AppDirectory.COMICS_IMAGES}")
    newComics.copyRecursively(target = oldComics, overwrite = true)

    tmpDir.delete()
  }
}