package com.konformist.comicsreader.utils

class ArchiveUtils {
  companion object {
    fun compressFactory(): CompressFiles {
      return CompressFiles()
    }

    fun extractFactory(): ExtractFiles {
      return ExtractFiles()
    }
  }
}