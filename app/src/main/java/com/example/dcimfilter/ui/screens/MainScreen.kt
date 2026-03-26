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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dcimfilter.R
import com.example.dcimfilter.settings.SettingsViewModel
import com.example.dcimfilter.ui.components.misc.AppSettings
import com.example.dcimfilter.ui.components.misc.hasAllFileAccess
import com.example.dcimfilter.ui.components.misc.hasUnrestrictedBattery
import com.example.dcimfilter.ui.components.ui.FilterCard
import com.example.dcimfilter.ui.components.ui.NoStorageAccessCard
import com.example.dcimfilter.ui.components.ui.PrimaryAppBar
import com.example.dcimfilter.ui.components.ui.SettingsCard


/**
 *  The parent composable for the entire UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val viewModel: SettingsViewModel = viewModel()
    val isOn by viewModel.isEnabled.collectAsStateWithLifecycle(initialValue = false)
    val selectedPackage by viewModel.sourcePackage.collectAsStateWithLifecycle(initialValue = "")
    val destinationFolder by viewModel.destinationFolder.collectAsStateWithLifecycle(initialValue = "")
    val settings = AppSettings(isOn, selectedPackage, destinationFolder)
    val context = LocalContext.current

    var allFileAccess by remember {
        mutableStateOf(
            hasAllFileAccess()
        )
    }

    var unrestrictedBattery by remember {
        mutableStateOf(
            hasUnrestrictedBattery(context)
        )
    }

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
        topBar = { PrimaryAppBar(navController) }
    ) { innerPadding ->
        if (Environment.isExternalStorageManager()) {
            MainBody(innerPadding, navController, viewModel, settings)
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
private fun MainBody(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: SettingsViewModel,
    settings: AppSettings
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilterCard(viewModel, settings)
        SettingsCard(viewModel, navController, settings)
    }
}

@Composable
private fun PowerOptimisationDialog(context: Context, changeDialogState: (Boolean) -> Unit) {
    val title = stringResource(R.string.permission_battery_title)
    val description = stringResource(R.string.permission_battery_description)
    val accept = stringResource(R.string.permission_ok)
    val decline = stringResource(R.string.permission_cancel)

    AlertDialog(
        onDismissRequest = { changeDialogState(true) },
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            TextButton(onClick = {
                context.startActivity(
                    Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                )
            }) {
                Text(accept)
            }
        },
        dismissButton = {
            TextButton(onClick = { changeDialogState(true) }) {
                Text(decline)
            }
        }
    )
}

@Composable
private fun StoragePermissionDialog(context: Context) {
    val title = stringResource(R.string.permission_storage_title)
    val description = stringResource(R.string.permission_storage_description)
    val accept = stringResource(R.string.permission_ok)
    val decline = stringResource(R.string.permission_cancel)

    val acceptOnclick = { context: Context ->
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = "package:${context.packageName}".toUri()
        }
        context.startActivity(intent)
    }

    AlertDialog(
        onDismissRequest = { (context as? Activity)?.finish() },
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            TextButton(onClick = { acceptOnclick(context) }) {
                Text(accept)
            }
        },
        dismissButton = {
            TextButton(onClick = { (context as? Activity)?.finish() }) {
                Text(decline)
            }
        }
    )
}




