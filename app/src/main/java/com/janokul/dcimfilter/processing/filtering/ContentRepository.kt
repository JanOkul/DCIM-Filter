package com.janokul.dcimfilter.processing.filtering

import android.net.Uri
import com.janokul.dcimfilter.Attribute
import com.janokul.dcimfilter.ContentId
import com.janokul.dcimfilter.RelativePath
import com.janokul.dcimfilter.room.rule.FilterRule
import com.janokul.dcimfilter.room.rule.types.ConditionAttribute
import com.janokul.dcimfilter.room.rule.types.value.ConditionValue

interface ContentRepository {
    fun fetchContentPaths(contentUris: Array<Uri>): Map<ContentId, RelativePath>
    fun fetchContentIdAttributes(contentId: ContentId, attributes: List<ConditionAttribute>): Map<Attribute, ConditionValue<*>>
    fun fetchActiveRules(): List<FilterRule>
}