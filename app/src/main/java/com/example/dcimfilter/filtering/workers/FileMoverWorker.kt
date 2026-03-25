package com.example.dcimfilter.filtering.workers

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dcimfilter.room.FilterDB
import com.example.dcimfilter.room.history.History

private const val TAG = "FileMoverWorker"

/**
 * Implements common functionality used by all file mover workers.
 */
abstract class FileMoverWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    protected val filterDao by lazy { FilterDB.getInstance(applicationContext).filterDao }
    protected val historyDao by lazy { FilterDB.getInstance(applicationContext).historyDao }

    suspend fun moveFile() {
        val nextEntry = filterDao.claimNext() ?: return
        val resolver =  applicationContext.contentResolver
        val mimeType = nextEntry.mimeType

        Log.d(TAG, "Received entry: $nextEntry")

        val destinationPath = convertMimeToDir(mimeType, nextEntry.destinationFolder)

        val uri = ContentUris.withAppendedId(
            convertMimeToBaseUri(mimeType),
            nextEntry.uriId
        )

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.RELATIVE_PATH, destinationPath)
        }

        val movedCount = resolver.update(uri, contentValues, null, null)

        if (movedCount > 0) {
            val historyEntry = History(filename = nextEntry.name, movedTo = destinationPath)
            historyDao.insertHistoryEntry(historyEntry)
        }

        Log.d(TAG, "Moved file: $uri")
    }

    private fun convertMimeToBaseUri(mimeType: String): Uri {
        return when {
            mimeType.startsWith("video/") -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            mimeType.startsWith("image/") -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external")
        }
    }

    private fun convertMimeToDir(mimeType: String, destinationFolder: String): String {
        return when {
            mimeType.startsWith("video/") -> "Movies/$destinationFolder"
            mimeType.startsWith("image/") -> "Pictures/$destinationFolder"
            else -> "${Environment.DIRECTORY_DCIM}/Camera"
        }
    }
}