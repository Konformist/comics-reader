package com.konformist.comicsreader

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

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
    settings.userAgentString = WebSettings.getDefaultUserAgent(this)

    val cookieManager = CookieManager.getInstance()
    cookieManager.setAcceptCookie(true)
    cookieManager.setAcceptThirdPartyCookies(webView, true)

    webView.webViewClient = object : WebViewClient() {
      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (url != null && isCaptchaPassed(url)) {
          val cookies = cookieManager.getCookie(url)
          val resultIntent = Intent()
          resultIntent.putExtra("cookie", cookies)
          setResult(RESULT_OK, resultIntent)
          finish()
        }
      }

      // Опционально: блокируем редиректы на сторонние сайты
      override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return false
      }
    }

    webView.loadUrl(captchaUrl)
  }

  private fun isCaptchaPassed(url: String): Boolean {
    // Здесь можно точнее подстраиваться под реальный сайт
    // return url.contains("example.com") && !url.contains("captcha")
    return true
  }
}