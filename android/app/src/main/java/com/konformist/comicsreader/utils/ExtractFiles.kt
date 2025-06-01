package com.konformist.comicsreader.utils

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.io.IOUtils
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class ExtractFiles {
  @Throws(IOException::class)
  fun extract(tarFile: File, outputDir: File): Boolean {
    val fis = BufferedInputStream(FileInputStream(tarFile))
    val tarIn = TarArchiveInputStream(fis)

    while (true) {
      val entry = tarIn.nextEntry ?: break
      if (!tarIn.canReadEntryData(entry)) {
        // log something?
        continue
      }

      val f = File(outputDir, entry.name)
      if (entry.isDirectory) {
        if (!f.isDirectory && !f.mkdirs()) {
          throw IOException("failed to create directory $f")
        }
      } else {
        val parent = f.parentFile
        if (parent == null || (!parent.isDirectory && !parent.mkdirs())) {
          throw IOException("failed to create directory $parent")
        }
        val outFile = FileOutputStream(f)
        IOUtils.copy(tarIn, outFile)
        outFile.close()
      }
    }

    tarIn.close()
    return true
  }
}