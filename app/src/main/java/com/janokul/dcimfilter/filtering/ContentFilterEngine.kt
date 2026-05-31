package com.janokul.dcimfilter.filtering

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateListOf
import com.janokul.dcimfilter.DCIM_REL_PATH_SQL
import com.janokul.dcimfilter.room.rule.FilterRule
import com.janokul.dcimfilter.room.rule.types.ConditionAttribute
import com.janokul.dcimfilter.room.rule.types.ConditionValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.BoolValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.LongValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.SpecialValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.StringValue

class ContentFilterEngine(context: Context, rules: List<FilterRule>) {
    val contentResolver: ContentResolver = context.contentResolver!!
    val rules = rules.filter { it.enabled }
    val mediaCollections = listOf(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )

    /**
     *
     */
    fun filterUris(contentUris: Array<Uri>): Array<Uri> {
        // 1. Fetch the relative paths of all the content URIs received.
        // 2. Filter any URIs not in /DCIM/, then group all the URIs by their relative path.
        // 3. Use the keys of the grouped URIs to get all the active rules, drop any groups without a corresponding rule,
        // 4. Apply each rule to it's group, combine into one array and return

        val uriIdToPath = fetchContentRelativePaths(contentUris)
        val groupedIds = groupUris(uriIdToPath)

        val rulePaths = rules.map { it.fromRelativePath }.toHashSet()
        val groupedActivePaths = groupedIds.filterKeys { path -> rulePaths.contains(path) }

        return emptyArray<Uri>()
    }

    /**
     * Fetches the relative path of all the content Uris given using Mediastore.
     * @param contentUris An array of content Uris, typically obtained from a content uri trigger.
     * @return A map from the content id to its relative path
     */
    private fun fetchContentRelativePaths(contentUris: Array<Uri>): Map<String, String> {
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
     * Flips map from a map of content id -> path, to a map of a path to a list of ids, which contain the same path
     *  @param idToRelPath a map of a content id to the relative path it resides in.
     *  @return A map which groups content ids by relative path (relative path -> list(ids))
     */
    private fun groupUris(idToRelPath: Map<String, String>): Map<String, List<String>> {
        val groupedUris = mutableMapOf<String, MutableList<String>>()

        idToRelPath.forEach { (id, path) ->
            groupedUris.getOrPut(path) { mutableStateListOf() }
                .add(id)
        }

        return groupedUris
    }

    /**
     *  Fetches a set of attributes for a piece of Media.
     *  @param contentId The id of the media within MediaStore.
     *  @param attributes The set of attributes to fetch the value of.
     *  @return A map from each attribute to it's corresponding value.
     */
    private fun fetchContentIdAttributes(
        contentId: String,
        attributes: List<ConditionAttribute>
    ): Map<String, ConditionValue<*>> {
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
                            is StringValue -> StringValue.RawStringValue(cursor.getString(index))
                            is LongValue -> LongValue(cursor.getString(index))
                            is BoolValue -> BoolValue() //TODO make it bool
                            is SpecialValue -> throw Exception() //todo make more verbose
                        }
                    }
                }
            }
        }

        return result
    }

    /**
     *  Filters a list of content ids by its respective rule
     *  @param rule A rule that filter's content at the relative path of contentIds
     *  @param contentIds The content group, which is a list of content ids.
     */
    private fun filterContentGroup(rule: FilterRule, contentIds: List<String>): List<String> {
        val conditions = rule.conditions

        if (conditions.isEmpty()) {
            return emptyList()
        }

        val filteredIds = mutableListOf<String>()

        contentIds.forEach { id ->
            val idAttributes = fetchContentIdAttributes(id, conditions.map { it.attribute })
            // 2. Check if rule matches todo where to put this functionality? In the FilterRule class as a method?
            // 3. add to filteredIds else discard
            // 4. return filteredids
        }

        return filteredIds
    }
}