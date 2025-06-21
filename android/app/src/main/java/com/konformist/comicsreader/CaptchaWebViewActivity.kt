package com.konformist.comicsreader

import android.annotation.SuppressLint
import android.os.Bundle
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
    settings.userAgentString =
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36"

    val cookieManager = CookieManager.getInstance()
    cookieManager.setAcceptCookie(true)
    cookieManager.setAcceptThirdPartyCookies(webView, true)

    webView.webViewClient = object : WebViewClient() {
      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (url != null && isCaptchaPassed(url)) {
          val cookies = cookieManager.getCookie(url)

          CoroutineScope(Dispatchers.IO).launch {
            GlobalCookieManager.save(RequestUtils.getDomain(url), cookies)
          }
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