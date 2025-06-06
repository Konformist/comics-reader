package com.konformist.comicsreader

import android.content.Context
import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.utils.AppDirectory
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.utils.archive.ArchiveFormat
import com.konformist.comicsreader.utils.archive.ArchiveUtils
import java.io.File
import java.io.InputStream
import java.time.LocalDate


class AppBackup(
  private val documentsApp: File,
  private val comicsImagesDir: File,
  context: Context,
) {
  private val backupFileName = "backup-db"
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

    val backupsDir = File("${documentsApp}${File.separator}${AppDirectory.BACKUPS}")
    if (!backupsDir.exists()) backupsDir.mkdirs()

    val backupFile =
      File("${backupsDir}${File.separator}backup-${DatesUtils.dateFormatted(LocalDate.now())}.tar")
    if (!backupFile.exists()) backupFile.createNewFile()

    val compress = ArchiveUtils.compressFactory()
    compress.addFile(backupTmp)
    compress.addFile(comicsImagesDir)
    compress.compress(backupFile, ArchiveFormat.TAR)

    deleteTmpDir()
    return true
  }

  fun restore(db: AppDatabase, tarFile: InputStream): Boolean {
    if (dirTmp.exists()) deleteTmpDir()
    dirTmp.mkdirs()

    val extract = ArchiveUtils.extractFactory()
    extract.extract(tarFile, ArchiveFormat.TAR, dirTmp)

    if (!backupTmp.exists() || !comicsTmp.exists()) return false
    db.close()
    backupTmp.copyTo(target = dbFile, overwrite = true)

    if (!comicsImagesDir.exists()) comicsImagesDir.mkdirs()
    comicsTmp.copyRecursively(target = comicsImagesDir, overwrite = true)

    deleteTmpDir()
    return true
  }
}