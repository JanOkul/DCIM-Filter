package com.janokul.dcimfilter.processing.filtering

import android.content.*
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.janokul.dcimfilter.*
import com.janokul.dcimfilter.room.rule.*
import com.janokul.dcimfilter.room.rule.types.ConditionAttribute
import com.janokul.dcimfilter.room.rule.types.value.*

private const val TAG = "ContentRepositoryImpl"

fun Int.toBooleanOrNull(): Boolean? {
    return when (this) {
        1 -> true
        0 -> false
        else -> null
    }
}

class ContentRepositoryImpl(
    private val contentResolver: ContentResolver,
    private val ruleDao: FilterRuleDao
    ): ContentRepository {
    val mediaCollections = listOf(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )

    /**
     * Fetches the relative path of all the content Uris given using Mediastore.
     * @param contentUris An array of content Uris, typically obtained from a content uri trigger.
     * @return A map from the content id to its relative path
     */
    override fun fetchContentPaths(contentUris: Array<Uri>): Map<ContentId, RelativePath> {
        val queryIds = contentUris.map{ ContentUris.parseId(it) }
        val questionMarks = queryIds.joinToString(",") { "?" }

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.RELATIVE_PATH
        )
        val selection = "${MediaStore.MediaColumns._ID} IN ($questionMarks) AND ${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?" // Makes sure content id relative path is within /DCIM/
        val selectionArgs = queryIds.map { it.toString() }.toTypedArray() + DCIM_REL_PATH_SQL
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        val results = mutableMapOf<String, String>()
        for (collection in mediaCollections) {
            contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val relPathIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)

                while (cursor.moveToNext()) {
                    results[cursor.getString(idIndex)] = cursor.getString(relPathIndex)
                }
            }
        }

        return results
    }

    /**
     *  Fetches a set of attributes for a piece of Media.
     *  @param contentId The id of the media within MediaStore.
     *  @param attributes The set of attributes to fetch the value of.
     *  @return A map from each attribute to it's corresponding value.
     */
    override fun fetchContentIdAttributes(
        contentId: ContentId,
        attributes: List<ConditionAttribute>
    ): Map<Attribute, ConditionValue<*>> {
        //todo - (Whole function optimisation), should take in a list of contentId's, and get the attributes for each content Id, in one query rather than n queries.
        val filteredAttributes = attributes.filter { it.queryable }
        // Filter out any columns that may not exist in MediaStore
        val projection = filteredAttributes.map { it.value } .toTypedArray()
        val selection = "${MediaStore.MediaColumns._ID} = ?"
        val selectionArgs = arrayOf(contentId)

        val result = mutableMapOf<String, ConditionValue<*>>()

        for (collection in mediaCollections) {
            contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->

                if (cursor.moveToNext()) {
                    filteredAttributes.forEach { attribute ->
                        val index = cursor.getColumnIndexOrThrow(attribute.value)
                        result[attribute.value] = when (attribute.valueType) {
                            is StringValue.RawStringValue -> StringValue.RawStringValue(cursor.getString(index))
                            is StringValue.PackageValue -> StringValue.PackageValue(cursor.getString(index))
                            is StringValue.DateValue -> StringValue.DateValue(cursor.getString(index))
                            is LongValue -> LongValue(cursor.getString(index))
                            is BoolValue -> BoolValue(cursor.getInt(index).toBooleanOrNull()!!.toString())
                            is SpecialValue.AllValue -> SpecialValue.AllValue()
                            is SpecialValue.NoneValue -> SpecialValue.NoneValue()
                        }

                        Log.d(TAG, "Fetched attribute: ${attribute.value} with value ${result[attribute.value]}")
                    }
                }
            }
        }

        return result
    }

    override fun fetchActiveRules(): List<FilterRule> {
        return ruleDao.getAllEnabled()
    }
}