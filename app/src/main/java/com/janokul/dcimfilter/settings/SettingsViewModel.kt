package com.janokul.dcimfilter.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.janokul.dcimfilter.filtering.job.MediaJobScheduler
import com.janokul.dcimfilter.filtering.scanners.FileScannerService
import com.janokul.dcimfilter.ui.components.misc.AppSettings
import kotlinx.coroutines.launch

//todo fix all this
class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SettingsRepository(app.applicationContext)
    private val scheduler = MediaJobScheduler(app)
    val isEnabled = repo.isEnabled
    fun setIsEnabled(value: Boolean) = viewModelScope.launch {
        if (value) {
            scheduler.buildAndStartJob()
        } else {
            scheduler.stopJob()
        }
        repo.setIsEnabled(value) }
}