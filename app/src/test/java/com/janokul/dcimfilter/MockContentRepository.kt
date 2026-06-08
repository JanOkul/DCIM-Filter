package com.janokul.dcimfilter

import android.net.Uri
import com.janokul.dcimfilter.processing.filtering.ContentRepository
import com.janokul.dcimfilter.room.rule.*
import com.janokul.dcimfilter.room.rule.types.*
import com.janokul.dcimfilter.room.rule.types.value.*

/* Default rule that can be copy pasted if needed
    FilterRule(
        id = 0,
        enabled = true,
        fromRelativePath = "DCIM/Camera/",
        toRelativePath = "app0",
        conditions = listOf(
            Condition(
                ConditionAttribute.OWNER_PACKAGE_NAME,
                ConditionOp.EQUALS,
                StringValue.PackageValue("com.pub0.app0")
            )
        )
    )
*/

class MockContentRepository(private val rules: List<FilterRule>): ContentRepository {
    val pathMap = mapOf(
        "0" to "DCIM/Camera/",
        "1" to "DCIM/Camera/",
        "2" to "DCIM/Camera/",
        "3" to "DCIM/Screenshots/",
        "4" to "DCIM/Screenshots/",
        "5" to "DCIM/Screenshots/",
        "6" to "Pictures/App1/",
        "7" to "Pictures/App2/"
    )

    val attributeMap = mapOf(
        "0" to mapOf(
            ConditionAttribute.DISPLAY_NAME to "image0.png",
            ConditionAttribute.OWNER_PACKAGE_NAME to "com.pub0.app0",
            ConditionAttribute.SIZE to "1000",
            ConditionAttribute.IS_FAVOURITE to "true"
        ),
        "1" to mapOf(
            ConditionAttribute.DISPLAY_NAME to "video1.mp4",
            ConditionAttribute.OWNER_PACKAGE_NAME to "com.pub0.app0",
            ConditionAttribute.SIZE to "5000",
            ConditionAttribute.IS_FAVOURITE to "false"
        ),
        "2" to mapOf(
            ConditionAttribute.DISPLAY_NAME to "video2.mp4",
            ConditionAttribute.OWNER_PACKAGE_NAME to "com.pub1.app1",
            ConditionAttribute.SIZE to "8000",
            ConditionAttribute.IS_FAVOURITE to "true"
        ),
        "3" to mapOf(
            ConditionAttribute.DISPLAY_NAME to "screenshot3.png",
            ConditionAttribute.OWNER_PACKAGE_NAME to "com.pub1.app1",
            ConditionAttribute.SIZE to "500",
            ConditionAttribute.IS_FAVOURITE to "false"
        ),
        "4" to mapOf(
            ConditionAttribute.DISPLAY_NAME to "screenshot4.png",
            ConditionAttribute.OWNER_PACKAGE_NAME to "com.pub0.app0",
            ConditionAttribute.SIZE to "5000",
            ConditionAttribute.IS_FAVOURITE to "true"
        ),
        "5" to mapOf(
            ConditionAttribute.DISPLAY_NAME to "screenshot5.png",
            ConditionAttribute.OWNER_PACKAGE_NAME to "com.pub1.app1",
            ConditionAttribute.SIZE to "600",
            ConditionAttribute.IS_FAVOURITE to "false"
        ),
        "6" to mapOf(
            ConditionAttribute.DISPLAY_NAME to "photo6.jpg",
            ConditionAttribute.OWNER_PACKAGE_NAME to "com.pub0.app2",
            ConditionAttribute.SIZE to "3000",
            ConditionAttribute.IS_FAVOURITE to "false"
        ),
        "7" to mapOf(
            ConditionAttribute.DISPLAY_NAME to "photo7.jpg",
            ConditionAttribute.OWNER_PACKAGE_NAME to "com.pub0.app2",
            ConditionAttribute.SIZE to "5000",
            ConditionAttribute.IS_FAVOURITE to "true"
        )
    )



    override fun fetchContentPaths(contentUris: Array<Uri>): Map<ContentId, RelativePath> {
        val paths = mutableMapOf<ContentId, RelativePath>()
        contentUris.forEach { uri ->
            val id = uri.lastPathSegment
            requireNotNull(id) {IllegalStateException("Mock or real URI must have an ID as its lastPathSegment.")}
            paths[id] = (pathMap[id] ?: throw IllegalArgumentException("Id $id not in test set"))
        }

        println("Fetches paths for ${contentUris.map { it.toString() }} URIs: $paths")
        return paths
    }

    override fun fetchContentIdAttributes(
        contentId: ContentId,
        attributes: List<ConditionAttribute>
    ): Map<Attribute, ConditionValue<*>> {
        val attributeMapForId = attributeMap[contentId] ?: throw IllegalArgumentException("Id $contentId not in test set.")
        val result = mutableMapOf<Attribute, ConditionValue<*>>()

        attributes.forEach { attribute ->
            val rawAttribute = attributeMapForId[attribute] ?: throw IllegalArgumentException("Attribute $attribute not in test set")

            result[attribute.value] = when (attribute.valueType) {
                is LongValue -> LongValue(rawAttribute)
                is BoolValue -> BoolValue(rawAttribute)
                is StringValue.PackageValue -> StringValue.PackageValue(rawAttribute)
                is StringValue.RawStringValue -> StringValue.RawStringValue(rawAttribute)
                is StringValue.DateValue -> StringValue.DateValue(rawAttribute)
                else -> throw IllegalArgumentException("Attribute value type ${attribute.valueType} is not queryable")
            }
        }

        println("Fetched Attributes for $contentId: $result")
        return result
    }

    override fun fetchActiveRules(): List<FilterRule> {
        return rules.filter { rule -> rule.enabled }
    }
}