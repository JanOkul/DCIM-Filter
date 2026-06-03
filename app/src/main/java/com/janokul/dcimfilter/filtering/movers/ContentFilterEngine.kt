package com.janokul.dcimfilter.filtering.movers

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.janokul.dcimfilter.Attribute
import com.janokul.dcimfilter.ContentId
import com.janokul.dcimfilter.DCIM_REL_PATH_SQL
import com.janokul.dcimfilter.RelativePath
import com.janokul.dcimfilter.room.rule.FilterRule
import com.janokul.dcimfilter.room.rule.types.ConditionAttribute
import com.janokul.dcimfilter.room.rule.types.value.ConditionValue
import com.janokul.dcimfilter.room.rule.types.value.BoolValue
import com.janokul.dcimfilter.room.rule.types.value.LongValue
import com.janokul.dcimfilter.room.rule.types.value.SpecialValue
import com.janokul.dcimfilter.room.rule.types.value.StringValue

class ContentFilterEngine(context: Context, rules: List<FilterRule>) {
    val contentResolver: ContentResolver = context.contentResolver!!
    val rules = rules.filter { it.enabled }
    val mediaCollections = listOf(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )

    /**
     *  Filters a set of content URI's against a rule against the content's relative path. Rules have a 1 to 1 relation to each path.
     *  @param contentUris A list of MediaStore content to filter.
     *  @return A map of each rule to a list of content that needs moving to the rule's destination path.
     */
    fun filterUris(contentUris: Array<Uri>): Map<FilterRule, List<ContentId>> {
        // 1. Fetch the relative paths of all the content URIs received.
        // 2. Filter any URIs not in /DCIM/, then group all the URIs by their relative path.
        // 3. Use the keys of the grouped URIs to get all the active rules, drop any groups without a corresponding rule,
        // 4. Apply each rule to it's group, combine into one array and return

        val contentPaths = fetchContentPaths(contentUris)
        val groupedContentByPath = groupContentByPath(contentPaths)
        val rulePaths = rules.map { it.fromRelativePath }.toHashSet()

        // Filters out any content groups that doesn't have any rules corresponding to the content group
        val filterableContent = groupedContentByPath.filterKeys { path -> rulePaths.contains(path) }

        val toFilter = mutableMapOf<FilterRule, List<ContentId>>()

        rules.forEach { rule -> toFilter[rule] =
            filterContentGroup(
                rule,
                filterableContent[rule.fromRelativePath] ?: emptyList()
            )
        }

        return toFilter
    }

    /**
     * Fetches the relative path of all the content Uris given using Mediastore.
     * @param contentUris An array of content Uris, typically obtained from a content uri trigger.
     * @return A map from the content id to its relative path
     */
    private fun fetchContentPaths(contentUris: Array<Uri>): Map<ContentId, RelativePath> {
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
    private fun groupContentByPath(idToRelPath: Map<ContentId, RelativePath>): Map<RelativePath, List<ContentId>> {
        val groupedUris = mutableMapOf<String, MutableList<String>>()

        idToRelPath.forEach { (id, path) ->
            groupedUris.getOrPut(path) { mutableListOf() }
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
                            is StringValue -> StringValue.RawStringValue(cursor.getString(index))
                            is LongValue -> LongValue(cursor.getString(index))
                            is BoolValue -> BoolValue(cursor.getString(index))
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
    private fun filterContentGroup(rule: FilterRule, contentIds: List<ContentId>): List<ContentId> {
        val conditions = rule.conditions

        if (conditions.isEmpty() || contentIds.isEmpty()) {
            return emptyList()
        }

        val filteredIds = mutableListOf<String>()

        contentIds.forEach { id ->
            val contentAttributes = fetchContentIdAttributes(id, conditions.map { it.attribute })
            if (rule.matches(contentAttributes)) {
                filteredIds.add(id)
            }
        }

        return filteredIds
    }
}