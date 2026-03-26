package com.example.dcimfilter.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SettingsRepository(app.applicationContext)

    val isEnabled = repo.isEnabled
    val sourcePackage = repo.sourcePackage
    val destinationFolder = repo.destinationFolder

    fun setIsEnabled(value: Boolean) = viewModelScope.launch { repo.setIsEnabled(value) }
    fun setSourcePackage(value: String) = viewModelScope.launch { repo.setSourcePackage(value) }
    fun setDestinationFolder(value: String) =
        viewModelScope.launch { repo.setDestinationFolder(value) }
}