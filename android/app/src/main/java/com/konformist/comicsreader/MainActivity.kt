package com.konformist.comicsreader

import android.content.Context
import android.os.Bundle
import android.webkit.CookieManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.getcapacitor.BridgeActivity
import com.konformist.comicsreader.db.AppDataStore
import com.konformist.comicsreader.db.GlobalCookieManager
import kotlinx.coroutines.runBlocking

class MainActivity : BridgeActivity() {
  private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
  private val Context.cookieStore: DataStore<Preferences> by preferencesDataStore(name = "cookie_data_store")

  public override fun onCreate(savedInstanceState: Bundle?) {
    AppDataStore.dataStore = settingsStore
    GlobalCookieManager.dataStore = cookieStore
    runBlocking { AppDataStore.readStore() }
    registerPlugin(WebApiPlugin::class.java)
    super.onCreate(savedInstanceState)

    // Настроим cookies
    val webView = getBridge().webView
    val cookieManager = CookieManager.getInstance()
    cookieManager.setAcceptThirdPartyCookies(webView, true) // Разрешаем сторонние cookies
  }
}
