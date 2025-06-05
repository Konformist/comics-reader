package com.konformist.comicsreader

import android.content.Context
import android.os.Environment
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.utils.AppDirectory
import com.konformist.comicsreader.utils.ArchiveUtils
import com.konformist.comicsreader.utils.DatesUtils
import java.io.File
import java.io.InputStream
import java.time.LocalDate


class AppBackup(private val appName: String, context: Context) {
  private val backupFileName = "backup-db"
  private val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

  private val comicsPath = File("${context.filesDir}${File.separator}${AppDirectory.COMICS_IMAGES}")
  private val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)

  private val dirTmp = File("${context.cacheDir}${File.separator}tmp")
  private val backupTmp = File("${dirTmp}${File.separator}${backupFileName}")
  private val comicsTmp = File("${dirTmp}${File.separator}${AppDirectory.COMICS_IMAGES}")

  private fun deleteTmpDir() {
    if (dirTmp.exists()) dirTmp.deleteRecursively()
  }

  fun backup(db: AppDatabase): Boolean {
    if (dirTmp.exists()) deleteTmpDir()
    dirTmp.mkdirs()

    db.close()
    dbFile.copyTo(target = backupTmp, overwrite = true)

    val backupsDir = File("${documentsDir}${File.separator}${appName}${File.separator}${AppDirectory.BACKUPS}")
    if (!backupsDir.exists()) backupsDir.mkdirs()

    val backupFile = File("${backupsDir}${File.separator}backup-${DatesUtils.dateFormatted(LocalDate.now())}.tar")
    if (!backupFile.exists()) backupFile.createNewFile()

    val compress = ArchiveUtils.compressFactory()
    compress.addFile(backupTmp)
    compress.addFile(comicsPath)
    compress.compress(backupFile)

    deleteTmpDir()
    return true
  }

  fun restore(db: AppDatabase, tarFile: InputStream): Boolean {
    if (dirTmp.exists()) deleteTmpDir()
    dirTmp.mkdirs()

    val extract = ArchiveUtils.extractFactory()
    extract.extract(tarFile, dirTmp)

    if (!backupTmp.exists() || !comicsTmp.exists()) return false
    db.close()
    backupTmp.copyTo(target = dbFile, overwrite = true)

    if (!comicsPath.exists()) comicsPath.mkdirs()
    comicsTmp.copyRecursively(target = comicsPath, overwrite = true)

    deleteTmpDir()
    return true
  }

  fun restore(db: AppDatabase, tarFile: File): Boolean {
    if (dirTmp.exists()) deleteTmpDir()
    dirTmp.mkdirs()

    if (!tarFile.exists()) return false
    val extract = ArchiveUtils.extractFactory()
    extract.extract(tarFile, dirTmp)

    if (!backupTmp.exists() || !comicsTmp.exists()) return false
    db.close()
    backupTmp.copyTo(target = dbFile, overwrite = true)

    if (!comicsPath.exists()) comicsPath.mkdirs()
    comicsTmp.copyRecursively(target = comicsPath, overwrite = true)

    deleteTmpDir()
    return true
  }
}