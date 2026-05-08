package com.janokul.dcimfilter.ui.rule.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.janokul.dcimfilter.room.rule.Condition

/**
 * Ui to allow user to configure rules for media whithin the fromPath
 */
@Composable
fun ConditionSelection(
    conditions: List<Condition>,
    onShortClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit
) {
    var pendingDeletion by remember { mutableStateOf<Int?>(null) }

    pendingDeletion?.let {
        ConfirmDeletionDialog(
            onDismiss = { pendingDeletion = null },
            onConfirm = { onLongClick(it); pendingDeletion = null }
        )
    }

    CollapsibleCard(
        initiallyCollapsed = false,
        title = { Text("Rule Selection", style = MaterialTheme.typography.titleLarge) },
        collapsedContent = {
        },
        expandedContent = {
            Column {
                Text(
                    "Media can only be moved if the following below conditions are ALL met. If there are no conditions then nothing will be moved.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.size(16.dp))

                if (conditions.isEmpty()) {
                    Text("No Conditions yet!")
                } else {
                    conditions.forEachIndexed { index, condition ->
                        ListItem(
                            headlineContent = { Text(condition.attribute.displayName) },
                            supportingContent = { Text(condition.op.op) },
                            trailingContent = { Text(condition.value.value.toString()) },
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .combinedClickable(
                                    onClick = { onShortClick(index) },
                                    onLongClick = { pendingDeletion = index }
                                )
                        )
                        if (index < conditions.lastIndex) Spacer(Modifier.size(4.dp))
                    }
                }
            }
        }
    )
}

@Composable
private fun ConfirmDeletionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete this? This action cannot be undone.") },
        onDismissRequest = onDismiss,
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Delete") }}
    )
}

