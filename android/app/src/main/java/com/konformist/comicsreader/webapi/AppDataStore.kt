package com.konformist.comicsreader.webapi

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class AppDataStore(private val dataStore: DataStore<Preferences>) {
  /** Авто перелистывание */
  private val autoReadingKey = booleanPreferencesKey("auto_reading")
  /** Таймер для авто перелистывания */
  private val autoReadingTimeoutKey = intPreferencesKey("auto_reading_timeout")
  /** Режим чтения */
  private val readingModeKey = stringPreferencesKey("reading_mode")
  /** Сжимать картинки при загрузке */
  private val isCompressKey = booleanPreferencesKey("is_compress")

  val readingModeList = listOf("vertical", "horizontal", "webtoon")

  val settings = Settings()

  suspend fun readStore(): Boolean {
    val storeSync = dataStore.data.first()

    settings.autoReading = storeSync[autoReadingKey] ?: false
    settings.autoReadingTimeout = storeSync[autoReadingTimeoutKey] ?: 20000
    settings.readingMode = storeSync[readingModeKey] ?: readingModeList[0]
    settings.isCompress = storeSync[isCompressKey] ?: true

    Log.i("DataStore read", settings.toString())

    return true
  }

  suspend fun saveStore(): Boolean {
    dataStore.edit { value ->
      run {
        value[autoReadingKey] = settings.autoReading
        value[autoReadingTimeoutKey] = settings.autoReadingTimeout
        value[readingModeKey] = settings.readingMode
        value[isCompressKey] = settings.isCompress
      }
    }

    return true
  }
}