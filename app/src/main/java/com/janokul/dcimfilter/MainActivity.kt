package com.janokul.dcimfilter

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.janokul.dcimfilter.ui.history.HistoryScreen
import com.janokul.dcimfilter.ui.main.MainScreen
import com.janokul.dcimfilter.ui.rule.RuleScreen
import com.janokul.dcimfilter.ui.theme.DCIMFilterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { (perm, granted) ->
            Log.d("MediaStoreTest", "$perm granted: $granted")
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
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


