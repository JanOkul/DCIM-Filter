package com.janokul.dcimfilter.filtering.scanners

import android.content.Context
import android.database.Cursor
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.janokul.dcimfilter.WORKER_ID
import com.janokul.dcimfilter.filtering.workers.BatchFileMoverWorker
import com.janokul.dcimfilter.room.DcimFilterDb
import com.janokul.dcimfilter.room.target.FilterTarget
import com.janokul.dcimfilter.room.target.FilterTargetDao
import jakarta.inject.Inject

private const val TAG = "BatchScanner"

class BatchScanner(
    private val context: Context,
    private val owner: String,
    private val destinationFolder: String,
) {
    @Inject
    lateinit var filterTargetDao: FilterTargetDao
    private val relativePath = "${Environment.DIRECTORY_DCIM}/Camera/"

    suspend fun batchFilter() {

        val files = sendDCIMQuery()
        Log.d(TAG, "File count: ${files.size}")

        files.forEach {
            filterTargetDao.insertFilterTarget(
                FilterTarget(
                    name = it.name,
                    uriId = it.id,
                    mimeType = it.mimeType,
                    destinationFolder = destinationFolder
                )
            )
        }

        Log.d(TAG, "Starting work")
        createWork()
    }

    /**
     *  Retrieves information from MediaStore about the entire DCIM folder at once.
     */
    private fun sendDCIMQuery(): List<QueryResult> {
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.OWNER_PACKAGE_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} = ?"

        val selectionArgs = arrayOf(relativePath)

        Log.d(TAG, "owner length: ${owner.length}")

        val cursor = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, null
        )

        Log.d(TAG, "Cursor length: ${cursor?.count}")
        return extractResult(cursor)
    }

    /**
     * Extracts the projection into a list of QueryResults objects, from a MediaStore query.
     */
    private fun extractResult(cursor: Cursor?): List<QueryResult> {
        val results = ArrayList<QueryResult>()

        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val mimetypeIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val ownerIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.OWNER_PACKAGE_NAME)
            val nameIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)


            while (it.moveToNext()) {
                // Can't do this in query without QUERY_ALL_PACKAGES permission.
                if (it.getString(ownerIndex) != owner) {
                    continue
                }

                val result = QueryResult(
                    owner, it.getLong(idIndex), it.getString(mimetypeIndex), it.getString(nameIndex)
                )

                Log.d(TAG, "Query result: $result")
                results.add(result)
            }
        }

        return results
    }

    private fun createWork() {
        val workManager = WorkManager.getInstance(context)
        val work = OneTimeWorkRequestBuilder<BatchFileMoverWorker>().setConstraints(
            Constraints.Builder().setRequiresBatteryNotLow(true).setRequiresStorageNotLow(true)
                .build()
        ).build()

        workManager.enqueueUniqueWork(
            WORKER_ID, ExistingWorkPolicy.REPLACE, work
        )
    }
}