package com.janokul.dcimfilter

import android.net.Uri
import com.janokul.dcimfilter.processing.filtering.ContentFilterEngine
import com.janokul.dcimfilter.room.rule.Condition
import com.janokul.dcimfilter.room.rule.FilterRule
import com.janokul.dcimfilter.room.rule.types.ConditionAttribute
import com.janokul.dcimfilter.room.rule.types.ConditionOp
import com.janokul.dcimfilter.room.rule.types.value.StringValue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.google.common.truth.Truth.*
import com.janokul.dcimfilter.room.rule.types.value.BoolValue
import com.janokul.dcimfilter.room.rule.types.value.LongValue

private fun createEngine(testRules: List<FilterRule>): ContentFilterEngine = ContentFilterEngine(MockContentRepository(testRules) )
private fun uri(id: String): Uri = mock<Uri>().also {
    whenever(it.lastPathSegment).thenReturn(id)
    whenever { it.toString() }.thenReturn(id)
}

class ContentFilterEngineTest {
    // ---------- Single Rule ----------
    @Test
    fun `String - Package Value`() {
        val testRules = listOf(
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
        )

        val testContentUris = arrayOf(
            uri("0"), // Valid URI Id
            uri("2"), // Same folder, different owner
            uri("4"), // Same owner, different folder, but in dcim
            uri("7")  // Completely different owner, folder not in dcim
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(1)
        assertThat(filteredUris[0]).containsExactly("0")
    }

    @Test
    fun `Long Value - 1`() {
        val testRules = listOf(
            FilterRule(
                id = 0,
                enabled = true,
                fromRelativePath = "DCIM/Camera/",
                toRelativePath = "app0",
                conditions = listOf(
                    Condition(
                        ConditionAttribute.SIZE,
                        ConditionOp.GREATER_THAN_OR_EQUAL,
                        LongValue("5000")
                    )
                )
            )
        )

        val testContentUris = arrayOf(
            uri("1"), // Valid URI Id
            uri("0"), // Same folder, different size
            uri("4"), // Same size, different folder, but in dcim
            uri("7")  // Same size, different folder, folder not in dcim
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(1)
        assertThat(filteredUris[0]).containsExactly("1")
    }

    @Test
    fun `Long Value - 2`() {
        val testRules = listOf(
            FilterRule(
                id = 0,
                enabled = true,
                fromRelativePath = "DCIM/Camera/",
                toRelativePath = "app0",
                conditions = listOf(
                    Condition(
                        ConditionAttribute.SIZE,
                        ConditionOp.GREATER_THAN_OR_EQUAL,
                        LongValue("5000")
                    )
                )
            )
        )

        val testContentUris = arrayOf(
            uri("1"), // Valid URI Id
            uri("2"), // Also valid
            uri("0"), // Too low for threshold
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(1)
        assertThat(filteredUris[0]).containsExactly("1", "2")
    }

    @Test
    fun `Bool Value - True`() {
        val testRules = listOf(
            FilterRule(
                id = 0,
                enabled = true,
                fromRelativePath = "DCIM/Camera/",
                toRelativePath = "app0",
                conditions = listOf(
                    Condition(
                        ConditionAttribute.IS_FAVOURITE,
                        ConditionOp.EQUALS,
                        BoolValue("true")
                    )
                )
            )
        )

        val testContentUris = arrayOf(
            uri("0"), // True
            uri("1"), // False
            uri("2"), // True
            uri("7"), // True
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(1)
        assertThat(filteredUris[0]).containsExactly("0", "2")
    }

    @Test
    fun `Bool Value - False`() {
        val testRules = listOf(
            FilterRule(
                id = 0,
                enabled = true,
                fromRelativePath = "DCIM/Camera/",
                toRelativePath = "app0",
                conditions = listOf(
                    Condition(
                        ConditionAttribute.IS_FAVOURITE,
                        ConditionOp.EQUALS,
                        BoolValue("false")
                    )
                )
            )
        )

        val testContentUris = arrayOf(
            uri("0"), // True
            uri("1"), // False
            uri("2"), // True
            uri("7"), // True
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(1)
        assertThat(filteredUris[0]).containsExactly("1")
    }


    // todo Test disabled rules
    // ---------- Multiple Rules ----------


    // ---------- Misc/Edge Cases ----------
    @Test
    fun `No conditions`() {
        val testRules = listOf(
            FilterRule(
                id = 0,
                enabled = true,
                fromRelativePath = "DCIM/Camera/",
                toRelativePath = "app0",
                conditions = emptyList()
            )
        )

        val testContentUris = arrayOf(
            uri("0"),
            uri("1")
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(1)
        assertThat(filteredUris[0]).containsExactly()
    }

    // ---------- Misc/Edge Cases ----------
    @Test
    fun `No uris`() {
        val testRules = listOf(
            FilterRule(
                id = 0,
                enabled = true,
                fromRelativePath = "DCIM/Camera/",
                toRelativePath = "app0",
                conditions = listOf(
                    Condition(
                        ConditionAttribute.IS_FAVOURITE,
                        ConditionOp.EQUALS,
                        BoolValue("false")
                    )
                )
            )
        )

        val testContentUris = emptyArray<Uri>()

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris)

        assertThat(filteredUris.size).isEqualTo(0)
    }

    @Test
    fun `No rules`() {
        val testRules = emptyList<FilterRule>()


        val testContentUris = arrayOf(
            uri("0"),
            uri("1")
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris)

        assertThat(filteredUris.size).isEqualTo(0)
    }

}