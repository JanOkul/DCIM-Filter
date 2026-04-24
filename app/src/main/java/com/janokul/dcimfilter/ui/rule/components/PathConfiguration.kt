package com.janokul.dcimfilter.ui.rule.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.janokul.dcimfilter.ui.rule.RuleViewModel

/**
 * Ui to allow user to configure the from and to path settings of a filter rule.
 */
@Composable
fun PathSelection(viewModel: RuleViewModel) {
    val fromPath = viewModel.currentFilterRule.fromRelativePath
    val toPath = viewModel.currentFilterRule.toRelativePath
    CollapsibleCard(
        {
            Text("Path Configuration", style = MaterialTheme.typography.titleLarge)
        },
        {
            Row {
                Text("From Path: ", style = MaterialTheme.typography.titleSmall,)
                Text(fromPath, style = MaterialTheme.typography.bodyMedium)
            }

            Row {
                Text("To Path: ", style = MaterialTheme.typography.titleSmall)
                Text(toPath, style = MaterialTheme.typography.bodyMedium)
            }
        },
        {
            PathComponent(
                fromPath,
                "From Path",
                "All files within this path will be filtered to the destination path",
                {
                    viewModel.updateCurrentRule(
                        viewModel.currentFilterRule.copy(fromRelativePath = it)
                    )
                }
            )

            PathComponent(
                toPath,
                "To Path",
                "Name of the folder you want to move files to. A folder will be created in Pictures/$toPath and Movies/$toPath for photo and video content.",
                {
                    viewModel.updateCurrentRule(
                        viewModel.currentFilterRule.copy(toRelativePath = it)
                    )
                }
            )
        }
    )
}

/**
 *  A template for each component that controls the routing of files.
 *  @param value The value currently displayed within the box
 *  @param title A title for the component
 *  @param description A description of what the component controls.
 *  @param onTextChange A callback for when the path text changes
 */
@Composable
private fun PathComponent(
    value: String,
    title: String,
    description: String,
    onTextChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Column(Modifier.padding(4.dp)) {
                Text(description, style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value,
                    onValueChange = { onTextChange(it) },
                    label = { Text("Current Path") }
                )
            }
        }
    }
}