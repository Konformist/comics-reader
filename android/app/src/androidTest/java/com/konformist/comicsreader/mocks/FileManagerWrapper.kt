package com.konformist.comicsreader.mocks

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

open class FileManagerMock {
    fun writeStream(outputFile: File, inputStream: InputStream) {
        // Здесь просто записываем в файл, но можем подменить в тестах
        FileOutputStream(outputFile).use { output ->
            inputStream.copyTo(output)
        }
    }

//    fun getFile(path: String): File {
//        return File(path)
//    }
}