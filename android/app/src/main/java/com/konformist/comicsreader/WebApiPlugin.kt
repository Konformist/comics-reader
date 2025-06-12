package com.konformist.comicsreader

import android.content.Intent
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

@CapacitorPlugin(name = "WebApi")
class WebApiPlugin : Plugin() {
  private lateinit var webApi: WebApi
  private var queryPath: String = ""
  private var methodCall: PluginCall? = null

  // Инициализация ActivityResultLauncher для получения результата
  private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

  @OptIn(DelicateCoroutinesApi::class)
  override fun load() {
    super.load()
    webApi = WebApi()

    // @TODO Что-то с ней сделать надо
    activityResultLauncher = activity.registerForActivityResult(
      ActivityResultContracts.StartActivityForResult()
    ) { value ->
      GlobalScope.launch(Dispatchers.IO) {
        val uri = if (value.resultCode == AppCompatActivity.RESULT_OK) value.data?.data else null
        val data = methodCall?.data?.optJSONObject("body")?.put("uri", uri)
        val result = if (uri != null && data != null) webApi.api(queryPath, data)
        else webApi.wrappedToResult(false)

        withContext(Dispatchers.Main) {
          methodCall?.resolve(JSObject(result))
          methodCall = null
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

    when (path) {
      Query.BACKUP_BACKUP_RESTORE -> {
        queryPath = path
        methodCall = call
        pickFile("application/x-tar")
      }

      Query.COMIC_ARCHIVE_ADD -> {
        queryPath = path
        methodCall = call
        pickFile("application/*")
      }

      Query.FILE_COMIC_COVER_ADD -> {
        queryPath = path
        methodCall = call
        pickFile("image/*")
      }

      Query.FILE_CHAPTER_PAGE_ADD -> {
        queryPath = path
        methodCall = call
        pickFile("image/*")
      }

      else -> {
        call.resolve(JSObject(webApi.api(path, body)))
      }
    }
  }
}