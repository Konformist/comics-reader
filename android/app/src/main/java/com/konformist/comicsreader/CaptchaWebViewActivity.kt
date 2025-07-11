package com.konformist.comicsreader

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.konformist.comicsreader.db.GlobalCookieManager
import com.konformist.comicsreader.utils.RequestUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaptchaWebViewActivity : AppCompatActivity() {
  private lateinit var webView: WebView

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    webView = WebView(this)
    setContentView(webView)

    val captchaUrl = intent.getStringExtra("captcha_url") ?: return finish()

    val settings = webView.settings
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.loadsImagesAutomatically = true
    settings.userAgentString =
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36"

    val cookieManager = CookieManager.getInstance()
    cookieManager.setAcceptCookie(true)
    cookieManager.setAcceptThirdPartyCookies(webView, true)

    webView.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url.toString()
        Log.d("Captcha", "Navigating to: $url")
        return false // Разрешаем WebView загружать URL
      }

      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        if (url != null) {
          val cookies = cookieManager.getCookie(url)
          Log.d("Captcha", "Finished loading: $url")
          Log.d("Captcha", "Cookies: $cookies")

          if (isCaptchaPassed(url) && !cookies.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
              GlobalCookieManager.save(RequestUtils.getDomain(url), cookies)
              Log.d("Captcha", "Cookies saved for ${RequestUtils.getDomain(url)}")
            }
            finish()
          }
        }
      }
    }

    webView.loadUrl(captchaUrl)
  }

  /**
   * Проверка, что мы прошли капчу.
   * Можно адаптировать под конкретные сайты (убрать challenge или captcha в URL).
   */
  private fun isCaptchaPassed(url: String): Boolean {
    return !url.contains("captcha", ignoreCase = true) &&
        !url.contains("challenge", ignoreCase = true) &&
        !url.contains("cloudflare", ignoreCase = true)
  }
}
