package com.janokul.dcimfilter.contentfilterengine

import android.net.Uri
import com.google.common.truth.Truth.assertThat
import com.janokul.dcimfilter.processing.filtering.ContentFilterEngine
import com.janokul.dcimfilter.room.rule.*
import com.janokul.dcimfilter.room.rule.types.*
import com.janokul.dcimfilter.room.rule.types.value.*
import org.junit.Test
import org.mockito.kotlin.*

fun createEngine(testRules: List<FilterRule>): ContentFilterEngine = ContentFilterEngine(
    MockContentRepository(testRules)
)
fun uri(id: String): Uri = mock<Uri>().also {
    whenever(it.lastPathSegment).thenReturn(id)
    whenever { it.toString() }.thenReturn(id)
}

fun allUri() = arrayOf(
    uri("0"),
    uri("1"),
    uri("2"),
    uri("3"),
    uri("4"),
    uri("5"),
    uri("6"),
    uri("7")
)

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

    @Test
    fun `Special Value - All Value`() {
        val testRules = listOf(
            FilterRule(
                id = 0,
                enabled = true,
                fromRelativePath = "DCIM/Camera/",
                toRelativePath = "app0",
                conditions = listOf(
                    Condition(
                        ConditionAttribute.FILTER_ALL,
                        ConditionOp.NO_OP,
                        SpecialValue.AllValue(permitted = true)
                    )
                )
            )
        )

        val testContentUris = arrayOf(
            uri("0"),
            uri("1"),
            uri("2"),
            uri("3"),
            uri("7")
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(1)
        assertThat(filteredUris[0]).containsExactly("0", "1", "2")
    }

    @Test
    fun `Special Value - All Value NOT PERMITTED`() {
        val testRules = listOf(
            FilterRule(
                id = 0,
                enabled = true,
                fromRelativePath = "DCIM/Camera/",
                toRelativePath = "app0",
                conditions = listOf(
                    Condition(
                        ConditionAttribute.FILTER_ALL,
                        ConditionOp.NO_OP,
                        SpecialValue.AllValue(permitted = false)
                    )
                )
            )
        )

        val testContentUris = arrayOf(
            uri("0"),
            uri("1"),
            uri("2"),
            uri("3"),
            uri("7")
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(0)
    }

    @Test
    fun `Special Value - None Value`() {
        val testRules = listOf(
            FilterRule(
                id = 0,
                enabled = true,
                fromRelativePath = "DCIM/Camera/",
                toRelativePath = "app0",
                conditions = listOf(
                    Condition(
                        ConditionAttribute.FILTER_ALL,
                        ConditionOp.NO_OP,
                        SpecialValue.NoneValue(permitted = true)
                    ),
                    Condition(
                        ConditionAttribute.SIZE,
                        ConditionOp.GREATER_THAN,
                        LongValue("5000")
                    )
                )
            )
        )

        val testContentUris = arrayOf(
            uri("0"),
            uri("1"),
            uri("2"),
            uri("3"),
            uri("7")
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(0)
    }

    @Test
    fun `Special Value - None Value NOT PERMITTED`() {
        val testRules = listOf(
            FilterRule(
                id = 0,
                enabled = true,
                fromRelativePath = "DCIM/Camera/",
                toRelativePath = "app0",
                conditions = listOf(
                    Condition(
                        ConditionAttribute.FILTER_ALL,
                        ConditionOp.NO_OP,
                        SpecialValue.NoneValue(permitted = false)
                    ),
                    Condition(
                        ConditionAttribute.SIZE,
                        ConditionOp.GREATER_THAN,
                        LongValue("5000")
                    )
                )
            )
        )

        val testContentUris = arrayOf(
            uri("0"),
            uri("1"),
            uri("2"),
            uri("3"),
            uri("7")
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(0)
    }

    @Test
    fun `No Filterable Content - Path Doesn't Match`() {
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
            uri("6"),
            uri("7")
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(0)
    }

    @Test
    fun `No Filterable Content - No Valid Content`() {
        val testRules = listOf(
            FilterRule(
                id = 0,
                enabled = true,
                fromRelativePath = "DCIM/Camera/",
                toRelativePath = "app0",
                conditions = listOf(
                    Condition(
                        ConditionAttribute.SIZE,
                        ConditionOp.GREATER_THAN,
                        LongValue("99999")
                    )
                )
            )
        )

        val testContentUris = allUri()

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(0)
    }

    @Test
    fun `Two Conditions`() {
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
                    ),
                    Condition(
                        ConditionAttribute.SIZE,
                        ConditionOp.LESS_THAN,
                        LongValue("8000")
                    )
                )
            )
        )

        val testContentUris = arrayOf(
            uri("0"), // Fails first check but fails second
            uri("1"), // Valid, passes both checks
            uri("2"), // Passes first check, but fails second check by 1
        )

        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris).values.toList()

        assertThat(filteredUris.size).isEqualTo(1)
        assertThat(filteredUris[0]).containsExactly("1")
    }

    // ---------- Multiple Rules ----------

    @Test
    fun `Two Rules`() {
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
            ),
            FilterRule(
                id = 1,
                enabled = true,
                fromRelativePath = "DCIM/Screenshots/",
                toRelativePath = "Screenshots",
                conditions = listOf(
                    Condition(
                        ConditionAttribute.SIZE,
                        ConditionOp.GREATER_THAN_OR_EQUAL,
                        LongValue("5000")
                    )
                )
            )
        )


        val testContentUris = allUri()
        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris)

        assertThat(filteredUris.size).isEqualTo(2)

        assertThat(filteredUris[testRules[0]]!!.size).isEqualTo(2)
        assertThat(filteredUris[testRules[0]]!!).containsExactly("0", "2")

        assertThat(filteredUris[testRules[1]]!!.size).isEqualTo(1)
        assertThat(filteredUris[testRules[1]]!!).containsExactly("4")
    }

    @Test
    fun `Two Rules, 1 Disabled`() {
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
            ),
            FilterRule(
                id = 1,
                enabled = false,
                fromRelativePath = "DCIM/Screenshots/",
                toRelativePath = "Screenshots",
                conditions = listOf(
                    Condition(
                        ConditionAttribute.SIZE,
                        ConditionOp.GREATER_THAN_OR_EQUAL,
                        LongValue("5000")
                    )
                )
            )
        )

        val testContentUris = allUri()
        val engine = createEngine(testRules)
        val filteredUris = engine.filterUris(testContentUris)

        assertThat(filteredUris.size).isEqualTo(1)

        assertThat(filteredUris[testRules[0]]!!.size).isEqualTo(2)
        assertThat(filteredUris[testRules[0]]!!).containsExactly("0", "2")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Same Relative Path`() {
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
            ),
            FilterRule(
                id = 1,
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

        val testContentUris = allUri()
        val engine = createEngine(testRules)
        engine.filterUris(testContentUris)
    }

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

        assertThat(filteredUris.size).isEqualTo(0)
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