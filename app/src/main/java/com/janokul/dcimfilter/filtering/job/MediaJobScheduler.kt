package com.janokul.dcimfilter.filtering.job

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.provider.MediaStore
import android.util.Log

private val TAG = "MediaJobScheduler"

class MediaJobScheduler(private val context: Context) {
    private val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    private val jobId = 0

    fun buildAndStartJob() {
        jobScheduler.schedule(buildJobInfo())
        Log.d(TAG, "Scheduled Job")
    }

    fun stopJob() {
        jobScheduler.cancel(jobId)
        Log.d(TAG, "Cancelled Job")
    }

    private fun buildJobInfo(): JobInfo {

        val componentName = ComponentName(context, MediaJobService::class.java)
        val jobInfo = JobInfo.Builder(jobId, componentName)
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
            //todo add .extras for dest folder

        return jobInfo.build()
    }
}