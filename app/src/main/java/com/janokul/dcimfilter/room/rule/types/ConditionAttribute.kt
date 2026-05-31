package com.janokul.dcimfilter.room.rule.types

import android.provider.MediaStore
import com.janokul.dcimfilter.room.rule.types.ConditionValue.LongValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.SpecialValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.StringValue
import kotlinx.serialization.Serializable

//todo add all the columns appropriate for the app
@Serializable
enum class ConditionAttribute(val value: String, val displayName: String, val valueType: ConditionValue<*>, val queryable: Boolean) {
    FILTER_NONE(
        value = "filter_none",
        displayName = "Filter None",
        valueType = SpecialValue.NoneValue,
        queryable = false
    ),

    FILTER_ALL(
        value = "filter_all",
        displayName = "Filter All",
        valueType = SpecialValue.AllValue,
        queryable = false
    ),

    DISPLAY_NAME(
        value = MediaStore.MediaColumns.DISPLAY_NAME,
        displayName = "Display Name (Filename)",
        valueType = StringValue.RawStringValue(),
        queryable = true
    ),

    OWNER_PACKAGE_NAME(
        value = MediaStore.MediaColumns.OWNER_PACKAGE_NAME,
        displayName = "Owner Package Name",
        valueType = StringValue.PackageValue(),
        queryable = true
    ),

    SIZE(
        value = MediaStore.MediaColumns.SIZE,
        displayName = "Size",
        valueType = LongValue(),
        queryable = true
    )
}
