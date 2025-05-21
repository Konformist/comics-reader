package com.konformist.comicsreader

import android.os.Bundle
import com.getcapacitor.BridgeActivity

class MainActivity : BridgeActivity() {
  public override fun onCreate(savedInstanceState: Bundle?) {
    registerPlugin(WebApiPlugin::class.java)
    registerPlugin(ImageFuck::class.java)
    super.onCreate(savedInstanceState)
  }
}
