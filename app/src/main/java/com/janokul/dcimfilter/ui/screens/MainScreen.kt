package com.janokul.dcimfilter.ui.screens

import android.app.Activity
import android.content.Intent
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
import com.janokul.dcimfilter.R
import com.janokul.dcimfilter.settings.SettingsViewModel
import com.janokul.dcimfilter.ui.components.misc.AppSettings
import com.janokul.dcimfilter.ui.components.misc.hasAllFileAccess
import com.janokul.dcimfilter.ui.components.misc.hasUnrestrictedBattery
import com.janokul.dcimfilter.ui.components.ui.FilterCard
import com.janokul.dcimfilter.ui.components.ui.InsufficientPermissionsCard
import com.janokul.dcimfilter.ui.components.ui.PrimaryAppBar
import com.janokul.dcimfilter.ui.components.ui.SettingsCard


/**
 *  The parent composable for the entire UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    var allFileAccess by remember { mutableStateOf(hasAllFileAccess()) }
    var unrestrictedBattery by remember { mutableStateOf(hasUnrestrictedBattery(context)) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        allFileAccess = hasAllFileAccess()
        unrestrictedBattery = hasUnrestrictedBattery(context)
    }

    if (!unrestrictedBattery) {
        val onAccept = {
            context.startActivity(
                Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            )
        }

        val onDecline = {
            (context as? Activity)?.finishAffinity()
            Unit
        }

        PermissionDialog(
            stringResource(R.string.permission_battery_title),
            stringResource(R.string.permission_battery_description),
            onAccept,
            onDecline
        )
    }

    if (!allFileAccess) {
        val onAccept = {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = "package:${context.packageName}".toUri()
            }
            context.startActivity(intent)
        }

        val onDecline = {
            (context as? Activity)?.finishAffinity()
            Unit
        }

        PermissionDialog(
            stringResource(R.string.permission_storage_title),
            stringResource(R.string.permission_storage_description),
            onAccept,
            onDecline
        )
    }

    val viewModel: SettingsViewModel = viewModel()
    val isEnabled by viewModel.isEnabled.collectAsStateWithLifecycle(initialValue = false)
    val selectedPackage by viewModel.sourcePackage.collectAsStateWithLifecycle(initialValue = "")
    val destinationFolder by viewModel.destinationFolder.collectAsStateWithLifecycle(initialValue = "")
    val timeoutNotification by viewModel.timeoutNotification.collectAsStateWithLifecycle(initialValue = false)
    val settings = AppSettings(isEnabled, selectedPackage, destinationFolder, timeoutNotification)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { PrimaryAppBar(navController) }
    ) { innerPadding ->
        if (hasAllFileAccess() && hasUnrestrictedBattery(context)) {
            MainBody(innerPadding, navController, viewModel, settings)
        } else {
            InsufficientPermissionsCard(innerPadding)
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
private fun PermissionDialog(
    title: String,
    description: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val accept = stringResource(R.string.permission_ok)
    val decline = stringResource(R.string.permission_cancel)

    AlertDialog(
        onDismissRequest = { onDecline() },
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            TextButton(onClick = { onAccept() }) {
                Text(accept)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDecline() }) {
                Text(decline)
            }
        }
    )
}




