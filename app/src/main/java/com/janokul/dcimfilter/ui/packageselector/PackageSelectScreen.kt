package com.janokul.dcimfilter.ui.packageselector

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.janokul.dcimfilter.R
import com.janokul.dcimfilter.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "Package Select"

data class AppItemInfo(
    val label: String,
    val packageName: String,
    val version: String?,
    val icon: ImageBitmap
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageSelectScreen(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    var installedPackages by remember { mutableStateOf<List<AppItemInfo>>(ArrayList()) }
    var loaded by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            installedPackages = getInstalledPackages(packageManager)
            loaded = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar(navController, stringResource(R.string.package_select_name)) }
    ) { innerPadding ->
        if (!loaded) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            PackageSelectContent(
                context,
                innerPadding,
                viewModel,
                navController,
                installedPackages
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    navController: NavController,
    appName: String = stringResource(R.string.app_name)
) {
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

@Composable
private fun PackageSelectContent(
    context: Context,
    innerPadding: PaddingValues,
    viewModel: SettingsViewModel,
    navController: NavController,
    installedPackages: List<AppItemInfo>
) {

    LazyColumn(
        Modifier
            .padding(paddingValues = innerPadding)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = installedPackages,
            key = { it.packageName }
        ) { item ->
            // Skip any null packages, and also skip self
            if (context.packageName != item.packageName) {
                AppItem(
                    viewModel,
                    navController,
                    item
                )
            }
        }
    }
}

@Composable
private fun AppItem(
    viewModel: SettingsViewModel,
    navController: NavController,
    item: AppItemInfo
) {
    Card {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(onClick = {
                    viewModel.setSourcePackage(item.packageName)
                    viewModel.setDestinationFolder(item.label)
                    Log.d(TAG, "Selected package: ${item.packageName}")
                    navController.popBackStack()
                }
                )
        ) {
            Image(
                bitmap = item.icon,
                contentDescription = "App icon for ${item.label}",
                Modifier.size(48.dp)
            )
            Column(Modifier.padding(start = 8.dp, end = 8.dp)) {
                Text(item.label, style = MaterialTheme.typography.titleSmall)
                Text(item.packageName, style = MaterialTheme.typography.bodySmall)
                Text(item.version ?: "", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun getInstalledPackages(packageManager: PackageManager): List<AppItemInfo> {
    val installedPackages = packageManager.queryIntentActivities(
        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
        0
    )

    return installedPackages.map {
        val packageInfo = packageManager.getPackageInfo(it?.activityInfo?.packageName ?: "", 0)
        AppItemInfo(
            it.loadLabel(packageManager).toString(),
            it.activityInfo.packageName,
            packageInfo.versionName,
            it.activityInfo.loadIcon(packageManager)
                .toBitmap(48, 48)
                .asImageBitmap()
        )
    }.distinctBy { it.packageName }
        .sortedBy { it.label }
}