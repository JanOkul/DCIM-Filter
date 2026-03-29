package com.janokul.dcimfilter.ui.components.ui

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
import com.janokul.dcimfilter.NavNames
import com.janokul.dcimfilter.R
import com.janokul.dcimfilter.settings.SettingsViewModel
import com.janokul.dcimfilter.ui.components.misc.AppSettings
import kotlinx.coroutines.delay

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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleMedium
            )

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
    val canEnable = settings.sourcePackage.isNotBlank() && settings.destinationFolder.isNotBlank()
    val hint = if (!canEnable) stringResource(R.string.settings_state_hint) else ""
    val context = LocalContext.current

    val switch = @Composable {
        Switch(
            checked = settings.isOn,
            enabled = canEnable || settings.isOn,
            onCheckedChange = {
                viewModel.updateServiceState(context.applicationContext,
                    AppSettings(it,
                        settings.sourcePackage,
                        settings.destinationFolder
                    )
                )
            },
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
            Text(
                stringResource(R.string.settings_state_subtitle),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                stringResource(R.string.settings_state_description) + " " + hint,
                style = MaterialTheme.typography.bodySmall,
                color = if (!canEnable && !settings.isOn) MaterialTheme.colorScheme.error else Color.Unspecified
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
    val isBlank = settings.sourcePackage.isBlank()
    val hint = if (isBlank) stringResource(R.string.settings_package_hint) else ""

    SettingsComponent {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Text(
                stringResource(R.string.settings_package_subtitle),
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                stringResource(R.string.settings_package_description) + " " + hint,
                style = MaterialTheme.typography.bodySmall,
                color = if (isBlank) MaterialTheme.colorScheme.error else Color.Unspecified
            )

            Spacer(modifier = Modifier.size(8.dp))

            OutlinedTextField(
                value = settings.sourcePackage,
                onValueChange = {},
                readOnly = true,
                enabled = !settings.isOn,
                label = { Text(stringResource(R.string.settings_current_package)) }
            )

            Spacer(modifier = Modifier.size(8.dp))

            FilledTonalButton(
                onClick = { navController.navigate(NavNames.PACKAGE_SELECT.id) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !settings.isOn
            ) {
                Text(stringResource(R.string.settings_package_button))
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
    var text by remember(settings.destinationFolder) { mutableStateOf(settings.destinationFolder) }
    val isBlank = settings.destinationFolder.isBlank()
    val hint = if (isBlank) stringResource(R.string.settings_destination_hint) else ""

    LaunchedEffect(text) {
        if (text != settings.destinationFolder) {
            delay(300)
            viewModel.setDestinationFolder(text)
            Log.d(TAG, "Setting destination folder to $text")
        }
    }

    SettingsComponent {
        Column {
            Text(
                stringResource(R.string.settings_destination_subtitle),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                stringResource(
                    R.string.settings_destination_description,
                    settings.destinationFolder,
                    settings.destinationFolder
                ) +
                        " " + hint,
                style = MaterialTheme.typography.bodySmall,
                color = if (isBlank) MaterialTheme.colorScheme.error else Color.Unspecified
            )

            Spacer(modifier = Modifier.size(8.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                enabled = !settings.isOn,
                label = { Text(stringResource(R.string.settings_current_destination)) }
            )
        }
    }
}

/**
 *  A standardised container for each setting in the application.
 *  @param composable The composable UI element that will be within a setting.
 */
@Composable
private fun SettingsComponent(composable: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        composable()
    }
}