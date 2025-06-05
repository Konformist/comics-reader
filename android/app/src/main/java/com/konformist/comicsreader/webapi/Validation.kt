package com.konformist.comicsreader.webapi

import com.konformist.comicsreader.exceptions.ValidationException
import androidx.core.net.toUri
import com.konformist.comicsreader.exceptions.DatabaseException
import com.konformist.comicsreader.exceptions.FilesException
import java.io.File

class Validation {
  companion object {
    fun id(value: Long?, field: String) {
      if (value == 0L || value == null) throw ValidationException("$field is empty")
    }

    fun string(value: String?, field: String) {
      if (value.isNullOrBlank()) throw ValidationException("$field is empty")
    }

    fun link(value: String?, field: String) {
      string(value, field)
      if (value != null && !value.contains(Regex("^https?://"))) {
        throw ValidationException("$field not link")
      }
    }

    fun uri(value: String?, field: String) {
      string(value, field)
      try {
        value?.toUri()
      } catch (e: Exception) {
        throw ValidationException("$field not uri")
      }
    }

    fun <T> contain(value: T?, data: List<T>, field: String) {
      if (value == null) throw ValidationException("$field is empty")
      if (!data.contains(value)) throw ValidationException("$field invalid, use $data")
    }

    fun dbCreate(value: Long, entity: String) {
      if (value == 0L) throw DatabaseException("$entity not created")
    }

    fun dbUpdate(value: Int, entity: String) {
      if (value == 0) throw DatabaseException("$entity not updated")
    }

    fun dbDelete(value: Int, entity: String) {
      if (value == 0) throw DatabaseException("$entity not deleted")
    }

    fun fileCreate(result: Boolean, file: File) {
      if (result) return;
      if (file.isDirectory) throw FilesException("Directory \"$file\" not created")
      else throw FilesException("File \"$file\" not created")
    }

    fun fileDelete(result: Boolean, file: File) {
      if (result) return;
      if (file.isDirectory) throw FilesException("Directory \"$file\" not deleted")
      else throw FilesException("File \"$file\" not deleted")
    }
  }
}