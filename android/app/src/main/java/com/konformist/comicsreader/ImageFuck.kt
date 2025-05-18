package com.konformist.comicsreader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import java.io.File
import java.io.FileOutputStream

@CapacitorPlugin(name = "ImageFuck")
class ImageFuck : Plugin() {
    private fun writeFile(file: File, bitmap: Bitmap) {
        var outStream: FileOutputStream? = null

        try {
            outStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, outStream)
        } finally {
            if (outStream != null) {
                outStream.flush()
                outStream.close()
            }
        }
    }

    @PluginMethod
    fun resize(call: PluginCall) {
        val zero = 0F

        val path = call.getString("path", "")
        val width = call.getFloat("width", zero)!!
        val height = call.getFloat("height", zero)!!

        if (width == zero && height == zero) {
            call.resolve()
        }

        val file = File("${context.filesDir}/$path")
        val bitmap = BitmapFactory.decodeFile(file.path)

        if (bitmap.width < width && bitmap.height < height) {
            call.resolve()
        }

        val imgWidth = bitmap.width.toFloat()
        val imgHeight = bitmap.height.toFloat()

        val coef = if (width != zero && height != zero) {
            if ((imgHeight / imgWidth) < (height / width)) {
                width / imgWidth
            } else {
                height / imgHeight
            }
        } else if (width != zero) {
            width / imgWidth
        } else {
            height / imgHeight
        }

        val resizedImage = Bitmap.createScaledBitmap(bitmap, (imgWidth * coef).toInt(), (imgHeight * coef).toInt(), true)

        writeFile(file, resizedImage)
        call.resolve()
    }

    @PluginMethod
    fun optimization(call: PluginCall) {
        val filePath: String = call.getString("path", "")!!
        val fileBase64: String = call.getString("data", "")!!
            .replace("data:image/jpeg;base64,", "")
            .replace("data:image/webp;base64,", "")
            .replace("data:image/png;base64,", "")
            .replace("data:image/gif;base64,", "")

        val decodedBytes: ByteArray = Base64.decode(fileBase64, Base64.DEFAULT)
        val decodedImage: Bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        val fileOut = File("${context.filesDir}/${filePath}")
        val dirOut = File(fileOut.parent!!)

        if (!dirOut.exists()) {
            dirOut.mkdirs()
        }

        writeFile(fileOut, decodedImage)
        call.resolve()
    }
}
