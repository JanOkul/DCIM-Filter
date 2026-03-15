package com.example.dcimfilter.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val context: Context) {

    val isEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.IS_ENABLED] ?: false }

    val selectedPackage: Flow<String> = context.dataStore.data
        .map { it[PreferencesKeys.SELECTED_PACKAGE] ?: "" }

    val destinationFolder: Flow<String> = context.dataStore.data
        .map { it[PreferencesKeys.DESTINATION_FOLDER] ?: "" }

    suspend fun setIsEnabled(value: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_ENABLED] = value }
    }

    suspend fun setSelectedPackage(value: String) {
        context.dataStore.edit { it[PreferencesKeys.SELECTED_PACKAGE] = value }
    }

    suspend fun setDestinationFolder(value: String) {
        context.dataStore.edit { it[PreferencesKeys.DESTINATION_FOLDER] = value }
    }
}