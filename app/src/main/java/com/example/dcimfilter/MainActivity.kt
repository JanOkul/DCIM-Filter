package com.example.dcimfilter

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.AlertDialog
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dcimfilter.background_processing.services.FileScannerService
import com.example.dcimfilter.features.main.MainScreen
import com.example.dcimfilter.features.package_select.PackageSelectScreen
import com.example.dcimfilter.ui.theme.DCIMFilterTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : ComponentActivity () {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { (perm, granted) ->
            Log.d("MediaStoreTest", "$perm granted: $granted")
        }
    }
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE
            )
        )

        createNotificationChannel()

        enableEdgeToEdge()
        setContent {
            DCIMFilterTheme {



                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") { MainScreen(navController) }
                    composable("package_select" ){ PackageSelectScreen(navController) }
                }
            }
        }
    }

    // todo Make foreground start on bootup
}


