package com.example.dcimfilter.filtering.workers

import android.content.Context
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.dcimfilter.NOTIFICATION_CHANNEL
import com.example.dcimfilter.NotificationIds
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
        delay(500)

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
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setContentTitle("Filtering Files")
            .setContentText("$current / $total")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(total, current, false)
            .setOngoing(true)

        setForeground(
            ForegroundInfo(
                NotificationIds.BATCH_SCANNER.id,
                notification.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        )
    }
}