package com.janokul.dcimfilter.ui.main

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import kotlinx.coroutines.launch


/**
 *  The parent composable for the entire UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var allFileAccess by remember { mutableStateOf(hasAllFileAccess()) }
    var unrestrictedBattery by remember { mutableStateOf(hasUnrestrictedBattery(context)) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        allFileAccess = hasAllFileAccess()
        unrestrictedBattery = hasUnrestrictedBattery(context)
    }

    if (!unrestrictedBattery) {
        val onAccept = {
            context.startActivity(
                Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            )
        }

        val onDecline = {
            (context as? Activity)?.finishAffinity()
            Unit
        }

        PermissionDialog(
            stringResource(R.string.permission_battery_title),
            stringResource(R.string.permission_battery_description),
            onAccept,
            onDecline
        )
    }

    if (!allFileAccess) {
        val onAccept = {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = "package:${context.packageName}".toUri()
            }
            context.startActivity(intent)
        }

        val onDecline = {
            (context as? Activity)?.finishAffinity()
            Unit
        }

        PermissionDialog(
            stringResource(R.string.permission_storage_title),
            stringResource(R.string.permission_storage_description),
            onAccept,
            onDecline
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { PrimaryAppBar(navController) }
    ) { innerPadding ->
        if (hasAllFileAccess() && hasUnrestrictedBattery(context)) {
            MainBody(innerPadding, navController, viewModel)
        } else {
            InsufficientPermissionsCard(innerPadding)
        }
    }
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (rules.isEmpty()) {
            NoRules(viewModel, navController)
        } else {
            RulesCard(navController, rules)
        }

    }
}

@Composable
fun NoRules(viewModel: MainViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    Card {
        Text("No Rules Added, Create your first!")
        TextButton(onClick = {
            scope.launch {
                val id = viewModel.newRuleBlank()
                navController.navigate("${NavNames.RULE.id}/$id")
            }
        }) {
            Text("Create First Rule")
        }
    }
}

@Composable
fun RulesCard(navController: NavController, rules: List<FilterRule>) {
    LazyColumn {
        items(
            items = rules,
            key = { it.id }
        ) { item ->
            Card(modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp),
                onClick = {navController.navigate("${NavNames.RULE.id}/${item.id}")}
            ) {
                Text(item.fromRelativePath, style = MaterialTheme.typography.titleMedium)
            }
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
    val accept = stringResource(R.string.permission_ok)
    val decline = stringResource(R.string.permission_cancel)

    AlertDialog(
        onDismissRequest = { onDecline() },
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            TextButton(onClick = { onAccept() }) {
                Text(accept)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDecline() }) {
                Text(decline)
            }
        }
    )
}




