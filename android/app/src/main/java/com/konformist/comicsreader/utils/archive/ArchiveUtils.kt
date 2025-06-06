package com.konformist.comicsreader.utils.archive

class ArchiveUtils {
  companion object {
    fun compressFactory(): ArchiveCompressor {
      return ArchiveCompressor()
    }

    fun extractFactory(): ArchiveExtractor {
      return ArchiveExtractor()
    }
  }
}