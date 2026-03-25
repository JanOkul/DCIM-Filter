package com.example.dcimfilter.filtering.scanners

import android.os.Environment
import android.os.FileObserver
import android.util.Log
import java.io.File

private const val TAG = "DCIMObserver"

class DcimObserver(private val handler: (String?) -> Unit
): FileObserver(dcimFile , CLOSE_WRITE) {
    companion object {
        private val dcimFile = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ),
            "Camera"
        )
    }

    override fun onEvent(event: Int, path: String?) {
        Log.d(TAG, "Event: $event, Path: $path")
        if (event != CLOSE_WRITE) {
            Log.d(TAG, "Event not CLOSE_WRITE, ignoring")
            return
        }
        handler(path)
    }
}