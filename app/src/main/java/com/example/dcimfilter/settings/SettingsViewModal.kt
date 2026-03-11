package com.example.dcimfilter.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SettingsRepository(app.applicationContext)

    val isEnabled = repo.isEnabled
    val selectedPackage = repo.selectedPackage
    val destinationFolder = repo.destinationFolder

    fun setIsEnabled(value: Boolean) = viewModelScope.launch { repo.setIsEnabled(value) }
    fun setSelectedPackage(value: String) = viewModelScope.launch { repo.setSelectedPackage(value) }
    fun setDestinationFolder(value: String) = viewModelScope.launch { repo.setDestinationFolder(value) }
}