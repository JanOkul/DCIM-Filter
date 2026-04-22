package com.janokul.dcimfilter.filtering.workers

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.janokul.dcimfilter.room.history.History
import com.janokul.dcimfilter.room.history.HistoryDao
import com.janokul.dcimfilter.room.target.FilterTarget
import com.janokul.dcimfilter.room.target.FilterTargetDao
import jakarta.inject.Inject

/**
 * Implements common functionality used by all file mover workers.
 */
abstract class FileMoverWorker(
    context: Context,
    params: WorkerParameters,
    private val tag: String
) : CoroutineWorker(context, params) {

    @Inject
    lateinit var filterTargetDao: FilterTargetDao

    @Inject
    lateinit var historyDao: HistoryDao

    suspend fun moveFile() {
        val entry = getEntry() ?: return
        val resolver = applicationContext.contentResolver
        val mimeType = entry.mimeType

        Log.d(tag, "Received entry: $entry")

        val destinationPath = convertMimeToDir(mimeType, entry.destinationFolder)

        val uri = ContentUris.withAppendedId(
            convertMimeToBaseUri(mimeType),
            entry.uriId
        )

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.RELATIVE_PATH, destinationPath)
        }

        val movedCount = resolver.update(uri, contentValues, null, null)

        if (movedCount > 0) {
            val historyEntry = History(
                filename = entry.name,
                uriId = entry.uriId,
                mimeType = entry.mimeType,
                movedTo = destinationPath
            )
            historyDao.insertHistoryEntry(historyEntry)
        }

        Log.d(tag, "Moved file: $uri")
    }

    abstract suspend fun getEntry(): FilterTarget?

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