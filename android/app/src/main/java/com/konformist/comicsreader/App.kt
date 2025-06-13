package com.konformist.comicsreader

import android.app.Application
import android.content.Context

class App : Application() {
  override fun onCreate() {
    super.onCreate()
    instance = this
  }

  companion object {
    private lateinit var instance: App
    val appName: String get() = instance.applicationContext.getString(R.string.app_name)
    val context: Context get() = instance.applicationContext
  }
}