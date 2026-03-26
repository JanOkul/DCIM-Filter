package com.example.dcimfilter.ui.components.misc

import android.content.Context
import android.os.Environment
import android.os.PowerManager

fun hasAllFileAccess(): Boolean {
    return Environment.isExternalStorageManager()
}

fun hasUnrestrictedBattery(context: Context): Boolean {
    val manager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return manager.isIgnoringBatteryOptimizations(context.packageName)
}