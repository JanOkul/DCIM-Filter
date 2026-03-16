package com.example.dcimfilter.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dcimfilter.R
import com.example.dcimfilter.settings.SettingsViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val TAG = "Settings"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageSelectScreen(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    val appName = stringResource(R.string.app_name)
    val context = LocalContext.current
    val packageManager = context.packageManager
    val installedPackages = packageManager.queryIntentActivities(
        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
        .map{ it.activityInfo }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(appName, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        PackageSelectContent(
            context,
            innerPadding,
            viewModel,
            navController,
            packageManager,
            installedPackages
        )
    }
}

@Composable
fun PackageSelectContent(
    context: Context,
    innerPadding: PaddingValues,
    viewModel: SettingsViewModel,
    navController: NavController,
    pm: PackageManager,
    installedPackages: List<ActivityInfo?>
) {
    LazyColumn(Modifier.padding(paddingValues = innerPadding)) {
        items(installedPackages.size) {
            val item = installedPackages[it]
            val packageInfo = pm.getPackageInfo(item?.packageName ?: "", 0)
            val version = packageInfo.versionName
            val label =  item?.loadLabel(pm).toString()
            val packageName = item?.packageName

            // Skip any null packages, and also skip self
            if (item != null && context.packageName != packageName) {
                AppItem(viewModel,
                    navController,
                    label,
                    packageName?: "N/A",
                    item,
                    pm,
                    version
                )
            }

            if (it < installedPackages.size) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
fun AppItem(
    viewModel: SettingsViewModel,
    navController: NavController,
    label: String,
    packageName: String,
    appInfo: ActivityInfo,
    pm: PackageManager,
    version: String?
) {

    var icon by remember { mutableStateOf<Drawable?>(null) }
    LaunchedEffect(packageName) {
        withContext(Dispatchers.IO) {
            icon = appInfo.loadIcon(pm)
        }
    }

    Row(
        Modifier.padding(8.dp)
            .clickable( onClick = {
                    viewModel.setSelectedPackage(packageName)
                    viewModel.setDestinationFolder(label)
                    Log.d(TAG, "Selected package: $packageName")
                    navController.popBackStack()
                }
            )
    ) {
        Image(
            painter = rememberDrawablePainter(icon),
            contentDescription = "App icon for $label",
            Modifier.size(64.dp)
        )
        Column(Modifier.padding(start = 8.dp)) {
            Text(label, style = MaterialTheme.typography.titleSmall)
            Text(packageName, style = MaterialTheme.typography.bodySmall)
            Text(version ?: "N/A", style = MaterialTheme.typography.bodySmall)
        }
    }
}