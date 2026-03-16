package com.example.dcimfilter.features.main.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NoStorageAccessCard(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally


    ) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Column (modifier = Modifier.padding(16.dp)) {
                Text(
                    "Storage Access Required",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Please restart app and enable full storage access",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}