package com.janokul.dcimfilter.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.FilterNone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.janokul.dcimfilter.NavNames
import com.janokul.dcimfilter.R
import com.janokul.dcimfilter.room.rule.FilterRule
import com.janokul.dcimfilter.ui.components.ui.InsufficientPermissionsCard


/**
 *  The parent composable for the entire UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var allFileAccess by remember { mutableStateOf(Environment.isExternalStorageManager()) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        allFileAccess = Environment.isExternalStorageManager()
    }

    if (!allFileAccess) {
        val onAccept = {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = "package:${context.packageName}".toUri()
            }
            context.startActivity(intent)
        }

        PermissionDialog(
            stringResource(R.string.permission_storage_title),
            stringResource(R.string.permission_storage_description),
            onAccept
        ) { (context as? Activity)?.finishAffinity() }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar(navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("New Rule") },
                icon = { Icon( Icons.Default.Add, contentDescription = "Create new rule") },
                onClick = { viewModel.createNewRulePage(navController) }
            )
        }
    ) { innerPadding ->
        if (Environment.isExternalStorageManager()) {
            MainBody(innerPadding, navController, viewModel)
        } else {
            InsufficientPermissionsCard(innerPadding)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(navController: NavController) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge) },
        actions = {
            IconButton(onClick = { navController.navigate(NavNames.HISTORY.id) }) {
                Icon(
                    Icons.Default.History,
                    contentDescription = stringResource(R.string.primary_content_description)
                )
            }
        }
    )
}


/**
 *  The main screen content composable
 *  @param innerPadding The padding values for the content
 */
@Composable
private fun MainBody(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: MainViewModel
) {
    val rules by viewModel.rules.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BatchFilterAllCard()
        Spacer(Modifier.size(8.dp))
        Text("Rules", style = MaterialTheme.typography.titleLarge)
        if (rules.isEmpty()) {
            NoRules(viewModel, navController)
        } else {
            Rules(navController, viewModel, rules)
        }


    }
}

@Composable
fun BatchFilterAllCard() {
    Card(
        Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Batch Filter", style = MaterialTheme.typography.titleLarge)
            Text("[Description of what this does]", style = MaterialTheme.typography.bodyMedium)

            Button(onClick = {/*todo add batch filter functionality*/} ) {
                Text("Filter All")
            }

        }
    }

}

@Composable
fun NoRules(viewModel: MainViewModel, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Outlined.FilterNone,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "No Rules Added",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Text(
                text = "To filter out files from the DCIM folder, you must define rules on how they should be filtered.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Button(
                onClick = { viewModel.createNewRulePage(navController) }
            ) {
                Text("Create First Rule")
            }
        }
    }
}

@Composable
fun Rules(
    navController: NavController,
    viewModel: MainViewModel,
    rules: List<FilterRule>
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = rules,
            key = { it.id }
        ) { item -> FilterRuleEntry(item, navController, viewModel) }
    }
}

@Composable
fun FilterRuleEntry(filterRule: FilterRule, navController: NavController, viewModel: MainViewModel) {
    Card(modifier = Modifier
        .fillMaxSize(),
        onClick = {navController.navigate("${NavNames.RULE.id}/${filterRule.id}")}
    ) {
        Row(modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Column {
                Text(filterRule.fromRelativePath, style = MaterialTheme.typography.titleMedium)
                Text("Conditions: ${filterRule.rules.size}")
            }
            Switch(
                checked = filterRule.enabled,
                onCheckedChange = { viewModel.changeEnabledState(filterRule.id) }
            )
        }
    }
}

@Composable
private fun PermissionDialog(
    title: String,
    description: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDecline() },
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            TextButton(onClick = { onAccept() }) {
                Text(stringResource(R.string.permission_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDecline() }) {
                Text(stringResource(R.string.permission_cancel))
            }
        }
    )
}




