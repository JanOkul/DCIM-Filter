package com.janokul.dcimfilter.filtering

import android.net.Uri
import com.janokul.dcimfilter.room.rule.FilterRule

class ContentFilterEngine {

    /**
     *
     */
    private fun filterUris(contentUris: Array<Uri>): Array<Uri> {

        return emptyArray<Uri>()
    }

    /**
     * Fetches the relative path of all the content Uris given using Mediastore.
     * @param contentUris An array of content Uris, typically obtained from a content uri trigger.
     * @return The relative path of all the Uris passed into the function.
     */
    private fun fetchContentRelativePaths(contentUris: Array<Uri>): Array<String> {

        return emptyArray()
    }

    private fun fetchFilteringRules(): List<FilterRule> {

        return emptyList<FilterRule>()
    }

}