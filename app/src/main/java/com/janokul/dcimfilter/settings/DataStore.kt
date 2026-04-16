package com.janokul.dcimfilter.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.janokul.dcimfilter.PREFS_DESTINATION_FOLDER
import com.janokul.dcimfilter.PREFS_IS_ENABLED
import com.janokul.dcimfilter.PREFS_SOURCE_PACKAGE
import com.janokul.dcimfilter.PREFS_TIMEOUT_NOTIFICATION
import com.janokul.dcimfilter.USER_PREFS_NAME

val Context.dataStore by preferencesDataStore(name = USER_PREFS_NAME)

object PreferencesKeys {
    val IS_ENABLED = booleanPreferencesKey(PREFS_IS_ENABLED)
    val SOURCE_PACKAGE = stringPreferencesKey(PREFS_SOURCE_PACKAGE)
    val DESTINATION_FOLDER = stringPreferencesKey(PREFS_DESTINATION_FOLDER)
    val TIMEOUT_NOTIFICATION = booleanPreferencesKey(PREFS_TIMEOUT_NOTIFICATION)
}


