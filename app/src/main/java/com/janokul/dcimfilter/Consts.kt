package com.janokul.dcimfilter

const val NOTIFICATION_CHANNEL = "DCIM_FILTER_CHANNEL"

const val USER_PREFS_NAME = "settings"
const val PREFS_IS_ENABLED = "is_enabled"
const val PREFS_SOURCE_PACKAGE = "source_package"
const val PREFS_DESTINATION_FOLDER = "destination_folder"

enum class NotificationIds(val id: Int) {
    FOREGROUND_SERVICE(1),
    BATCH_SCANNER(2)
}

enum class NavNames(val id: String) {
    MAIN("main"),
    PACKAGE_SELECT("package_select"),
    HISTORY("history")
}

enum class WorkerIds(val id: String) {
    SINGLE("single_file_move"),
    BATCH("batch_file_move")
}

