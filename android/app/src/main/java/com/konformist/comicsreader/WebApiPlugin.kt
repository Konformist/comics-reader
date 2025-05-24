package com.konformist.comicsreader

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