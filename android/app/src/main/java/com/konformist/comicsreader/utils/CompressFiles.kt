package com.konformist.comicsreader.utils

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class CompressFiles {
  private val files: MutableList<File> = mutableListOf()

  @Throws(IOException::class)
  private fun addFileToTar(tarOut: TarArchiveOutputStream, file: File, parent: String) {
    val entryName = parent + file.name
    val tarEntry = tarOut.createArchiveEntry(file, entryName)
    tarOut.putArchiveEntry(tarEntry)

    if (file.isFile) {
      IOUtils.copy(FileInputStream(file), tarOut)
      tarOut.closeArchiveEntry()
    } else if (file.isDirectory) {
      tarOut.closeArchiveEntry()
      val children = file.listFiles()
      if (children != null) {
        for (child in children) {
          addFileToTar(tarOut, child, "$entryName/")
        }
      }
    }
  }

  fun addFile(file: File) {
    files.add(file)
  }

  fun compress(fileOut: File) {
    val outStream = FileOutputStream(fileOut)
    val tarOutStream = TarArchiveOutputStream(outStream)
    tarOutStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR)

    for (file in files) {
      addFileToTar(tarOutStream, file, "")
    }
  }
}