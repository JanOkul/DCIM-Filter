package com.example.dcimfilter.filtering.workers

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "SingleFileMoverWorker"


class SingleFileMoverWorker(context: Context, params: WorkerParameters): FileMoverWorker(context, params) {

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            moveFile()
        }
        return Result.success()
    }
}

