package com.janokul.dcimfilter.room.rule

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.janokul.dcimfilter.Attribute
import com.janokul.dcimfilter.room.rule.types.ConditionOp
import com.janokul.dcimfilter.room.rule.types.value.BoolValue
import com.janokul.dcimfilter.room.rule.types.value.ConditionValue
import com.janokul.dcimfilter.room.rule.types.value.LongValue
import com.janokul.dcimfilter.room.rule.types.value.SpecialValue
import com.janokul.dcimfilter.room.rule.types.value.StringValue

private const val TAG = "FilterRule"

@Entity
data class FilterRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val enabled: Boolean,
    val fromRelativePath: String,
    val toRelativePath: String,
    val conditions: List<Condition>
) {
    companion object {
        fun empty(): FilterRule {
            return FilterRule(
                enabled = false,
                fromRelativePath = "",
                toRelativePath = "",
                conditions = emptyList()
            )
        }
    }

    fun matches(toMatch: Map<Attribute, ConditionValue<*>>): Boolean {
        return conditions.all { condition ->
            val actualValue = toMatch[condition.attribute.value] ?: return false

            match(condition.op, condition.value, actualValue)
        }
    }

    private fun match(op: ConditionOp, targetValue: ConditionValue<*>, actualValue: ConditionValue<*>): Boolean {
        if (targetValue::class != actualValue::class) {
            Log.d(TAG, "Type mismatch between target and actual values. Target: ${targetValue::class}, Actual: ${actualValue::class}")
            return false
        }
        Log.d(TAG, "Matching the following: ${targetValue.value} ${op.op} ${actualValue.value}")
        val matchResult = when (targetValue) {
            is LongValue    -> targetValue.matches(op, actualValue as LongValue)
            is BoolValue    -> targetValue.matches(op, actualValue as BoolValue)
            is StringValue  -> targetValue.matches(op, actualValue as StringValue)
            is SpecialValue -> targetValue.matches(op, actualValue as SpecialValue)
        }
        Log.d(TAG, "Matches result: $matchResult")
        return matchResult
    }
}

//