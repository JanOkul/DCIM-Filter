package com.janokul.dcimfilter.room.rule.types.value

import com.janokul.dcimfilter.room.rule.types.ConditionOp
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class BoolValue(override var value: String = false.toString()) : ConditionValue<Boolean>() {
    @Transient
    override val validOps = listOf(
        ConditionOp.EQUALS,
        ConditionOp.NOT_EQUALS
    )
    @Transient
    override var defaultOp: ConditionOp = ConditionOp.EQUALS

    override fun asSelf(): Boolean = value.toBoolean()

    override fun matches(op: ConditionOp, other: ConditionValue<Boolean>): Boolean {
        val selfValue = asSelf()
        val otherValue = other.asSelf()

        return when (op) {
            ConditionOp.EQUALS -> selfValue == otherValue
            ConditionOp.NOT_EQUALS -> selfValue != otherValue
            else -> throw IllegalArgumentException("Operand: $op is not valid for boolean operations.")
        }
    }
}