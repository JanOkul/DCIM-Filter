package com.janokul.dcimfilter.room.rule.types.value

import com.janokul.dcimfilter.room.rule.types.ConditionOp
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class StringValue: ConditionValue<String>() {
    @Transient
    override val defaultOp: ConditionOp = ConditionOp.EQUALS

    @Transient
    override val validOps = listOf(
        ConditionOp.EQUALS,
        ConditionOp.NOT_EQUALS
    )

    override fun matches(op: ConditionOp, other: ConditionValue<String>): Boolean {
        val selfValue = asSelf()
        val otherValue = other.asSelf()

        return when (op) {
            ConditionOp.EQUALS -> selfValue == otherValue
            ConditionOp.NOT_EQUALS -> selfValue != otherValue
            else -> throw IllegalArgumentException("Operand: $op is not valid for string operations.")
        }
    }

    override fun asSelf(): String = value
    @Serializable
    data class RawStringValue(override var value: String = "") : StringValue()

    @Serializable
    data class DateValue(override var value: String = "") : StringValue()

    @Serializable
    data class PackageValue(override var value: String = "") : StringValue()
}