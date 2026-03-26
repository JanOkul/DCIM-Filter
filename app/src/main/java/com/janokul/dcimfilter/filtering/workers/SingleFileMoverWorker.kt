package com.janokul.dcimfilter.filtering.workers

import android.content.Context
import androidx.work.WorkerParameters
import com.janokul.dcimfilter.WORK_DATA_ID
import com.janokul.dcimfilter.room.queue.FilterTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "SingleFileMoverWorker"

class SingleFileMoverWorker(context: Context, private val params: WorkerParameters) :
    FileMoverWorker(context, params, TAG) {

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            delay(500)
            moveFile()
        }
        return Result.success()
    }

    override suspend fun getEntry(): FilterTarget? {
        val uriId = inputData.getLong(WORK_DATA_ID, -1)

        if (uriId == -1L) return null

        return filterDao.claimByUriId(uriId)
    }
}

