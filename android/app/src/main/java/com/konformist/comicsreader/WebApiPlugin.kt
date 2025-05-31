package com.konformist.comicsreader

import android.Manifest
import android.content.pm.PackageManager
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.konformist.comicsreader.webapi.WebApi

@CapacitorPlugin(name = "WebApi")
class WebApiPlugin : Plugin() {
  private lateinit var webApi: WebApi

  override fun load() {
    super.load()
    if (context.checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      activity.requestPermissions(arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED)
    }
    webApi = WebApi(context)
  }

  @PluginMethod
  fun api(call: PluginCall) {
    call.resolve(
      JSObject.fromJSONObject(
        webApi.api(
          call.data.optString("path", ""),
          call.data.optJSONObject("body"),
        )
      )
    )
  }
}