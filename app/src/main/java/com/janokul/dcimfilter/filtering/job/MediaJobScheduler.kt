package com.janokul.dcimfilter.filtering.job

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.work.Constraints
import com.janokul.dcimfilter.JOB_ID

private const val TAG = "MediaJobScheduler"


class MediaJobScheduler(private val context: Context) {
    private val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

    fun buildAndStartJob() {
        val jobInfo = buildJob()
        startJob(jobInfo)
    }

    fun startJob(jobInfo: JobInfo) {
        jobScheduler.schedule(jobInfo)
        Log.d(TAG, "Scheduled Job")
    }

    fun stopJob() {
        jobScheduler.cancel(JOB_ID)
        Log.d(TAG, "Cancelled Job")
    }

    fun buildJob(): JobInfo {
        val componentName = ComponentName(context, MediaJobService::class.java)

        val jobInfo = JobInfo.Builder(JOB_ID, componentName)
            .addTriggerContentUri(
                JobInfo.TriggerContentUri(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS
                )
            )
            .addTriggerContentUri(
                JobInfo.TriggerContentUri(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS
                )
            )

        Log.d(TAG, "Built Job")
        return jobInfo.build()
    }
}