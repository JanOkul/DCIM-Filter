package com.example.dcimfilter

const val NOTIFICATION_CHANNEL = "DCIM_FILTER_CHANNEL"

enum class NotificationIds(val id: Int) {
    FOREGROUND_SERVICE(1),
    BATCH_SCANNER(2)
}

enum class NavNames(val id: String) {
    MAIN("main"),
    PACKAGE_SELECT("package_select"),
    HISTORY("history")
}