package com.konformist.comicsreader

import android.content.Context
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.getcapacitor.BridgeActivity
import com.konformist.comicsreader.webapi.AppDataStore
import kotlinx.coroutines.runBlocking

class MainActivity : BridgeActivity() {
  private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

  public override fun onCreate(savedInstanceState: Bundle?) {
    AppDataStore.dataStore = dataStore
    runBlocking { AppDataStore.readStore() }
    registerPlugin(WebApiPlugin::class.java)
    super.onCreate(savedInstanceState)
  }
}
