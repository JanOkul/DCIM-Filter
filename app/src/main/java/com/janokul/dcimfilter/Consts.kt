package com.janokul.dcimfilter

const val NOTIFICATION_CHANNEL = "DCIM_FILTER_CHANNEL"

const val USER_PREFS_NAME = "settings"
const val PREFS_IS_ENABLED = "is_enabled"
const val PREFS_SOURCE_PACKAGE = "source_package"
const val PREFS_DESTINATION_FOLDER = "destination_folder"
const val PREFS_TIMEOUT_NOTIFICATION = "timeout_notification"

const val WORKER_ID = "file_mover"
const val WORK_DATA_ID = "uri_id"

const val DB_NAME = "dcimFilter.db"

enum class NotificationIds(val id: Int) {
    FOREGROUND_SERVICE(1),
    BATCH_SCANNER(2),
    SERVICE_TIMEOUT(3)
}

enum class NavNames(val id: String) {
    MAIN("main"),
    PACKAGE_SELECT("package_select"),
    HISTORY("history"),
    RULE("rule")
}

const val RULE_ID = "ruleId"
