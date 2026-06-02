package com.janokul.dcimfilter.room.rule

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.janokul.dcimfilter.Attribute
import com.janokul.dcimfilter.room.rule.types.ConditionOp
import com.janokul.dcimfilter.room.rule.types.value.ConditionValue

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

    // Checks if content matches
    fun matches(toMatch: Map<Attribute, ConditionValue<*>>): Boolean {
        return conditions.all { condition ->
            val actualValue = toMatch[condition.attribute.value] ?: return false

            match(condition.op, condition.value, actualValue)
        }
    }

    private fun match(op: ConditionOp, targetValue: ConditionValue<*>,  actualValue: ConditionValue<*>): Boolean {
        if (targetValue::class != actualValue::class) {
            Log.d(TAG, "Type mismatch between target and actual values. Target: ${targetValue::class}, Actual: ${actualValue::class}")
            return false
        }

        //todo needs to be split into matching for the given type of conditionValue
        return when (op) {
            ConditionOp.EQUALS -> targetValue.asSelf() == actualValue.asSelf()
            ConditionOp.NOT_EQUALS -> targetValue.asSelf() != actualValue.asSelf()
            ConditionOp.GREATER_THAN -> targetValue.asSelf() > actualValue.asSelf()
        }
    }
}

//