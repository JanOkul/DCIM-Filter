package com.example.dcimfilter.ui.components.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.dcimfilter.NavNames
import com.example.dcimfilter.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimaryAppBar(navController: NavController) {
    val appName = stringResource(R.string.app_name)
    TopAppBar(
        title = { Text(appName, style = MaterialTheme.typography.titleLarge) },
        actions = {
            IconButton( onClick = { navController.navigate(NavNames.HISTORY.id) }) {
                Icon(
                    Icons.Default.History,
                    contentDescription = stringResource(R.string.primary_content_description)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondaryAppBar(navController: NavController, appName: String = stringResource(R.string.app_name)) {
    TopAppBar(
        title = { Text(appName, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.secondary_content_description)
                )
            }
        }
    )
}