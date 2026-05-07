package com.janokul.dcimfilter.ui.rule


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.janokul.dcimfilter.ui.rule.components.AppBar
import com.janokul.dcimfilter.ui.rule.components.ConditionSelection
import com.janokul.dcimfilter.ui.rule.components.PathSelection
import com.janokul.dcimfilter.ui.rule.components.conditiondialog.ConditionDialog


private const val TAG = "RuleScreen"
@Composable
fun RuleScreen(navController: NavController, viewModel: RuleViewModel = hiltViewModel()) {
    var showExitDialog by remember { mutableStateOf(false) }
    var showConditionDialog by remember { mutableStateOf(false) }
    var conditionDialogIndex by remember { mutableIntStateOf(-1) }
    val clearConditionDialog = { showConditionDialog = false; conditionDialogIndex = -1 }

    BackHandler(enabled = viewModel.isDirty) {
        showExitDialog = true
    }

    if (showExitDialog && viewModel.isDirty) {
        ExitDialog(
            onStay = { showExitDialog = false },
            onLeave = { showExitDialog = false; navController.popBackStack() }
        )
    }

    if (showConditionDialog) {
        ConditionDialog(
            condition = viewModel.currentFilterRule.conditions.getOrNull(conditionDialogIndex),
            dismissModal = clearConditionDialog,
            saveCondition = { if (conditionDialogIndex == -1) viewModel.addCondition(it) else viewModel.updateCondition(conditionDialogIndex, it) },
            // Auto saves package name to package label name
            autoSetToPath = {viewModel.updateCurrentRule(viewModel.currentFilterRule.copy(toRelativePath = it)) }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar(viewModel, navController) { showExitDialog = true } },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("New Condition") },
                icon = { Icon( Icons.Default.Add, contentDescription = "Create new rule") },
                onClick = { showConditionDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PathSelection(viewModel)
            ConditionSelection(
                viewModel.currentFilterRule.conditions,
                { conditionDialogIndex = it; showConditionDialog = true },
                { viewModel.deleteCondition(it) }
            )
        }
    }
}

// Should be shown when the user tries to exit the rule page without saving content
@Composable
fun ExitDialog(onLeave: () -> Unit, onStay: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onStay() },
        title = { Text("Unsaved Changes") },
        text = { Text("Your changes won't be saved.") },
        confirmButton = {
            TextButton(onClick = { onLeave() }) {
                Text("Leave")
            }
        },
        dismissButton = {
            TextButton(onClick = { onStay() }) {
                Text("Stay")
            }
        },
    )
}
