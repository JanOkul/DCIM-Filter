package com.example.dcimfilter.workers


import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.os.Environment
import android.os.FileObserver
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import com.example.dcimfilter.R
import com.example.dcimfilter.dataStore
import com.example.dcimfilter.queue.FilterDB
import com.example.dcimfilter.queue.FilterTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

const val TAG = "FileScannerService"
class FileScannerService : Service() {
    private var selectedPackage: String? = "com.ss.android.ugc.trill"
    private val dcimPath = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        "Camera"
    )
    private var fileObserver: FileObserver? = null
    private val dao by lazy { FilterDB.getInstance(this).filterDao }
    private val scope = CoroutineScope(Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())
    private val debounceMap = mutableMapOf<String, Runnable>()

    init {
        Log.d(TAG, "DCIM  ${dcimPath}")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channelId = getString(R.string.notification_channel_id)
        val title = getString(R.string.foreground_notification_title)
        val text = getString(R.string.foreground_notification_text)

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, builder)
        Log.d(TAG, "Started foreground service")

        fileObserver = DcimObserver(dcimPath, this::enqueueFile)
        fileObserver?.startWatching()
        Log.d(TAG, "Started file observer")



        return START_STICKY
    }


    // todo Add Mediastore app checking and also make sure to make absolute path rather than relative
    private fun enqueueFile(path: String?) {
        if (path == null) return

        if (debounceMap.containsKey(path)) {
            Log.d(TAG, "Ignoring duplicate event for: $path")
            return
        }

        // Check if file owner is source app.
        if (getOwner(path) != selectedPackage) {
            Log.d(TAG, "Package owner is not selected package")
            return
        }

        val cleanupTask = Runnable {
            debounceMap.remove(path)
            Log.d(TAG, "$path expired")
        }

        scope.launch {
            dao.insertFilterTarget(FilterTarget(file = path))
            Log.d(TAG, "Enqueued: $path")
        }

        debounceMap[path] = cleanupTask
        handler.postDelayed(cleanupTask, 500)
    }

    private fun getOwner(displayName: String): String? {
        val path = "DCIM/Camera/"

        val projection = arrayOf(
            MediaStore.MediaColumns.OWNER_PACKAGE_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.RELATIVE_PATH
        )

        val selection =
            "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?"

        val selectionArgs = arrayOf(displayName, path)



        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            null
        )

        Log.d(TAG, "Cursor length: ${cursor?.count}")
        cursor?.use {
            val ownerIndex =
                it.getColumnIndexOrThrow(MediaStore.MediaColumns.OWNER_PACKAGE_NAME)

            while (it.moveToNext()) {
                val owner = it.getString(ownerIndex)
                Log.d(TAG, "Owner=$owner")
                return owner
            }
        }

        return null
    }

    override fun onDestroy() {
        fileObserver?.stopWatching()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}