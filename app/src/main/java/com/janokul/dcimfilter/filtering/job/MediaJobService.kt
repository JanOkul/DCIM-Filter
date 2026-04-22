package com.janokul.dcimfilter.filtering.job

import android.app.job.JobParameters
import android.app.job.JobService
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.janokul.dcimfilter.PREFS_DESTINATION_FOLDER
import com.janokul.dcimfilter.WORKER_ID
import com.janokul.dcimfilter.filtering.scanners.QueryResult
import com.janokul.dcimfilter.filtering.workers.BatchFileMoverWorker
import com.janokul.dcimfilter.room.target.FilterTarget
import com.janokul.dcimfilter.room.target.FilterTargetDao
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val TAG = "MediaJobService"
class MediaJobService: JobService() {
    private lateinit var destinationFolder: String

    @Inject
    lateinit var filterTargetDao: FilterTargetDao
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onStartJob(params: JobParameters?): Boolean {
        val uris = params?.triggeredContentUris ?: emptyArray()

        destinationFolder = params?.extras?.getString(PREFS_DESTINATION_FOLDER) ?: "N/A"
        processTrigger(uris)

        reschedule()
        jobFinished(params, false)
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    private fun reschedule() {
        MediaJobScheduler(this).buildAndStartJob()
    }

    private fun processTrigger(uris: Array<Uri>) {
        val resultCursor = queryUriInfo(uris)
        val extractedResults = extractCursor(resultCursor).map { result ->
            FilterTarget(
                name = result.name,
                uriId = result.id,
                mimeType = result.mimeType,
                destinationFolder = destinationFolder
            )
        }

        scope.launch {
            filterTargetDao.insertAll(extractedResults)
            Log.d(TAG, "Enqueued length: ${extractedResults.size}")
            createWork()
        }


    }

    private fun queryUriInfo(uris: Array<Uri>): Cursor? {
        val projection = arrayOf(
            MediaStore.MediaColumns.OWNER_PACKAGE_NAME,
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DISPLAY_NAME
        )

        val ids = uris.map { it.lastPathSegment }
        val questionMarks = ids.joinToString(separator = ", ") { "?" }
        val selection = "${MediaStore.MediaColumns._ID} IN ($questionMarks) AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
        val selectionArgs = ids.toTypedArray() + "${Environment.DIRECTORY_DCIM}/Camera/"

        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            null
        )

        Log.d(TAG, "Cursor length: ${cursor?.count}")
        return cursor
    }

    private fun extractCursor(cursor: Cursor?): ArrayList<QueryResult> {
        val results = ArrayList<QueryResult>()

        cursor?.use {
            val ownerIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.OWNER_PACKAGE_NAME)
            val idIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val mimetypeIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val nameIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)

            if (it.moveToNext()) {
                val result = QueryResult(
                    it.getString(ownerIndex),
                    it.getLong(idIndex),
                    it.getString(mimetypeIndex),
                    it.getString(nameIndex)
                )

                Log.d(TAG, "Query result: $result")
                results.add(result)
            }
        }

        return results
    }

    private fun createWork() {
        val workManager = WorkManager.getInstance(this)
        val work = OneTimeWorkRequestBuilder<BatchFileMoverWorker>().setConstraints(
            Constraints.Builder().setRequiresBatteryNotLow(true).setRequiresStorageNotLow(true)
                .build()
        ).build()

        workManager.enqueueUniqueWork(
            WORKER_ID, ExistingWorkPolicy.REPLACE, work
        )
    }

}