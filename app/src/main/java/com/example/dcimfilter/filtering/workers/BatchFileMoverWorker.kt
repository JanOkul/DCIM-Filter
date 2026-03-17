package com.example.dcimfilter.filtering.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class BatchFileMoverWorker(
    context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}