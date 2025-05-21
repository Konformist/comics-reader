package com.konformist.comicsreader

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.konformist.comicsreader.webapi.WebApi
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class WebApiPluginTest {
  private val webApi: WebApi = WebApi(ApplicationProvider.getApplicationContext())

  @Test
  @Throws(Exception::class)
  fun addTag() {
    val tag =
      JSONObject("{\"id\":100,\"cdate\":${Date().time},\"mdate\":${Date().time},\"name\":\"New tag\"}")
    val rowId = webApi.addTag(tag)

    Assert.assertEquals("{\"id\":1}", rowId)
  }

  @Test
  @Throws(Exception::class)
  fun setTag() {
    val tag =
      JSONObject("{\"id\":1,\"cdate\":${Date().time},\"mdate\":${Date().time},\"name\":\"New tag 2\"}")
    webApi.setTag(tag)

    val tagJson = webApi.getTag(1.toLong())

    Assert.assertEquals("{\"id\":1,\"name\":\"New tag 2\"}", tagJson)
  }
}
