package com.janokul.dcimfilter.ui.rule


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.janokul.dcimfilter.ui.rule.components.AppBar
import com.janokul.dcimfilter.ui.rule.components.PathSelection
import com.janokul.dcimfilter.ui.rule.components.RuleSelection


private const val TAG = "RuleScreen"
@Composable
fun RuleScreen(navController: NavController, viewModel: RuleViewModel = hiltViewModel()) {
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = viewModel.isDirty) {
        showExitDialog = true
    }

    if (showExitDialog && viewModel.isDirty) {
        ExitDialog(
            onStay = { showExitDialog = false },
            onLeave = { showExitDialog = false; navController.popBackStack() }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar(viewModel, navController) { showExitDialog = true } }
    ) { innerPadding ->
        RuleBody(innerPadding, viewModel)
    }
}

@Composable
fun RuleBody(innerPadding: PaddingValues, viewModel: RuleViewModel) {
    Column(
        Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PathSelection(viewModel)
        RuleSelection(emptyList())
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
