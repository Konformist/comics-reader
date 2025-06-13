package com.konformist.comicsreader

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.konformist.comicsreader.webapi.Query
import com.konformist.comicsreader.webapi.WebApi
import kotlinx.coroutines.launch

@CapacitorPlugin(name = "WebApi")
class WebApiPlugin : Plugin() {
  private lateinit var webApi: WebApi

  private var queryPath: String = ""
  private var methodCall: PluginCall? = null
  private lateinit var activityResultLauncher: ActivityResultLauncher<Array<String>>

  private fun getLauncher(): ActivityResultLauncher<Array<String>> {
    return activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { value ->
      val call = methodCall ?: return@registerForActivityResult

      if (value == null) {
        call.reject("Файл не выбран")
        return@registerForActivityResult
      }

      activity.lifecycleScope.launch {
        val data = call.data.optJSONObject("body")?.put("uri", value.toString())
        call.resolve(JSObject(webApi.api(queryPath, data)))
      }
    }
  }

  override fun load() {
    super.load()
    webApi = WebApi()
    activityResultLauncher = getLauncher()
  }

  private fun pickFile(path: String, call: PluginCall, types: Array<String>) {
    queryPath = path
    methodCall = call
    activityResultLauncher.launch(types)
  }

  @PluginMethod
  fun api(call: PluginCall) {
    val path = call.data.optString("path", "")
    val body = call.data.optJSONObject("body")

    when (path) {
      Query.BACKUP_BACKUP_RESTORE -> pickFile(path, call, arrayOf("application/x-tar"))
      Query.COMIC_ARCHIVE_ADD -> pickFile(path, call, arrayOf("application/*"))
      Query.FILE_COMIC_COVER_ADD,
      Query.FILE_CHAPTER_PAGE_ADD -> pickFile(path, call, arrayOf("image/*"))

      else -> {
        activity.lifecycleScope.launch {
          call.resolve(JSObject(webApi.api(path, body)))
        }
      }
    }
  }
}