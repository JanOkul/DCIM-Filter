package com.janokul.dcimfilter.room.rule.types

import android.provider.MediaStore
import com.janokul.dcimfilter.room.rule.types.ConditionValue.BoolValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.LongValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.StringValue
import kotlinx.serialization.Serializable

//todo add all the columns appropriate for the app
@Serializable
enum class ConditionAttribute(val value: String, val displayName: String, val valueType: ConditionValue<*>) {
    FILTER_NONE("filter_none", "Filter None", BoolValue()),
    FILTER_ALL("filter_all", "Filter All", BoolValue(value = true)),
    OWNER_PACKAGE_NAME(MediaStore.MediaColumns.OWNER_PACKAGE_NAME, "Owner Package Name", StringValue.PackageValue()),
    SIZE(MediaStore.MediaColumns.SIZE, "Size", LongValue())
}