package com.konformist.comicsreader.utils.archive

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.io.IOUtils
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class ArchiveExtractor {
  // Основной метод для извлечения файлов из архива
  @Throws(IOException::class)
  private fun extractFromArchive(archiveIn: InputStream, type: ArchiveFormat, outputDir: File): Boolean {
    val archiveStream = when (type) {
      ArchiveFormat.TAR -> TarArchiveInputStream(BufferedInputStream(archiveIn))
      ArchiveFormat.ZIP -> ZipArchiveInputStream(BufferedInputStream(archiveIn))
    }

    archiveStream.use { tarIn ->
      while (true) {
        val entry = tarIn.nextEntry ?: break
        if (!tarIn.canReadEntryData(entry)) continue

        val f = File(outputDir, entry.name)
        if (entry.isDirectory) {
          if (!f.isDirectory && !f.mkdirs()) {
            throw IOException("Failed to create directory $f")
          }
        } else {
          val parent = f.parentFile
          if (parent == null || (!parent.isDirectory && !parent.mkdirs()))
            throw IOException("Failed to create directory $parent")

          FileOutputStream(f).use { outFile -> IOUtils.copy(tarIn, outFile) }
        }
      }
    }
    return true
  }

  // Извлечение из InputStream с указанием типа архива
  @Throws(IOException::class)
  fun extract(archiveFile: InputStream, type: ArchiveFormat, outputDir: File): Boolean {
    return extractFromArchive(archiveFile, type, outputDir)
  }

  // Извлечение из File с указанием типа архива
  @Throws(IOException::class)
  fun extract(archiveFile: File, type: ArchiveFormat, outputDir: File): Boolean {
    val fileInputStream = FileInputStream(archiveFile)
    val result = extractFromArchive(fileInputStream, type, outputDir)
    fileInputStream.close()
    return result
  }
}