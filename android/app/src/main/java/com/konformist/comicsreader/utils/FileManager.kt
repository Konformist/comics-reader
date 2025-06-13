package com.konformist.comicsreader.utils

import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import com.konformist.comicsreader.App
import com.konformist.comicsreader.db.AppDatabase
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream

class FileManager {
  companion object {
    val filesDir: File get() = App.context.filesDir
    val cacheDir: File get() = App.context.cacheDir

    val dataBaseFile: File get() = App.context.getDatabasePath(AppDatabase.DATABASE_NAME)

    const val CHAPTERS_DIR_NAME: String = "chapters"
    const val COMICS_DIR_NAME: String = "comics"
    const val BACKUPS_DIR_NAME: String = "backups"

    val comicsDir: File get() = File(filesDir, COMICS_DIR_NAME)
    val chaptersDir: File get() = File(filesDir, CHAPTERS_DIR_NAME)

    val downloadsDir: File =
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val documentsDir: File =
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

    val downloadsAppDir: File get() = File(downloadsDir, App.appName)
    val documentsAppDir: File get() = File(documentsDir, App.appName)

    val backupsDir: File get() = File(documentsAppDir, BACKUPS_DIR_NAME)

    fun getExtensionFromMime(mimeType: String?): String {
      return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: ""
    }

    fun getExtension(uriStr: String): String {
      return MimeTypeMap.getFileExtensionFromUrl(uriStr)
    }

    fun <T> getInputByUri(uri: Uri, callback: (value: FileInputStream) -> T?): T? {
      return App.context.contentResolver.openFileDescriptor(uri, "r")?.use {
        FileInputStream(it.fileDescriptor).use { fileIn ->
          return callback(fileIn)
        }
      }
    }

    fun writeStream(outFile: File, inputStream: InputStream) {
      FileOutputStream(outFile).use { it ->
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
          it.write(buffer, 0, bytesRead)
        }
      }
    }

    fun write(file: File, data: String): Boolean {
      val parentDir = file.parent ?: return false
      val parentFile = File(parentDir)

      if (!parentFile.exists()) parentFile.mkdirs()

      try {
        val outputStreamWriter = FileWriter(file)
        outputStreamWriter.write(data)
        outputStreamWriter.close()
        return true
      } catch (_: IOException) {
        return false
      }
    }

    @Serializable
    data class FileNode(
      val name: String,
      val path: String,
      val isDirectory: Boolean,
      val mimeType: String? = null,
      val size: Long = 0L,
      val lastModified: Long = 0L,
      val children: List<FileNode>? = null,
    )

    fun getMimeType(file: String): String {
      val extension = getExtension(file)
      val result = if (extension.isEmpty()) null
      else MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
      return result ?: ""
    }

    fun getMimeType(file: File): String {
      return getMimeType(file.name)
    }

    private fun getDirectory(file: File, children: List<FileNode>): FileNode {
      return FileNode(
        name = file.name,
        path = Uri.fromFile(file).toString(),
        isDirectory = true,
        lastModified = file.lastModified(),
        children = children,
      )
    }

    private fun getFile(file: File): FileNode {
      return FileNode(
        name = file.name,
        path = Uri.fromFile(file).toString(),
        isDirectory = false,
        size = file.length(),
        lastModified = file.lastModified(),
        mimeType = getMimeType(file),
      )
    }

    fun getFileTree(rootFile: File): FileNode {
      if (!rootFile.isDirectory) return getFile(rootFile)

      // Рекурсивно обрабатываем содержимое директории
      val children = rootFile.listFiles()?.map {
        if (it.isDirectory) getFileTree(it)
        else getFile(it)
      }?.toList() ?: emptyList()

      return getDirectory(rootFile, children)
    }
  }
}