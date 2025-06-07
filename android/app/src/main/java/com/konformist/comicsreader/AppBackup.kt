package com.konformist.comicsreader

import com.konformist.comicsreader.db.AppDatabase
import com.konformist.comicsreader.utils.DatesUtils
import com.konformist.comicsreader.utils.FileManager
import com.konformist.comicsreader.utils.archive.ArchiveFormat
import com.konformist.comicsreader.utils.archive.ArchiveUtils
import java.io.File
import java.io.InputStream
import java.time.LocalDate


class AppBackup {
  private val backupFileName = "backup-db"

  private val dirTmp = File("${FileManager.cacheDir}", "tmp")
  private val backupTmp = File(dirTmp, backupFileName)
  private val comicsTmp = File(dirTmp, FileManager.COMICS_DIR_NAME)

  private fun deleteTmpDir() {
    if (dirTmp.exists()) dirTmp.deleteRecursively()
  }

  fun backup(db: AppDatabase): Boolean {
    if (dirTmp.exists()) deleteTmpDir()
    dirTmp.mkdirs()

    db.close()
    FileManager.dataBaseFile.copyTo(target = backupTmp, overwrite = true)

    val backupsDir = File(FileManager.documentsAppDir, FileManager.BACKUPS_DIR_NAME)
    if (!backupsDir.exists()) backupsDir.mkdirs()

    val backupFile = File(backupsDir, "backup-${DatesUtils.dateFormatted(LocalDate.now())}.tar")
    if (!backupFile.exists()) backupFile.createNewFile()

    val compress = ArchiveUtils.compressFactory()
    compress.addFile(backupTmp)
    compress.addFile(FileManager.comicsImagesDir)
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
    backupTmp.copyTo(target = FileManager.dataBaseFile, overwrite = true)

    if (!FileManager.comicsImagesDir.exists()) FileManager.comicsImagesDir.mkdirs()
    comicsTmp.copyRecursively(target = FileManager.comicsImagesDir, overwrite = true)

    deleteTmpDir()
    return true
  }
}