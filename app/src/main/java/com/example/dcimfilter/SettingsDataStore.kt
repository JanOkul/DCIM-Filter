package com.example.dcimfilter

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val SELECTED_PACKAGE = stringPreferencesKey("selected_package")
}