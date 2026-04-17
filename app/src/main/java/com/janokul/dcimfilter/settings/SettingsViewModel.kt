package com.janokul.dcimfilter.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.janokul.dcimfilter.filtering.scanners.FileScannerService
import com.janokul.dcimfilter.filtering.Job.MediaJobScheduler
import com.janokul.dcimfilter.ui.components.misc.AppSettings
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SettingsRepository(app.applicationContext)

    val isEnabled = repo.isEnabled
    val sourcePackage = repo.sourcePackage
    val destinationFolder = repo.destinationFolder
    val timeoutNotification = repo.timeoutNotification

    fun setIsEnabled(value: Boolean) = viewModelScope.launch { repo.setIsEnabled(value) }
    fun setSourcePackage(value: String) = viewModelScope.launch { repo.setSourcePackage(value) }
    fun setDestinationFolder(value: String) =
        viewModelScope.launch { repo.setDestinationFolder(value) }

    fun setTimeoutNotification(value: Boolean) = viewModelScope.launch { repo.setTimeoutNotification(value) }

//    fun updateServiceState(context: Context, settings: AppSettings) {
//        setIsEnabled(settings.isEnabled)
//
//        val intent = Intent(context, FileScannerService::class.java)
//
//        if (settings.isEnabled) {
//            context.startForegroundService(intent
//                .putExtra(PREFS_SOURCE_PACKAGE, settings.sourcePackage)
//                .putExtra(PREFS_DESTINATION_FOLDER, settings.destinationFolder)
//                .putExtra(PREFS_TIMEOUT_NOTIFICATION, settings.timeoutNotification)
//            )
//        } else {
//            context.stopService(intent)
//        }
//    }

        fun updateServiceState(context: Context, settings: AppSettings) {
        setIsEnabled(settings.isEnabled)

        val intent = Intent(context, FileScannerService::class.java)

        if (settings.isEnabled) {
            MediaJobScheduler(context).buildAndStartJob()
        } else {
            MediaJobScheduler(context).stopJob()
        }
    }
}