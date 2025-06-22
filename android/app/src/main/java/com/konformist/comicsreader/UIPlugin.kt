package com.konformist.comicsreader

import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@CapacitorPlugin(name = "UI")
class UIPlugin : Plugin() {
  @PluginMethod
  fun toast(call: PluginCall) {
    val text = call.getString("text")

    Handler(Looper.getMainLooper()).post(
      Runnable {
        Toast.makeText(context, text, Toast.LENGTH_LONG)
          .show()
        call.resolve()
      }
    )
  }

  @PluginMethod
  fun loading(call: PluginCall) {
    if (call.getBoolean("show") == true) {
      activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
      activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
  }

  @PluginMethod
  fun reading(call: PluginCall) {
    activity.lifecycleScope.launch(Dispatchers.Main) {
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
