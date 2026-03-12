package com.example.dcimfilter

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
import com.example.dcimfilter.ui_components.FilterCard
import com.example.dcimfilter.ui_components.SettingsCard


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



