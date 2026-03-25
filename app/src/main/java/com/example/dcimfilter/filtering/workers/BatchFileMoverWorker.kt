package com.example.dcimfilter.filtering.workers

import android.content.Context
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.dcimfilter.R
import kotlinx.coroutines.delay

private const val TAG = "BatchFileMoverWorker"

class BatchFileMoverWorker(
    private val context: Context,
    params: WorkerParameters
): FileMoverWorker(context, params) {
    override suspend fun doWork(): Result {
        val filesToMove = filterDao.getCount() // For UI

        setProgress(workDataOf(
            "message" to "Found $filesToMove files to filter",
            "files_to_move" to filesToMove,
        ))

        // Give illusion of work as it seems too quick on low file counts.
        if (filesToMove >= 0) {
            delay(500)
        }

        var filesMoved = 0

        if (filesToMove >= 20) {
            updateNotification(filesMoved, filesToMove)
        }

        Log.d(TAG, "Found $filesToMove files to filter")
        while (filterDao.peekFilterTarget() != null) {
            moveFile()
            filesMoved++
            updateNotification(filesMoved, filesToMove)
        }

        return Result.success()
    }

    private suspend fun updateNotification(current: Int, total: Int) {
        setForeground(
            ForegroundInfo(
                2,
                NotificationCompat.Builder(applicationContext,context.getString(R.string.notification_channel_id))
                    .setContentTitle("Filtering Files")
                    .setContentText("$current / $total")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setProgress(total, current, false)
                    .setOngoing(true)
                    .build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        )
    }
}