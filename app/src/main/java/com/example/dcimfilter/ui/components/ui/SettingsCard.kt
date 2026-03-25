package com.example.dcimfilter.ui.components.ui

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dcimfilter.R
import com.example.dcimfilter.filtering.scanners.FileScannerService
import com.example.dcimfilter.settings.SettingsViewModel
import com.example.dcimfilter.ui.components.misc.AppSettings
import kotlinx.coroutines.delay

val subtitleStyle = Typography().titleSmall
val descriptionStyle = Typography().bodySmall
const val TAG = "Settings"

/**
 *  The setting card to be displayed in the main screen.
 *  @param viewModel The view model to be passed to each setting that needs it.
 *  @param settings Data class of application settings
 */
@Composable
fun SettingsCard(
    viewModel: SettingsViewModel,
    navController: NavController,
    settings: AppSettings
) {

    val context = LocalContext.current

    LaunchedEffect(settings.isOn) {
        if (settings.isOn) {
            context.startForegroundService(
                Intent(context, FileScannerService::class.java)
                    .putExtra("selectedPackage", settings.selectedPackage)
                    .putExtra("destinationFolder", settings.destinationFolder)
            )
        } else {
            context.stopService(
                Intent(context, FileScannerService::class.java)
            )
        }
    }

    SettingsContent(
        viewModel,
        navController,
        settings
    )
}

/**
 *  The main UI for the settings.
 *  @param viewModel The view model to be passed to each setting that needs it.
 *  @param settings Data class of application settings
 */
@Composable
private fun SettingsContent(
    viewModel: SettingsViewModel,
    navController: NavController,
    settings: AppSettings
) {
    val title = stringResource(R.string.settings_title)


    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium)

            IsOnSetting(viewModel, settings)
            SourcePackageSetting(viewModel, navController, settings)
            DestinationFolderSetting(viewModel, settings)
        }
    }
}

/**
 *  The settings UI for the on/off switch.
 *  @param viewModel The view model for the settings.
 *  @param settings Data class of application settings
 */
@Composable
private fun IsOnSetting(viewModel: SettingsViewModel, settings: AppSettings) {
    val subtitle = stringResource(R.string.settings_on_off_subtitle)
    val description = stringResource(R.string.settings_on_off_description)
    val hint = stringResource(R.string.settings_on_off_hint)
    val canEnable = settings.selectedPackage.isNotBlank() && settings.destinationFolder.isNotBlank()

    val switch = @Composable {
        Switch(
            checked = settings.isOn,
            enabled = canEnable || settings.isOn,
            onCheckedChange = { viewModel.setIsEnabled(it) },
            thumbContent = if (settings.isOn) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            } else {
                null
            }
        )
    }

    SettingsComponent {
        Column(Modifier.weight(1f)) {
            Text(subtitle, style = subtitleStyle)
            Text("$description ${if (!canEnable) hint else ""}",
                style = descriptionStyle,
                color = if (!canEnable && ! settings.isOn) MaterialTheme.colorScheme.error else Color.Unspecified
            )
        }

        switch()
    }
}

/**
 *  The settings UI for the source package picker.
 *  @param viewModel The view model for the settings.
 *  @param settings Data class of application settings
 */
@Composable
private fun SourcePackageSetting(
    viewModel: SettingsViewModel,
    navController: NavController,
    settings: AppSettings
) {
    val subtitle = stringResource(R.string.settings_source_package_subtitle)
    val description = stringResource(R.string.settings_source_package_description)
    val hint = stringResource(R.string.settings_source_package_hint)
    val buttonName = stringResource(R.string.settings_source_package_button_name)
    val currentPackage = stringResource(R.string.settings_source_package_current_package)
    val isBlank = settings.selectedPackage.isBlank()

    SettingsComponent {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Text(subtitle, style = subtitleStyle)
            Text("$description ${if (isBlank) hint else ""}",
                style = descriptionStyle,
                color = if (isBlank) MaterialTheme.colorScheme.error else Color.Unspecified
            )

            Spacer(modifier = Modifier.size(8.dp))

            OutlinedTextField(
                value = settings.selectedPackage,
                onValueChange = {},
                readOnly = true,
                enabled = !settings.isOn,
                label = { Text(currentPackage) }
            )

            Spacer(modifier = Modifier.size(8.dp))

            FilledTonalButton (
                onClick = { navController.navigate("package_select") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !settings.isOn
            ) {
                Text(buttonName)
            }

        }
    }
}

/**
 *  The settings UI for the destination picker.
 *  @param viewModel The view model for the settings.
 *  @param settings Data class of application settings
 */
@Composable
private fun DestinationFolderSetting(
    viewModel: SettingsViewModel,
    settings: AppSettings
) {
    val subtitle = stringResource(R.string.settings_destination_subtitle)
    val description = stringResource(R.string.settings_destination_description)
    val hint = stringResource(R.string.settings_destination_hint)
    val currentDestination = stringResource(R.string.settings_current_destination_uri)
    var text by remember(settings.destinationFolder) { mutableStateOf(settings.destinationFolder) }
    val isBlank = settings.destinationFolder.isBlank()

    LaunchedEffect(text) {
        if (text != settings.destinationFolder) {
            delay(300)
            viewModel.setDestinationFolder(text)
            Log.d(TAG, "Setting destination folder to $text")
        }
    }

    SettingsComponent {
        Column {
            Text(subtitle, style = subtitleStyle)
            Text("$description ${if (isBlank) hint else ""}",
                style = descriptionStyle,
                color = if (isBlank) MaterialTheme.colorScheme.error else Color.Unspecified
            )

            Spacer(modifier = Modifier.size(8.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                enabled = !settings.isOn,
                label = { Text(currentDestination) }
            )
        }
    }
}

/**
 *  A standardized container for each setting in the application.
 *  @param composable The composable UI element that will be within a setting.
 */
@Composable
private fun SettingsComponent(composable: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        composable()
    }
}