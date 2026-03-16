package com.example.dcimfilter.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.example.dcimfilter.R
import com.example.dcimfilter.ui.components.FilterCard
import com.example.dcimfilter.ui.components.NoStorageAccessCard
import com.example.dcimfilter.ui.components.SettingsCard
import com.example.dcimfilter.ui.components.hasAllFileAccess
import com.example.dcimfilter.ui.components.hasUnrestrictedBattery


/**
 *  The parent composable for the entire UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val appName = stringResource(R.string.app_name)
    val context = LocalContext.current

    var allFileAccess by remember { mutableStateOf(
        hasAllFileAccess()
    ) }

    var unrestrictedBattery by remember { mutableStateOf(
        hasUnrestrictedBattery(context)
    ) }


    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        allFileAccess = hasAllFileAccess()
        unrestrictedBattery = hasUnrestrictedBattery(context)
    }



    if (!allFileAccess) {
        StoragePermissionDialog(context)
    }

    if (!unrestrictedBattery) {
        PowerOptimisationDialog(context) {
            unrestrictedBattery = it
            unrestrictedBattery
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(appName, style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { innerPadding ->
        if (Environment.isExternalStorageManager()) {
            MainBody(innerPadding, navController)
        } else {
            NoStorageAccessCard(innerPadding)

        }
    }
}

/**
 *  The main screen content composable
 *  @param innerPadding The padding values for the content
 */
@Composable
fun MainBody(innerPadding: PaddingValues, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilterCard()
        SettingsCard(navController = navController)
    }
}

@Composable
fun PowerOptimisationDialog(context: Context, changeDialogState: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { changeDialogState(true) },
        title = { Text(stringResource(R.string.permission_battery_title)) },
        text = { Text(stringResource(R.string.permission_battery_description)) },
        confirmButton = {
            TextButton(onClick = {
                context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
            }) { Text(stringResource(R.string.permission_ok)) }
        },
        dismissButton = {
            TextButton(onClick = { changeDialogState(true) }) {
                Text(stringResource(R.string.permission_cancel))
            }
        }
    )
}

@Composable
fun StoragePermissionDialog(context: Context) {
    AlertDialog(
        onDismissRequest = { (context as? Activity)?.finish() },
        title = { Text(stringResource(R.string.permission_storage_title)) },
        text = { Text(stringResource(R.string.permission_storage_description)) },
        confirmButton = {
            TextButton(onClick = {
                context.startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = "package:${context.packageName}".toUri()
                })
            }) { Text(stringResource(R.string.permission_ok)) }
        },
        dismissButton = {
            TextButton(onClick = { (context as? Activity)?.finish() }) {
                Text(stringResource(R.string.permission_cancel))
            }
        }
    )
}




