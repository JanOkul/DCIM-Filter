package com.example.dcimfilter.background_processing.services

import android.os.FileObserver
import android.util.Log
import java.io.File

class DcimObserver(dcimDir: File, private val handler: (String?) -> Unit
): FileObserver(dcimDir, CLOSE_WRITE) {

    override fun onEvent(event: Int, path: String?) {
        Log.d("FileScannerService", "Event: $event, Path: $path")
        if (event != CLOSE_WRITE) return
        handler(path)
    }
}