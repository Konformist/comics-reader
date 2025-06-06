package com.konformist.comicsreader.utils

import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.Deflater

class ArchiveCompressor {
  private val files: MutableList<File> = mutableListOf()

  @Throws(IOException::class)
  private fun <T : ArchiveEntry> addFileToArchive(
    tarOut: ArchiveOutputStream<T>,
    file: File,
    parent: String
  ) {
    val entryName = parent + file.name
    val tarEntry = tarOut.createArchiveEntry(file, entryName)
    tarOut.putArchiveEntry(tarEntry)

    if (file.isFile) {
      IOUtils.copy(FileInputStream(file), tarOut)
      tarOut.closeArchiveEntry()
    } else if (file.isDirectory) {
      tarOut.closeArchiveEntry()
      file.listFiles()?.forEach { child ->
        addFileToArchive(tarOut, child, "$entryName/")
      }
    }
  }

  fun addFile(file: File) {
    files.add(file)
  }

  fun compress(archiveOut: File, format: ArchiveFormat) {
    return FileOutputStream(archiveOut).use { outStream ->
      val archiveOutStream =  when (format) {
        ArchiveFormat.TAR -> TarArchiveOutputStream(outStream).apply {
          setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR)
        }
        ArchiveFormat.ZIP -> ZipArchiveOutputStream(outStream).apply {
          setLevel(Deflater.BEST_COMPRESSION)
        }
      }

      archiveOutStream.use { stream ->
        files.forEach { file -> addFileToArchive(archiveOutStream, file, "") }
        stream.finish()
      }
    }
  }
}