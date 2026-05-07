package com.janokul.dcimfilter.ui.rule.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.janokul.dcimfilter.R
import com.janokul.dcimfilter.ui.rule.RuleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    viewModel: RuleViewModel,
    navController: NavController,
    onLeaveAttempt: () -> Unit
) {
    val title = if (viewModel.isNew) "New Rule" else "Rule - ${viewModel.currentFilterRule.fromRelativePath}"
    var pendingDelete by remember { mutableStateOf(false) }

    if (pendingDelete) {
        DeleteDialog(
            {
                pendingDelete = false
                viewModel.deleteCurrentRule()
                navController.popBackStack()
            },
            {
                pendingDelete = false
            }
        )
    }

    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (viewModel.isDirty)
                IconButton(onClick = {
                    onLeaveAttempt()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.secondary_content_description)
                    )
                }
            else
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.secondary_content_description)
                    )
                }
        },
        actions = {
            IconButton(onClick = { pendingDelete = true }) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete Rule",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            IconButton(
                onClick = {
                    viewModel.saveCurrentRule()
                    navController.popBackStack()
                }, enabled = viewModel.isDirty
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Create Rule",
                    tint = if (viewModel.isDirty) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }
    )
}

@Composable
fun DeleteDialog(onDelete: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text("Delete Confirmation") },
        text = { Text("Are you sure you want to delete this rule? You cannot undo this.") },
        confirmButton = {
            TextButton(onClick = { onDelete() }) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text("Cancel")
            }
        },
    )
}