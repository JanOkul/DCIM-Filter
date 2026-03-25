package com.example.dcimfilter.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dcimfilter.room.FilterDB
import com.example.dcimfilter.room.history.History
import com.example.dcimfilter.ui.components.ui.SecondaryAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//todo add paging to history items
@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val dao by lazy { FilterDB.getInstance(context).historyDao }
    var historyItems by remember { mutableStateOf<List<History>?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            historyItems = dao.getHistory()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { SecondaryAppBar(navController, "History") }
    ) { innerPadding ->
        if (historyItems != null) {
            HistoryContent(innerPadding, historyItems!!)
        } else {
            Text("uh oh") //todo fix whatever this is
        }
    }
}

@Composable
fun HistoryContent(innerPadding: PaddingValues, historyItems: List<History>) {
    Column(
        Modifier.padding(innerPadding).padding(16.dp)
    ) {
        if (historyItems.isNotEmpty()) {
            HistoryCardNotEmpty(historyItems)
        } else {
            HistoryCardEmpty()
        }
    }
}

@Composable
fun HistoryCardNotEmpty(historyItems: List<History>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = historyItems,
            key = { it.id }
        ) {
            HistoryItem(it)
        }
    }
}


@Composable
fun HistoryCardEmpty() {
    Column(
        Modifier.fillMaxWidth()
            .fillMaxHeight()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.HistoryToggleOff,
            contentDescription = "No history log icon.",
            Modifier.size(32.dp)

            )

        Spacer(Modifier.size(8.dp))
        Text(
            "Nothing logged yet",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun HistoryItem(item: History) {
    Card {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(item.filename, style = MaterialTheme.typography.titleSmall)

            // todo add click to show media functionality
            Row {
                Text(
                    "File moved to ${item.movedTo}",
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

fun formatTimestamp(timestamp: Long): String {
    val formatter: DateTimeFormatter? = DateTimeFormatter
        .ofPattern("dd/MM/yy HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    return formatter?.format(Instant.ofEpochMilli(timestamp)) ?: ""
}