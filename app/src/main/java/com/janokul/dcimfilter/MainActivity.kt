package com.janokul.dcimfilter

import android.Manifest
import android.app.*
import android.app.job.JobScheduler
import android.os.*
import android.util.Log
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.*
import androidx.navigation.compose.*
import com.janokul.dcimfilter.filtering.job.MediaJobScheduler
import com.janokul.dcimfilter.settings.SettingsViewModel
import com.janokul.dcimfilter.ui.history.HistoryScreen
import com.janokul.dcimfilter.ui.main.MainScreen
import com.janokul.dcimfilter.ui.rule.RuleScreen
import com.janokul.dcimfilter.ui.theme.DCIMFilterTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { (perm, granted) ->
            Log.d("MediaStoreTest", "$perm granted: $granted")
        }
    }

    private val viewModel: SettingsViewModel by viewModels()
    private val jobScheduler by lazy {
        getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onResume() {
        super.onResume()
        val context = this
        lifecycleScope.launch {
            val isEnabled = viewModel.isEnabled.first()
            val isJobScheduled = jobScheduler.getPendingJob(JOB_ID) != null

            if (isEnabled && !isJobScheduled) {
                MediaJobScheduler(context).buildAndStartJob()
            }

            if (!isEnabled && isJobScheduled) {
                MediaJobScheduler(context).stopJob()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
        }

        createNotificationChannel()

        enableEdgeToEdge()
        setContent {
            DCIMFilterTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NavNames.MAIN.id,

                    // Animation
                    enterTransition = {
                        slideInHorizontally(
                            animationSpec = tween(300, easing = EaseOutQuart),
                            initialOffsetX = { it })
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            animationSpec = tween(300, easing = EaseOutQuart),
                            targetOffsetX = { -it / 3 })
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            animationSpec = tween(
                                300,
                                easing = EaseOutQuart
                            ), initialOffsetX = { -it / 3 })
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            animationSpec = tween(300, easing = EaseOutQuart),
                            targetOffsetX = { it })
                    }

                ) {
                    composable(NavNames.MAIN.id) { MainScreen(navController) }
                    composable(NavNames.HISTORY.id) { HistoryScreen(navController) }
                    composable(
                        route = "${NavNames.RULE.id}/{ruleId}?isNew={isNew}",
                        arguments = listOf(
                            navArgument("ruleId") { type = NavType.LongType },
                            navArgument("isNew") {
                                type = NavType.BoolType
                                defaultValue = false
                            }
                        )

                    ) { RuleScreen(navController) }
                }
            }
        }
    }
}


