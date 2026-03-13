package com.example.dcimfilter.workers

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.FileObserver
import android.os.IBinder
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import com.example.dcimfilter.queue.FilterDB
import com.example.dcimfilter.queue.FilterTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

class FileScannerService : Service() {

    private val searchDir = File("/storage/emulated/0/DCIM/Camera")
    private var fileObserver: FileObserver? = null
    private val dao by lazy { FilterDB.getInstance(this).filterDao }
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fileObserver = FolderObserver(searchDir, this::enqueueFile)
        fileObserver?.startWatching()
        Log.d("FileScannerService", "Started")
        return START_STICKY
    }

    // todo Add Mediastore app checking and also make sure to make absolute path rather than relative
    private fun enqueueFile(path: String?) {
        if (path == null) {
            Log.d("FileScannerService", "Path is null")
            return
        }
        scope.launch {
            dao.insertFilterTarget(FilterTarget(file = path))
            Log.d("FileScannerService", "Enqueued: $path")
        }
    }

    override fun onDestroy() {
        fileObserver?.stopWatching()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}