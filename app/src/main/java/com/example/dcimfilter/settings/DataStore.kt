package com.example.dcimfilter.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "settings")

object PreferencesKeys {
    val IS_ENABLED = booleanPreferencesKey("is_enabled")
    val SELECTED_PACKAGE = stringPreferencesKey("selected_package")
    val DESTINATION_FOLDER = stringPreferencesKey("destination_folder")
}


