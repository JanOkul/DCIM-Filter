package com.example.dcimfilter.background_processing.services


import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.FileObserver
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dcimfilter.R
import com.example.dcimfilter.background_processing.workers.SingleFileMoverWorker
import com.example.dcimfilter.queue.FilterDB
import com.example.dcimfilter.queue.FilterTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

const val TAG = "FileScannerService"

private data class QueryResult(val owner: String, val id: Long, val mimeType: String)

class FileScannerService : Service() {
    private var selectedPackage: String? = "com.ss.android.ugc.trill"
    private val dcimPath = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        "Camera"
    )
    private var fileObserver: FileObserver? = null
    private val dao by lazy { FilterDB.getInstance(this).filterDao }
    private val scope = CoroutineScope(Dispatchers.IO)
    private val path = "DCIM/Camera/"

    init {
        Log.d(TAG, "DCIM  ${dcimPath}")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channelId = getString(R.string.notification_channel_id)
        val title = getString(R.string.foreground_notification_title)
        val text = getString(R.string.foreground_notification_text)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)
        Log.d(TAG, "Started foreground service")

        fileObserver = DcimObserver(dcimPath, this::enqueueFile)
        fileObserver?.startWatching()
        Log.d(TAG, "Started file observer")


        return START_STICKY
    }


    // todo Add Mediastore app checking and also make sure to make absolute path rather than relative
    private fun enqueueFile(path: String?) {
        if (path == null) return

        //todo add check of storage permission, if no permission then stop service

        val result = getFileInfo(path)
        val owner = result?.owner ?: return
        val id = result.id
        val mimeType = result.mimeType

        // Check if file owner is source app.
        if (owner != selectedPackage) {
            Log.d(TAG, "The owner of $path is not the source app, owner: $result")
            return
        }

        val entry = FilterTarget(
            uriId = id,
            mimeType = mimeType,
            name = path
        )

        scope.launch {
            dao.insertFilterTarget(entry)
            Log.d(TAG, "Enqueued: $path")
        }



        val manager = WorkManager.getInstance(applicationContext)
        val work = OneTimeWorkRequestBuilder<SingleFileMoverWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()

        manager.enqueue(work)
    }

    private fun getFileInfo(displayName: String): QueryResult? {


        val projection = arrayOf(
            MediaStore.MediaColumns.OWNER_PACKAGE_NAME,
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.MIME_TYPE
        )
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
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
            val ownerIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.OWNER_PACKAGE_NAME)
            val idIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val mimetypeIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)

            if (it.moveToFirst()) {
                val result = QueryResult(
                    it.getString(ownerIndex),
                    it.getLong(idIndex),
                    it.getString(mimetypeIndex)
                )

                Log.d(TAG, "Query result: $result")
                return result
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