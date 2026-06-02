package com.janokul.dcimfilter.room.rule.types.value

import com.janokul.dcimfilter.room.rule.types.ConditionOp
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class LongValue(override var value: String = "0") : ConditionValue<Long>() {
    @Transient
    override val validOps = listOf(
        ConditionOp.EQUALS,
        ConditionOp.NOT_EQUALS,
        ConditionOp.GREATER_THAN,
        ConditionOp.GREATER_THAN_OR_EQUAL,
        ConditionOp.LESS_THAN,
        ConditionOp.LESS_THAN_OR_EQUAL
    )

    @Transient
    override val defaultOp: ConditionOp = ConditionOp.EQUALS
    override fun asSelf(): Long = value.toLong()

    override fun matches(op: ConditionOp, other: ConditionValue<Long>): Boolean {
        val selfValue = asSelf()
        val otherValue = other.asSelf()

        return when (op) {
            ConditionOp.EQUALS                 -> selfValue == otherValue
            ConditionOp.NOT_EQUALS             -> selfValue != otherValue
            ConditionOp.GREATER_THAN           -> selfValue > otherValue
            ConditionOp.GREATER_THAN_OR_EQUAL  -> selfValue >= otherValue
            ConditionOp.LESS_THAN              -> selfValue < otherValue
            ConditionOp.LESS_THAN_OR_EQUAL     -> selfValue <= otherValue
            else -> throw IllegalArgumentException("Operand: $op is not valid for long operations.")
        }
    }
}