package com.janokul.dcimfilter.ui.screens

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.janokul.dcimfilter.R
import com.janokul.dcimfilter.room.FilterDB
import com.janokul.dcimfilter.room.history.History
import com.janokul.dcimfilter.ui.components.misc.HistoryViewModel
import com.janokul.dcimfilter.ui.components.ui.SecondaryAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val TAG = "HistoryScreen"
@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val dao = FilterDB.getInstance(context).historyDao
    val viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.factory(dao))
    val historyItems = viewModel.historyPaged.collectAsLazyPagingItems()
    var totalItems by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            totalItems = dao.getCount()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { SecondaryAppBar(navController, stringResource(R.string.history_name)) }
    ) { innerPadding ->
        HistoryContent(innerPadding, historyItems, totalItems)
    }
}

@Composable
private fun HistoryContent(
    innerPadding: PaddingValues,
    historyItems: LazyPagingItems<History>,
    count: Int
) {
    Column(
        Modifier
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        if (count > 0) {
            HistoryCardNotEmpty(historyItems)
        } else {
            HistoryCardEmpty()
        }
    }
}

@Composable
private fun HistoryCardNotEmpty(historyItems: LazyPagingItems<History>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            count = historyItems.itemCount,
            key = historyItems.itemKey { it.id }
        ) { index ->
            val item = historyItems[index]
            item?.let {
                HistoryItem(it)
            }
        }
    }
}

@Composable
private fun HistoryCardEmpty() {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.HistoryToggleOff,
            contentDescription = null,
            Modifier.size(32.dp)
        )

        Spacer(Modifier.size(8.dp))
        Text(
            stringResource(R.string.history_empty),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun HistoryItem(item: History) {
    val context = LocalContext.current

    Card(
        onClick = {
            openMedia(context, item.uriId, item.mimeType)
        }
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(item.filename, style = MaterialTheme.typography.titleSmall)

            Row {
                Text(
                    stringResource(R.string.history_description, item.movedTo),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    formatTimestamp(item.movedAt),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter: DateTimeFormatter? = DateTimeFormatter
        .ofPattern("dd/MM/yy HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    return formatter?.format(Instant.ofEpochMilli(timestamp)) ?: ""
}

private fun openMedia(context: Context, id: Long, mime: String) {
    val intent = Intent(Intent.ACTION_VIEW)
        .setDataAndType(
            ContentUris.withAppendedId(
                MediaStore.Files.getContentUri("external"),
                id
            ),
            mime
        ).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Log.d(TAG, "Failed to open $id, reason: $e")
    }
}