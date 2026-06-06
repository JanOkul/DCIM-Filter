package com.janokul.dcimfilter.processing.job

import android.app.job.*
import android.util.Log
import com.janokul.dcimfilter.processing.filtering.ContentFilterEngine
import com.janokul.dcimfilter.processing.movers.*
import com.janokul.dcimfilter.room.rule.FilterRuleDao
import com.janokul.dcimfilter.room.target.FilterTargetDao
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.*

private const val TAG = "MediaJobService"
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

            val engine = ContentFilterEngine(context.contentResolver, enabledRules)
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