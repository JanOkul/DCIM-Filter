package com.janokul.dcimfilter.ui.rule.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Ui to allow user to configure the from and to path settings of a filter rule.
 */
@Composable
fun PathSelection() {
    Card {
        Column(
            Modifier.Companion
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Path Configuration", style = MaterialTheme.typography.titleLarge)
            var fromPath by remember { mutableStateOf("") }
            PathComponent(
                fromPath,
                "From Path",
                "All files within this path will be filtered to the destination path"
            )

            var toPath by remember { mutableStateOf("") }
            PathComponent(
                toPath,
                "To Path",
                "Name of the folder you want to move files to. A folder will be created in Pictures/$toPath and Movies/$toPath for photo and video content."
            )
        }
    }
}

/**
 *  A template for each component that controls the routing of files.
 *  @param value The value currently displayed within the box
 *  @param title A title for the component
 *  @param description A description of what the component controls.
 */
@Composable
private fun PathComponent(value: String, title: String, description: String) {
    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Column(Modifier.Companion.padding(4.dp)) {
                Text(description, style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = value,
                    onValueChange = { it },
                    label = { Text("Current Path") }
                )
            }
        }
    }
}