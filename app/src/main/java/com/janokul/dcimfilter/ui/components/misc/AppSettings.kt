package com.janokul.dcimfilter.ui.components.misc

data class AppSettings(
    val isEnabled: Boolean,
    val sourcePackage: String,
    val destinationFolder: String,
    val timeoutNotification: Boolean
)
