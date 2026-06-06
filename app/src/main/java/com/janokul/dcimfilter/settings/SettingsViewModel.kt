package com.janokul.dcimfilter.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.janokul.dcimfilter.processing.job.MediaJobScheduler
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