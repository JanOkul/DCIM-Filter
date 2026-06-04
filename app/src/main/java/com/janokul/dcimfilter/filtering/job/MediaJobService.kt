package com.janokul.dcimfilter.filtering.job

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.janokul.dcimfilter.WORKER_ID
import com.janokul.dcimfilter.filtering.movers.ContentFilterEngine
import com.janokul.dcimfilter.filtering.movers.moveContent
import com.janokul.dcimfilter.filtering.workers.BatchFileMoverWorker
import com.janokul.dcimfilter.room.rule.FilterRule
import com.janokul.dcimfilter.room.rule.FilterRuleDao
import com.janokul.dcimfilter.room.target.FilterTargetDao
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val TAG = "MediaJobService"
@AndroidEntryPoint
class MediaJobService: JobService() {

    @Inject
    lateinit var filterTargetDao: FilterTargetDao

    @Inject
    lateinit var ruleDao: FilterRuleDao

    override fun onStartJob(params: JobParameters?): Boolean {
        val uris = params?.triggeredContentUris ?: emptyArray()
        val context = this
        CoroutineScope(Dispatchers.IO).launch {
            val enabledRules = ruleDao.getAllEnabled()
            Log.d(TAG, "Fetched ${enabledRules.size} rules where ${enabledRules.count { it.enabled }}/${enabledRules.size} are enabled. (Must be 100%)")

            val engine = ContentFilterEngine(context, enabledRules)
            Log.d(TAG, "Filtering the following ${uris.size} URIS.")
            val filteredUris = engine.filterUris(uris)
            Log.d(TAG, "There are ${filteredUris.size} URI groups that need to be filtered.")

            filteredUris.forEach { (rule, ids) ->
                Log.d(TAG, "Moving ${ids.size} in content group ${rule.fromRelativePath} to the path ${rule.toRelativePath}.")
                moveContent(rule, ids)
            }

            requeueJob()
            jobFinished(params, false)
            Log.d(TAG, "Filtering finished.")
        }
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    private fun requeueJob() {
        MediaJobScheduler(this).buildAndStartJob()
    }
}