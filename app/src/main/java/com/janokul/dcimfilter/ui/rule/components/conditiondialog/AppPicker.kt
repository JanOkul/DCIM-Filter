package com.janokul.dcimfilter.ui.rule.components.conditiondialog

import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "Package Select"

data class AppItemInfo(
    val label: String,
    val packageName: String,
    val version: String?,
    val icon: ImageBitmap
)

@Composable
fun AppPicker(
    closePicker: () -> Unit,
    onSelectPackage: (String, String) -> Unit
) {
    val context = LocalContext.current
    val thisPackageName = context.packageName
    val packageManager = context.packageManager
    var installedPackages by remember { mutableStateOf<List<AppItemInfo>>(ArrayList()) }
    var loaded by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            installedPackages = getInstalledPackages(packageManager, thisPackageName)
            loaded = true
        }
    }
    Column{
        Row(verticalAlignment = Alignment.CenterVertically){
            IconButton(onClick = closePicker) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            Text("Select an Application",  style = MaterialTheme.typography.titleLarge)
        }
        AppPickerContent(installedPackages, onSelectPackage, closePicker)
    }
}

@Composable
private fun AppPickerContent(
    installedPackages: List<AppItemInfo>,
    onSelectPackage: (String, String) -> Unit,
    closePicker: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = installedPackages,
            key = { it.packageName }
        ) { item ->
            AppItem(
                item,
                onSelectPackage,
                closePicker
            )
        }
    }
}

@Composable
private fun AppItem(
    item: AppItemInfo,
    onSelectPackage: (String, String) -> Unit,
    closePicker: () -> Unit
) {
    Card {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(onClick = {
                    onSelectPackage(item.packageName, item.label)
                    closePicker()
                    Log.d(TAG, "Selected package: ${item.packageName}")
                })
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

private fun getInstalledPackages(packageManager: PackageManager, thisPackageName: String): List<AppItemInfo> {
    val result = packageManager.queryIntentActivities(
        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
        0
    )

    val installedPackages = result.map {
        val packageInfo = packageManager.getPackageInfo(it?.activityInfo?.packageName ?: "", 0)
        AppItemInfo(
            it.loadLabel(packageManager).toString(),
            it.activityInfo.packageName,
            packageInfo.versionName,
            it.activityInfo.loadIcon(packageManager)
                .toBitmap(48, 48)
                .asImageBitmap()
        )
    }

    return installedPackages
        .filter { it.packageName != thisPackageName}
        .distinctBy { it.packageName }
        .sortedBy { it.label }
}