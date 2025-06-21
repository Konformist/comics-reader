package com.konformist.comicsreader.db

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class GlobalCookieManager {
  companion object {
    lateinit var dataStore: DataStore<Preferences>

    // Метод для сохранения куков для конкретного домена
    suspend fun save(domain: String, cookies: String?) {
      val domainKey = stringPreferencesKey(domain)
      dataStore.edit { preferences ->
        preferences[domainKey] = cookies ?: ""
      }
    }

    // Метод для загрузки куков по домену
    suspend fun load(domain: String): String? {
      val domainKey = stringPreferencesKey(domain)
      val preferences = dataStore.data.first()
      return preferences[domainKey]
    }

    // Дополнительно: удаление куков по домену (если нужно)
    suspend fun remove(domain: String) {
      val domainKey = stringPreferencesKey(domain)
      dataStore.edit { preferences ->
        preferences.remove(domainKey)
      }
    }
  }
}