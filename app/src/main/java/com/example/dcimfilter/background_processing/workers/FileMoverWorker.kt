package com.example.dcimfilter.background_processing.workers

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dcimfilter.queue.FilterDB

abstract class FileMoverWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    private val dao by lazy { FilterDB.getInstance(applicationContext).filterDao }

    suspend fun moveFile(): Result {
        val nextEntry = dao.peekFilterTarget() ?: return Result.failure()

        val resolver =  applicationContext.contentResolver
        val mimeType = nextEntry.mimeType

        Log.d(TAG, "${MediaStore.canManageMedia(applicationContext)}")
        Log.d(TAG, "uri=${nextEntry.uriId}")
        Log.d(TAG, "mimeType=$mimeType")


        val base = when {
            mimeType.startsWith("video/") -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            mimeType.startsWith("image/") -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external")
        }

        val uri = ContentUris.withAppendedId(
            base,
            nextEntry.uriId
        )

        val newRelPath = when {
            mimeType.startsWith("video/") -> "Movies/Tiktok"
            mimeType.startsWith("image/") -> "Pictures/Tiktok"
            else -> "DCIM/Camera"
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.RELATIVE_PATH, newRelPath)
        }

        MediaStore.createWriteRequest(resolver, listOf(uri)).send()

        resolver.update(uri, contentValues, null, null)
        return Result.success()
    }
}