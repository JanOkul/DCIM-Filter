package com.example.dcimfilter.ui_components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dcimfilter.R
import com.example.dcimfilter.settings.SettingsViewModel

val subtitleStyle = Typography().titleSmall
val descriptionStyle = Typography().bodySmall


/**
 *  The setting card to be displayed in the main screen.
 *  @param viewModel The view model to be passed to each setting that needs it.
 */
@Composable
fun SettingsCard(viewModel: SettingsViewModel = viewModel()) {
    val isOn by viewModel.isEnabled.collectAsState(initial = true)
    val selectedPackage by viewModel.selectedPackage.collectAsState(initial = "")
    val destinationFolder by viewModel.destinationFolder.collectAsState(initial = "")
    val context = LocalContext.current


    val destinationPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            viewModel.setDestinationFolder(uri.toString())
        } else {
            Toast.makeText(
                context,
                "No folder selected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    SettingsContent(
        viewModel,
        isOn,
        selectedPackage,
        destinationFolder,
        { destinationPickerLauncher.launch(null) }
    )
}

/**
 *  The main UI for the settings.
 *  @param viewModel The view model to be passed to each setting that needs it.
 *  @param isOn The current state of the on/off switch.
 *  @param selectedPackage The current selected package (to be displayed to user).
 *  @param destinationFolder The current destination folder (to be displayed to user).
 *  @param destinationPickerLauncher The activity result launcher for the destination picker.
 */
@Composable
private fun SettingsContent(
    viewModel: SettingsViewModel,
    isOn: Boolean,
    selectedPackage: String,
    destinationFolder: String,
    destinationPickerLauncher: () -> Unit
) {
    val title = stringResource(R.string.settings_title)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
//                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleMedium)

            IsOnSetting(viewModel, isOn)
            SourcePackageSetting(viewModel, selectedPackage, isOn)
            DestinationFolderSetting(destinationFolder, destinationPickerLauncher, isOn)
        }
    }
}

/**
 *  The settings UI for the on/off switch.
 *  @param viewModel The view model for the settings.
 *  @param isOn The current state of the switch.
 */
@Composable
private fun IsOnSetting(viewModel: SettingsViewModel, isOn: Boolean) {
    val subtitle = stringResource(R.string.settings_on_off_subtitle)
    val description = stringResource(R.string.settings_on_off_description)

    val switch = @Composable {
        Switch(
            checked = isOn,
            onCheckedChange = { viewModel.setIsEnabled(it) },
            thumbContent = if (isOn) {
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
            Text(description, style = descriptionStyle)
        }

        switch()
    }
}

/**
 *  The settings UI for the source package picker.
 *  @param viewModel The view model for the settings.
 *  @param selectedPackage The current selected package (to be displayed to user).
 *  @param isOn If the filtering service is active.
 */
@Composable
private fun SourcePackageSetting(viewModel: SettingsViewModel, selectedPackage: String, isOn: Boolean) {
    val subtitle = stringResource(R.string.settings_source_package_subtitle)
    val description = stringResource(R.string.settings_source_package_description)
    val buttonName = stringResource(R.string.settings_source_package_button_name)
    val context = LocalContext.current
    var draftPackage by rememberSaveable(selectedPackage) { mutableStateOf(selectedPackage) }

    LaunchedEffect(isOn, selectedPackage) {
        if (isOn && draftPackage != selectedPackage) {
            draftPackage = selectedPackage
            Toast.makeText(
                context,
                "Selected package not changed. Current package: $selectedPackage",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    SettingsComponent {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Text(subtitle, style = subtitleStyle)
            Text(description, style = descriptionStyle)

            Spacer(modifier = Modifier.size(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                OutlinedTextField(
                    value = draftPackage,
                    enabled = !isOn, // If the filtering service is active, the text field should be disabled.
                    onValueChange = { draftPackage = it },
                    singleLine = true,
                    label = {
                        Text(buttonName)
                    }
                )

                Spacer(Modifier.size(8
                    .dp))

                FilledTonalIconButton(
                    onClick = { viewModel.setSelectedPackage(draftPackage) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = draftPackage != selectedPackage && !isOn
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirm selection"
                    )
                }
            }
        }
    }
}

/**
 *  The settings UI for the destination picker.
 *  @param destinationFolder The current destination folder (to be displayed to user).
 *  @param destinationPickerLauncher The activity result launcher for the destination picker.
 *  @param isOn If the filtering service is active.
 */
@Composable
private fun DestinationFolderSetting(
    destinationFolder: String,
    destinationPickerLauncher: () -> Unit,
    isOn: Boolean
) {
    val subtitle = stringResource(R.string.settings_destination_subtitle)
    val description = stringResource(R.string.settings_destination_description)
    val buttonName = stringResource(R.string.settings_destination_button_name)
    val currentDestination = stringResource(R.string.settings_current_destination_uri)

    SettingsComponent {
        Column(/*Modifier.weight(1f).padding(end = 16.dp))*/) {
            Text(subtitle, style = subtitleStyle)
            Text(description, style = descriptionStyle)

            Spacer(modifier = Modifier.size(8.dp))

            OutlinedTextField(
                value = destinationFolder,
                onValueChange = {},
                readOnly = true,
                enabled = !isOn,
                label = { Text(currentDestination) }
            )

            Spacer(modifier = Modifier.size(8.dp))

            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { destinationPickerLauncher() },
                enabled = !isOn // If the filtering service is active, the button should be disabled.
            ) {
                Text(buttonName)
            }
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
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        composable()
    }
}