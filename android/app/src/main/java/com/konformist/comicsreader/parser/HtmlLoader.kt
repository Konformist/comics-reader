package com.konformist.comicsreader.parser

import com.fleeksoft.ksoup.nodes.Document
import com.konformist.comicsreader.utils.RequestUtils
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL

class HtmlLoader {
  companion object {
    private fun getDocument(stream: InputStream): Document {
      InputStreamReader(stream).use { inReader ->
        BufferedReader(inReader).use { reader ->
          val stringBuilder = StringBuilder()

          var line: String?
          while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
          }

          return Parser.getDocument(stringBuilder.toString())
        }
      }
    }

    fun loadHTML(url: String, cookie: String): Document {
      val connection = RequestUtils.getConnection(URL(url), cookie)

      connection.inputStream.use { inStream ->
        val result = getDocument(inStream)
        connection.disconnect()
        return result
      }
    }
  }
}
