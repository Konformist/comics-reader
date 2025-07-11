package com.konformist.comicsreader.utils

import android.content.Intent
import com.konformist.comicsreader.App
import com.konformist.comicsreader.CaptchaWebViewActivity
import com.konformist.comicsreader.db.GlobalCookieManager
import kotlinx.coroutines.runBlocking
import java.net.HttpURLConnection
import java.net.URL

class RequestUtils {
  companion object {
    fun getDomain(url: URL): String = "${url.protocol}://${url.host}"
    fun getDomain(url: String): String = getDomain(URL(url))

    //    const val CAPTCHA_REQUEST_CODE = 1001
    private fun updateAsBrowser(url: URL, connection: HttpURLConnection, cookie: String) {
      val cookies = if (cookie.isNotBlank()) cookie
      else runBlocking { GlobalCookieManager.load(getDomain(url)) }

      if (cookies?.isNotBlank() == true) connection.setRequestProperty("Cookie", cookies)
      connection.setRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36"
      )
      connection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
      connection.setRequestProperty("Priority", "u=0, i")
      connection.setRequestProperty("Referer", getDomain(url))
    }

    fun getConnection(url: URL, cookie: String = ""): HttpURLConnection {
      val connection = url.openConnection() as HttpURLConnection
      connection.requestMethod = "GET"
      connection.instanceFollowRedirects = true

      // ⏱️ Добавляем таймауты
      connection.connectTimeout = 10_000 // 10 секунд на подключение
      connection.readTimeout = 15_000    // 15 секунд на чтение

      updateAsBrowser(url, connection, cookie)
      connection.connect()

      if (connection.responseCode == 403) {
        val intent = Intent(App.context, CaptchaWebViewActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("captcha_url", url.toString())
        App.context.startActivity(intent)
      } else if (connection.responseCode != 200) {
        throw Exception("Error connection: ${connection.responseCode}")
      }

      return connection
    }
  }
}