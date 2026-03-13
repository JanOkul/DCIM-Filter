package com.example.dcimfilter.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.dcimfilter.R

/**
 *  The filter card UI to be displayed in the main screen.
 */
@Composable
fun FilterCard() {
    val subtitle = stringResource(R.string.batch_filter_subtitle)
    val description = stringResource(R.string.batch_filter_description)
    val buttonName = stringResource(R.string.batch_filter_button_name)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(subtitle, style = MaterialTheme.typography.titleMedium)

            Column(modifier = Modifier.padding(8.dp)) {
                Text(description, style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.size(8.dp))

                // todo
                FilledTonalButton(onClick = {}) {
                    Text(buttonName)
                }
            }
        }
    }
}