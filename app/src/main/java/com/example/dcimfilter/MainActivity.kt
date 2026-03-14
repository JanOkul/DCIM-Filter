package com.example.dcimfilter

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.dcimfilter.features.main.MainScreen
import com.example.dcimfilter.ui.theme.DCIMFilterTheme
import com.example.dcimfilter.workers.FileScannerService

class MainActivity : ComponentActivity() {
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

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC
            )
        )

        createNotificationChannel()
        startForegroundService(Intent(this, FileScannerService::class.java))

        enableEdgeToEdge()
        setContent {
            DCIMFilterTheme {
                MainScreen()
            }
        }
    }

    // todo Make foreground start on bootup
}


