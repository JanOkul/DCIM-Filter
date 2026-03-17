package com.example.dcimfilter.ui.components

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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.work.WorkManager
import com.example.dcimfilter.R
import com.example.dcimfilter.filtering.scanners.BatchScanner
import com.example.dcimfilter.filtering.workers.BatchFileMoverWorker
import com.example.dcimfilter.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration

/**
 *  The filter card UI to be displayed in the main screen.
 */
@Composable
fun FilterCard(context: Context, viewModel: SettingsViewModel, selectedPackage: String, destinationFolder: String) {
    val scope = rememberCoroutineScope()
    val subtitle = stringResource(R.string.batch_filter_subtitle)
    val description = stringResource(R.string.batch_filter_description)
    val buttonName = stringResource(R.string.batch_filter_button_name)
    val workInfo by WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkLiveData("batch_file_move")
        .observeAsState()
    val progressBarMessage = workInfo?.firstOrNull()?.progress?.getString("progress_message") ?: ""
    val progressBarFloat =workInfo?.firstOrNull()?.progress?.getFloat("progress_float", defaultValue = 0.0f) ?: 0.0f
    var filteringInProgress by remember { mutableStateOf(false) }


    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(subtitle, style = MaterialTheme.typography.titleMedium)

            Column(modifier = Modifier.padding(8.dp)) {
                Text(description, style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.size(8.dp))

                // todo
                Row(
                ) {
                    FilledTonalButton(
                        enabled = selectedPackage != "" && destinationFolder != "" && !filteringInProgress,
                        onClick = {
                        scope.launch(Dispatchers.IO) {
                            filteringInProgress = true
                            val prevState = viewModel.isEnabled.first()
                            viewModel.setIsEnabled(false)
                            BatchScanner(context, selectedPackage, destinationFolder).batchFilter()
                            viewModel.setIsEnabled(prevState)
                            filteringInProgress = false

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context,
                                    "Batch filtering complete",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    }) {
                        Text(buttonName)
                    }

                    Spacer(Modifier.size(16.dp))

                    Column(
                        verticalArrangement = Arrangement.Bottom
                    ) {

                        Text(progressBarMessage)
                        LinearProgressIndicator(
                            progress = { progressBarFloat },

                            )
                    }
                }
            }
        }
    }
}