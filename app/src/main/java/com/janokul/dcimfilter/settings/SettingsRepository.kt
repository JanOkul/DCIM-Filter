package com.janokul.dcimfilter.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val context: Context) {

    val isEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.IS_ENABLED] ?: false }

    val sourcePackage: Flow<String> = context.dataStore.data
        .map { it[PreferencesKeys.SOURCE_PACKAGE] ?: "" }

    val destinationFolder: Flow<String> = context.dataStore.data
        .map { it[PreferencesKeys.DESTINATION_FOLDER] ?: "" }

    val timeoutNotification: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.TIMEOUT_NOTIFICATION] ?: false }

    suspend fun setIsEnabled(value: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_ENABLED] = value }
    }

    suspend fun setSourcePackage(value: String) {
        context.dataStore.edit { it[PreferencesKeys.SOURCE_PACKAGE] = value }
    }

    suspend fun setDestinationFolder(value: String) {
        context.dataStore.edit { it[PreferencesKeys.DESTINATION_FOLDER] = value }
    }

    suspend fun setTimeoutNotification(value: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.TIMEOUT_NOTIFICATION] = value }
    }
}