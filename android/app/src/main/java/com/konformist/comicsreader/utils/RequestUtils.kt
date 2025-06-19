package com.konformist.comicsreader.utils

import android.content.Intent
import com.konformist.comicsreader.App
import com.konformist.comicsreader.CaptchaWebViewActivity
import java.net.HttpURLConnection
import java.net.URL

class RequestUtils {
  companion object {
    //    const val CAPTCHA_REQUEST_CODE = 1001
    private fun updateAsBrowser(url: URL, connection: HttpURLConnection, cookie: String) {
      if (cookie.isNotBlank()) connection.setRequestProperty("Cookie", cookie)
      connection.setRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36"
      )
      connection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
      connection.setRequestProperty("Priority", "u=0, i")
      connection.setRequestProperty("Referer", "${url.protocol}://${url.host}")
      connection.setRequestProperty(
        "sec-ch-ua",
        "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\""
      )
      connection.setRequestProperty("sec-ch-ua-arch", "\"x86\"")
      connection.setRequestProperty("sec-ch-ua-bitness", "\"64\"")
      connection.setRequestProperty("sec-ch-ua-full-version", "\"136.0.7103.114\"")
      connection.setRequestProperty(
        "sec-ch-ua-full-version-list",
        "\"Chromium\";v=\"136.0.7103.114\", \"Google Chrome\";v=\"136.0.7103.114\", \"Not.A/Brand\";v=\"99.0.0.0\""
      )
      connection.setRequestProperty("sec-ch-ua-mobile", "?0")
      connection.setRequestProperty("sec-ch-ua-model", "\"\"")
      connection.setRequestProperty("sec-ch-ua-platform", "\"Windows\"")
      connection.setRequestProperty("sec-ch-ua-platform-version", "\"19.0.0\"")
      connection.setRequestProperty("sec-fetch-dest", "document")
      connection.setRequestProperty("sec-fetch-mode", "navigate")
      connection.setRequestProperty("sec-fetch-site", "same-origin")
      connection.setRequestProperty("sec-fetch-user", "?1")
      connection.setRequestProperty("upgrade-insecure-requests", "1")
    }

    fun getConnection(url: URL, cookie: String = ""): HttpURLConnection {
      val connection = url.openConnection() as HttpURLConnection
      connection.requestMethod = "GET"
      updateAsBrowser(url, connection, cookie)
      connection.connect()

      if (connection.responseCode == 403) {
        val intent = Intent(App.context, CaptchaWebViewActivity::class.java)
        intent.putExtra("captcha_url", url)
        App.context.startActivity(intent)
      } else if (connection.responseCode != 200) {
        throw Exception("Error connection: ${connection.responseCode}")
      }

      return connection
    }

    fun postConnection(url: URL, cookie: String = ""): HttpURLConnection {
      val connection = url.openConnection() as HttpURLConnection
      connection.requestMethod = "POST"
      updateAsBrowser(url, connection, cookie)
      connection.connect()

      if (connection.responseCode == 403) {
        val intent = Intent(App.context, CaptchaWebViewActivity::class.java)
        intent.putExtra("captcha_url", url)
        App.context.startActivity(intent)
      } else if (connection.responseCode != 200) {
        throw Exception("Error connection: ${connection.responseCode}")
      }

      return connection
    }
  }
}