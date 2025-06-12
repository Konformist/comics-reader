package com.konformist.comicsreader.db

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class AppDataStore {
  companion object {
    lateinit var dataStore: DataStore<Preferences>

    /** Авто перелистывание */
    private val AUTO_READING_KEY = booleanPreferencesKey("auto_reading")

    /** Таймер для авто перелистывания */
    private val AUTO_READING_TIMEOUT_KEY = intPreferencesKey("auto_reading_timeout")

    /** Режим чтения */
    private val READING_MODE_KEY = stringPreferencesKey("reading_mode")

    /** Сжимать картинки при загрузке */
    private val IS_COMPRESS_KEY = booleanPreferencesKey("is_compress")

    val readingModeList = listOf("vertical", "horizontal", "webtoon")

    var settings = Settings()

    suspend fun readStore(): Boolean {
      val storeSync = dataStore.data.first()

      settings.autoReading = storeSync[AUTO_READING_KEY] == true
      settings.autoReadingTimeout = storeSync[AUTO_READING_TIMEOUT_KEY] ?: 20000
      settings.readingMode = storeSync[READING_MODE_KEY] ?: readingModeList[0]
      settings.isCompress = storeSync[IS_COMPRESS_KEY] != false

      return true
    }

    suspend fun saveStore(): Boolean {
      dataStore.edit { value ->
        run {
          value[AUTO_READING_KEY] = settings.autoReading
          value[AUTO_READING_TIMEOUT_KEY] = settings.autoReadingTimeout
          value[READING_MODE_KEY] = settings.readingMode
          value[IS_COMPRESS_KEY] = settings.isCompress
        }
      }

      return true
    }
  }
}