package com.konformist.comicsreader

import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin

@CapacitorPlugin(name = "UI")
class UIPlugin : Plugin() {

  private val mainHandler = Handler(Looper.getMainLooper())

  @PluginMethod
  fun toast(call: PluginCall) {
    val text = call.getString("text")

    mainHandler.post {
      Toast.makeText(context, text, Toast.LENGTH_LONG).show()
      call.resolve()
    }
  }

  @PluginMethod
  fun loading(call: PluginCall) {
    mainHandler.post {
      if (call.getBoolean("show") == true) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      } else {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      }
      call.resolve()
    }
  }

  @PluginMethod
  fun reading(call: PluginCall) {
    mainHandler.post {
      val windowInsetsController = activity.window.decorView.windowInsetsController

      if (call.getString("mode") == "start") {
        windowInsetsController?.show(WindowInsets.Type.statusBars())
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      } else {
        windowInsetsController?.hide(WindowInsets.Type.statusBars())
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      }

      call.resolve()
    }
  }
}
