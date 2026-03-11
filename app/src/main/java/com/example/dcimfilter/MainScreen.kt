package com.example.dcimfilter

import android.content.Context
import android.media.MediaScannerConnection
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


/**
 *  The parent composable for the entire UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val appName = stringResource(R.string.app_name)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(appName, style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { innerPadding ->
        MainBody(innerPadding)
    }
}

/**
 *  The main screen content composable
 *  @param innerPadding The padding values for the content
 */
@Composable
fun MainBody(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilterCard()
        SettingsCard()
    }
}

/**
 *  The filter card UI to be displayed in the main screen.
 */
@Composable
fun FilterCard() {
    val subtitle = stringResource(R.string.batch_filter_subtitle)
    val description = stringResource(R.string.batch_filter_description)
    val buttonName = stringResource(R.string.batch_filter_button_name)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(subtitle, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.size(8.dp))

            // todo
            FilledTonalButton(onClick = {}) {
                Text(buttonName)
            }
        }
    }
}




// IMPORTANT FOR FILTERING - DO NOT DELETE
//fun createAndScanTestFile(context: Context) {
//    val downloads = android.os.Environment.getExternalStoragePublicDirectory(
//        android.os.Environment.DIRECTORY_DOWNLOADS
//    )
//    val testFile = java.io.File(downloads, "test_fresh.jpg")
//    testFile.writeText("fake image content") // just to create it
//
//    MediaScannerConnection.scanFile(
//        context,
//        arrayOf(testFile.absolutePath),
//        arrayOf("image/jpeg")
//    ) { path, uri ->
//        Log.d("MediaStoreTest", "Fresh file scanned: $path -> $uri")
//    }
//}
//
//fun debugAllImages(context: Context) {
//    val projection = arrayOf(
//        MediaStore.MediaColumns.DISPLAY_NAME,
//        MediaStore.MediaColumns.OWNER_PACKAGE_NAME
//    )
//
//    context.contentResolver.query(
//        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // Camera roll, DCIM etc
//        projection, null, null, null
//    )?.use { cursor ->
//        Log.d("MediaStoreTest", "Total images found: ${cursor.count}")
//        while (cursor.moveToNext()) {
//            Log.d("MediaStoreTest", "File: ${cursor.getString(0)} | Owner: ${cursor.getString(1)}")
//        }
//    }
//}



