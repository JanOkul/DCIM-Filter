package com.example.dcimfilter.ui.components.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.dcimfilter.R
import com.example.dcimfilter.filtering.scanners.BatchScanner
import com.example.dcimfilter.settings.SettingsViewModel
import com.example.dcimfilter.ui.components.misc.AppSettings


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *  The filter card UI to be displayed in the main screen.
 */
@Composable
fun FilterCard(viewModel: SettingsViewModel, settings: AppSettings) {
    val subtitle = stringResource(R.string.filter_subtitle)
    val description = stringResource(R.string.filter_description)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(subtitle, style = MaterialTheme.typography.titleMedium)

            Column(modifier = Modifier.padding(8.dp)) {
                Text(description, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.size(8.dp))
                FilterButtonAndProgress(viewModel, settings)
            }
        }
    }
}

@Composable
private fun FilterButtonAndProgress(viewModel: SettingsViewModel, settings: AppSettings) {
    val context = LocalContext.current
    val workInfo = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkLiveData("batch_file_move")
        .observeAsState().value?.firstOrNull()
    val filteringInProgress = workInfo?.state == WorkInfo.State.RUNNING

    var wasFiltering by remember { mutableStateOf(false) }
    val toastText = stringResource(R.string.filter_toast)

    LaunchedEffect(filteringInProgress) {
        if (wasFiltering && !filteringInProgress) {
            Toast.makeText(
                context,
                toastText,
                Toast.LENGTH_LONG
            ).show()
        }

        wasFiltering = filteringInProgress
    }

    val scope = rememberCoroutineScope()
    val filesToMove = workInfo?.progress?.getInt("files_to_move", 0)
    val canEnable =
        !filteringInProgress && settings.sourcePackage.isNotBlank() && settings.destinationFolder.isNotBlank()

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalButton(
            enabled = canEnable,
            onClick = {
                scope.launch(Dispatchers.IO) {
                    filterOnClick(context, settings, viewModel)
                }
            }
        ) {
            Text(stringResource(R.string.filter_button))
        }

        if (filesToMove != null) {
            if (filteringInProgress && filesToMove > 0) {
                CircularProgressIndicator(
                    Modifier.size(24.dp)
                )
            }
        }
    }
}

private suspend fun filterOnClick(
    context: Context,
    settings: AppSettings,
    viewModel: SettingsViewModel
) {
    // Temporarily disable file service
    val previousServiceState = settings.isOn
    viewModel.setIsEnabled(false)

    BatchScanner(context, settings.sourcePackage, settings.destinationFolder)
        .batchFilter()

    // Reset file service state to what it was before
    viewModel.setIsEnabled(previousServiceState)
}