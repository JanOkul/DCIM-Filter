package com.example.dcimfilter.filtering.workers

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import androidx.work.workDataOf

private const val TAG = "BatchFileMoverWorker"

class BatchFileMoverWorker(
    context: Context,
    params: WorkerParameters
): FileMoverWorker(context, params) {
    override suspend fun doWork(): Result {
        val filesToMove = dao.getCount() // For UI
        var filesMoved = 0
        setProgress(workDataOf(
            "progress_message" to "Found $filesToMove files to filter",
            "progress_float" to 0.0f
        ))

        Log.d(TAG, "Found $filesToMove files to filter")

        while (dao.peekFilterTarget() != null) {
            moveFile()
            filesMoved++
            setProgress(workDataOf(
                "progress_message" to "$filesMoved of $filesToMove files filtered",
                "progress_float" to filesMoved.toFloat() / filesToMove.toFloat()
            ))
        }

        setProgress(workDataOf(
            "progress_message" to "Completed file filtering!",
            "progress_float" to 1.0f
        ))
        return Result.success()
    }
}