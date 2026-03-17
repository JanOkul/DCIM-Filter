package com.example.dcimfilter.filtering.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class BatchFileMoverWorker(
    context: Context,
    params: WorkerParameters
): FileMoverWorker(context, params) {
    override suspend fun doWork(): Result {
        val files_to_move = dao.getCount() // For UI
        var files_moved = 0
        setProgress(workDataOf(
            "progress_message" to "Found $files_to_move files to filter",
            "progress_float" to 0.0f
        ))

        while (dao.peekFilterTarget() != null) {
            moveFile()
            files_moved++
            setProgress(workDataOf(
                "progress_message" to "$files_moved of $files_to_move files filtered",
                "progress_float" to files_moved.toFloat() / files_to_move.toFloat()
            ))
        }

        setProgress(workDataOf(
            "progress_message" to "Completed file filtering!",
            "progress_float" to 1.0f
        ))
        return Result.success()
    }
}