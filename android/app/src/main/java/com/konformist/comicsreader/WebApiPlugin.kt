package com.konformist.comicsreader

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.konformist.comicsreader.webapi.Query
import com.konformist.comicsreader.webapi.WebApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

@CapacitorPlugin(name = "WebApi")
class WebApiPlugin : Plugin() {
  private lateinit var webApi: WebApi
  private var queryPath: String = ""

  // Инициализация ActivityResultLauncher для получения результата
  private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

  @OptIn(DelicateCoroutinesApi::class)
  override fun load() {
    super.load()
    webApi = WebApi(context)

    // @TODO Что-то с ней сделать надо
    activityResultLauncher = activity.registerForActivityResult(
      ActivityResultContracts.StartActivityForResult()
    ) { value ->
      GlobalScope.launch(Dispatchers.IO) {
        val uri = if (value.resultCode == AppCompatActivity.RESULT_OK) value.data?.data else null
        val data = JSONObject().put("uri", uri)
        val result = webApi.api(queryPath, data)

        withContext(Dispatchers.Main) {
          notifyListeners("filePick", JSObject.fromJSONObject(result))
        }
      }
    }
  }

  private fun pickFile(type: String) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.type = type
    activityResultLauncher.launch(intent)
  }

  @PluginMethod
  fun api(call: PluginCall) {
    val path = call.data.optString("path", "")
    val body = call.data.optJSONObject("body")

    if (path == Query.BACKUP_BACKUP_RESTORE) {
      queryPath = path
      pickFile("application/x-tar")
      val result = JSONObject().apply { put("result", true) }
      call.resolve(JSObject.fromJSONObject(result))
    } else {
      call.resolve(JSObject.fromJSONObject(webApi.api(path, body)))
    }
  }
}