package com.janokul.dcimfilter.room.rule.types

import android.provider.MediaStore

//todo add all the columns appropriate for the app
enum class RuleKeys(val value: String) {
    OWNER_PACKAGE_NAME(MediaStore.MediaColumns.OWNER_PACKAGE_NAME),
    FILTER_ALL("")
}