package com.janokul.dcimfilter.processing.filtering

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
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

private const val TAG = "ContentFilterEngine"

class ContentFilterEngine(private val contentRepository: ContentRepository) {
    val rules = contentRepository.fetchActiveRules().filter { it.enabled }

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

        val contentPaths = contentRepository.fetchContentPaths(contentUris)
        val groupedContentByPath = groupContentByPath(contentPaths)
        val rulePaths = rules.map { it.fromRelativePath }.toHashSet()

        // Filters out any content groups that doesn't have any rules corresponding to the content group
        val filterableContent = groupedContentByPath.filterKeys { path -> rulePaths.contains(path) }
        Log.d(TAG, filterableContent.toString())

        val toFilter = mutableMapOf<FilterRule, List<ContentId>>()

        rules.forEach { rule ->
            val contentIds = filterableContent[rule.fromRelativePath] ?: emptyList()
            Log.d(TAG, "There are ${contentIds.size} ids to filter for group ${rule.fromRelativePath}: $contentIds ")
            val filteredContentIds = filterContentGroup(rule, contentIds)
            Log.d(TAG, "${filteredContentIds.size} Ids need filtered: $filteredContentIds")
            toFilter[rule] = filteredContentIds
        }

        return toFilter
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
            val contentAttributes = contentRepository.fetchContentIdAttributes(id, conditions.map { it.attribute })
            if (rule.matches(contentAttributes)) {
                filteredIds.add(id)
            }
        }

        return filteredIds
    }
}