package com.example.dcimfilter

import android.os.ParcelFileDescriptor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dcimfilter.settings.SettingsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { innerPadding ->
        MainBody(innerPadding)
    }
}

@Composable
fun MainBody(innerPadding: PaddingValues) {
    var checked by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Filter()
        Settings()
    }
}

@Composable
fun Filter() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(
                R.string.card1_title
            ),
                style = MaterialTheme.typography.titleMedium
            )
            Text(stringResource(
                R.string.card1_body
            ),
                style = MaterialTheme.typography.bodyMedium
            )
            FilledTonalButton(onClick = {}) {
                Text(stringResource(R.string.card1_button))
            }
        }
    }
}

@Composable
fun Settings(viewModel: SettingsViewModel = viewModel()) {
    val isOn by viewModel.isEnabled.collectAsState(initial = true)
    val selectedPackage by viewModel.selectedPackage.collectAsState(initial = "")

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(
                R.string.card2_title
            ),
                style = MaterialTheme.typography.titleMedium
            )

            SettingsComponent(
                "On/Off",
                "Toggle operation button"
            ) {
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text("Source Package", style = MaterialTheme.typography.titleSmall)
                    Text(
                        stringResource(R.string.settings_source_package_description),
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = selectedPackage,
                        onValueChange = { viewModel.setSelectedPackage(it) },
                        label = { Text("Package Name") }
                    )
                }
            }

            SettingsComponent(
                "Destination Folder",
                "Where the filtered files are moved to"
            ) {
                //todo
            }
        }
    }
}

@Composable
fun SettingsComponent(
    name: String,
    description: String,
    settingComponent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(name, style = MaterialTheme.typography.titleSmall)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }

        settingComponent()
    }
}
