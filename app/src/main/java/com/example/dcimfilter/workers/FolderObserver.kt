package com.example.dcimfilter.workers

import android.os.FileObserver
import java.io.File

class FolderObserver(
    searchDir: File,
    private val handler: (String?) -> Unit
): FileObserver(
    searchDir,
    CLOSE_WRITE
) {

    override fun onEvent(event: Int, path: String?) {
        handler(path)
    }

}