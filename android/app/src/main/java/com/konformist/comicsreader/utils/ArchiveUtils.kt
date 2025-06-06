package com.konformist.comicsreader.utils

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