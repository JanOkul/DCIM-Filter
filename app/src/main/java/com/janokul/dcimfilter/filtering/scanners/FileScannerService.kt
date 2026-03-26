package com.janokul.dcimfilter.filtering.scanners


import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.database.Cursor
import android.os.Build
import android.os.Environment
import android.os.FileObserver
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.janokul.dcimfilter.NotificationIds
import com.janokul.dcimfilter.PREFS_DESTINATION_FOLDER
import com.janokul.dcimfilter.PREFS_SOURCE_PACKAGE
import com.janokul.dcimfilter.R
import com.janokul.dcimfilter.WORKER_ID
import com.janokul.dcimfilter.WORK_DATA_ID
import com.janokul.dcimfilter.filtering.workers.SingleFileMoverWorker
import com.janokul.dcimfilter.room.FilterDB
import com.janokul.dcimfilter.room.queue.FilterTarget
import com.janokul.dcimfilter.ui.components.misc.hasAllFileAccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private const val TAG = "FileScannerService"


class FileScannerService : Service() {
    private val dao by lazy { FilterDB.getInstance(this).filterDao }
    private val scope = CoroutineScope(Dispatchers.IO)
    private val relativePath = "${Environment.DIRECTORY_DCIM}/Camera/"
    private var fileObserver: FileObserver? = DcimObserver(this::enqueueFile)
    private lateinit var sourcePackage: String
    private lateinit var destinationFolder: String

    /**
     * Retrieves user settings, starts file observer and foreground service.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            // Already running, nothing to reinitialise
            return START_STICKY
        }

        // Get selected package setting, if null stop service.
        sourcePackage = intent.getStringExtra(PREFS_SOURCE_PACKAGE) ?: run {
            stopSelf()
            Log.d(TAG, "Source Package is null, stopping service")
            return START_NOT_STICKY
        }

        // Get destination folder setting, if null stop service.
        destinationFolder = intent.getStringExtra(PREFS_DESTINATION_FOLDER) ?: run {
            stopSelf()
            Log.d(TAG, "Destination Folder is null, stopping service")
            return START_NOT_STICKY
        }

        Log.d(TAG, "Source Package: $sourcePackage")
        Log.d(TAG, "Destination Folder: $destinationFolder")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NotificationIds.FOREGROUND_SERVICE.id,
                buildNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(
                NotificationIds.FOREGROUND_SERVICE.id,
                buildNotification()
            )
        }

        Log.d(TAG, "Started foreground service")

        fileObserver?.startWatching()
        Log.d(TAG, "Started file observer")
        return START_STICKY
    }

    override fun onDestroy() {
        fileObserver?.stopWatching()
        scope.cancel()

        Log.d(TAG, "Stopped file observer")
        Log.d(TAG, "Stopping foreground service")
        super.onDestroy()
    }

    /**
     *  Enqueue a single file to the Room for processing
     */
    private fun enqueueFile(name: String?) {
        if (name == null) return

        // If file access is turned off while service is running, errors will occur.
        if (!hasAllFileAccess()) {
            Log.d(TAG, "No storage permission, stopping service")
            stopSelf()
            return
        }

        val fileInfo = getFileInfo(name) ?: return
        // Check if file owner is source app.
        if (fileInfo.owner != sourcePackage) {
            Log.d(TAG, "The owner of $name is not the source app, owner: $fileInfo")
            return
        }

        // Insert into Room queue.
        scope.launch {
            dao.insertFilterTarget(
                FilterTarget(
                    name = name,
                    uriId = fileInfo.id,
                    mimeType = fileInfo.mimeType,
                    destinationFolder = destinationFolder
                )
            )
            Log.d(TAG, "Enqueued: $name")
            createWork(fileInfo.id)
        }
    }

    /**
     *  Retrieves information from MediaStore about the file created and observed by DCIMObserver.
     *  @param displayName Name of the file observed by DCIMObserver.
     */
    private fun getFileInfo(displayName: String): QueryResult? {
        val projection = arrayOf(
            MediaStore.MediaColumns.OWNER_PACKAGE_NAME,
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DISPLAY_NAME
        )

        // No need to query owner, as null check is still required and also allows for an explicit
        // check that the file is not owned by selected package.
        val selection =
            "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
        val selectionArgs = arrayOf(displayName, relativePath)

        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            null
        )

        Log.d(TAG, "Cursor length: ${cursor?.count}")
        return extractResult(cursor)
    }

    /**
     * Extracts the projection into a QueryResults object from a MediaStore query.
     */
    private fun extractResult(cursor: Cursor?): QueryResult? {
        cursor?.use {
            val ownerIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.OWNER_PACKAGE_NAME)
            val idIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val mimetypeIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val nameIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)

            if (it.moveToFirst()) {
                val result = QueryResult(
                    it.getString(ownerIndex),
                    it.getLong(idIndex),
                    it.getString(mimetypeIndex),
                    it.getString(nameIndex)
                )

                Log.d(TAG, "Owner length: ${it.getString(ownerIndex).length}")
                Log.d(TAG, "Query result: $result")
                return result
            }
        }

        return null
    }

    /**
     *  Builds notification so that the service is considered a foreground service.
     */
    private fun buildNotification(): Notification {
        val channelId = "DCIM_FILTER_CHANNEL"
        val title = getString(R.string.foreground_notification_title)
        val text = getString(R.string.foreground_notification_text)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    /**
     * Creates a work request to process a single file in the Room queue.
     */
    private fun createWork(id: Long) {
        val manager = WorkManager.getInstance(applicationContext)
        val work = OneTimeWorkRequestBuilder<SingleFileMoverWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .setInputData(workDataOf(WORK_DATA_ID to id))
            .build()

        manager.enqueueUniqueWork(
            WORKER_ID,
            ExistingWorkPolicy.KEEP,
            work
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null
}