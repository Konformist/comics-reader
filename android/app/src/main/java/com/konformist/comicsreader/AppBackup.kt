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
  private val chaptersTmp = File(dirTmp, FileManager.CHAPTERS_DIR_NAME)

  private fun deleteTempDir() {
    if (dirTmp.exists()) dirTmp.deleteRecursively()
  }

  private fun createTempDir() {
    if (dirTmp.exists()) dirTmp.deleteRecursively()
    dirTmp.mkdirs()
  }

  fun backup(db: AppDatabase): Boolean {
    createTempDir()

    db.close()
    FileManager.dataBaseFile.copyTo(target = backupTmp, overwrite = true)

    val backupsDir = FileManager.backupsDir
    if (!backupsDir.exists()) backupsDir.mkdirs()

    val backupFile = File(backupsDir, "backup-${DatesUtils.dateFormatted(LocalDate.now())}.tar")
    if (!backupFile.exists()) backupFile.createNewFile()

    val compress = ArchiveUtils.compressFactory()
    compress.addFile(backupTmp)
    compress.addFile(FileManager.comicsDir)
    compress.addFile(FileManager.chaptersDir)
    compress.compress(backupFile, ArchiveFormat.TAR)

    deleteTempDir()
    return true
  }

  fun restore(db: AppDatabase, tarFile: InputStream): Boolean {
    createTempDir()

    val extract = ArchiveUtils.extractFactory()
    extract.extract(tarFile, ArchiveFormat.TAR, dirTmp)

    if (!backupTmp.exists()
      || !comicsTmp.exists()
      || !chaptersTmp.exists())
      return false

    db.close()
    backupTmp.copyTo(target = FileManager.dataBaseFile, overwrite = true)

    if (!FileManager.comicsDir.exists()) FileManager.comicsDir.mkdirs()
    comicsTmp.copyRecursively(target = FileManager.comicsDir, overwrite = true)

    if (!FileManager.chaptersDir.exists()) FileManager.chaptersDir.mkdirs()
    chaptersTmp.copyRecursively(target = FileManager.chaptersDir, overwrite = true)

    deleteTempDir()
    return true
  }
}