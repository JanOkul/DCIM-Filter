package com.janokul.dcimfilter.ui.rule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.janokul.dcimfilter.room.rule.Rule
import com.janokul.dcimfilter.ui.main.SecondaryAppBar

private const val TAG = "RuleScreen"
@Composable
fun RuleScreen(navController: NavController, viewModel: RuleViewModel = hiltViewModel()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { SecondaryAppBar(navController, "Rule - ${viewModel.ruleId}") }
    ) { innerPadding ->
        RuleBody(innerPadding)
    }
}

@Composable
fun RuleBody(innerPadding: PaddingValues) {
    Column(modifier = Modifier.padding(innerPadding)) {
        PathSelection()
        RuleSelection(emptyList())
    }
}

/**
 * Ui to allow user to configure the from and to path settings of a filter rule.
 */
@Composable
fun PathSelection() {
    var fromPath by remember { mutableStateOf("") }
    Card {
        RuleComponent {
            Column {
                Text("From Path", style = MaterialTheme.typography.titleMedium)
                Text("All files within this path will be filtered to the destination path", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = fromPath,
                    onValueChange = { fromPath = it },
                    label = { Text("Current Path") }
                )
            }
        }

        var toPath by remember { mutableStateOf("") }
        RuleComponent {
            Column {
                Text("To Path", style = MaterialTheme.typography.titleMedium)
                Text("Name of the folder you want to move files to. A folder will be created in Pictures/$toPath and Movies/$toPath for photo and video content. ", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = toPath,
                    onValueChange = { toPath = it },
                    label = { Text("Current Path") }
                )
            }
        }
    }
}

/**
 *  A standardised container for each setting in the application.
 *  @param composable The composable UI element that will be within a setting.
 */
@Composable
private fun RuleComponent(composable: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        composable()
    }
}

/**
 * Ui to allow user to configure rules for media whithin the fromPath
 */
@Composable
fun RuleSelection(rules: List<Rule>) {
    Card {
        RuleComponent {
            Column {
                Text("Rule Selection", style = MaterialTheme.typography.titleMedium)
                Text("Each rule can be customised such that a file is only eligible for filtering if it all rules entered below evaluate to true for a file.", style = MaterialTheme.typography.bodyMedium)
                Column(
                    Modifier.verticalScroll(rememberScrollState())
                ) {

                }
            }
        }
    }
}
