package com.janokul.dcimfilter.room.rule.types.value

import com.janokul.dcimfilter.room.rule.types.ConditionOp
import kotlinx.serialization.*

@Serializable
sealed class SpecialValue : ConditionValue<Unit>() {
    @Transient
    override val validOps = emptyList<ConditionOp>()
    abstract var permitted: Boolean

    @Serializable
    data class NoneValue(
        override var value: String = Unit.toString(),
        override var permitted: Boolean = false
    ) : SpecialValue() {
        @Transient
        override val defaultOp: ConditionOp = ConditionOp.NO_OP

        override fun asSelf(): Unit = Unit
        override fun matches(op: ConditionOp, other: ConditionValue<Unit>): Boolean {
            return false
        }
    }

    @Serializable
    data class AllValue(
        override var value: String = Unit.toString(),
        override var permitted: Boolean = false
    ) : SpecialValue() {
        @Transient
        override val defaultOp: ConditionOp = ConditionOp.NO_OP

        override fun asSelf(): Unit = Unit
        override fun matches(op: ConditionOp, other: ConditionValue<Unit>): Boolean {
            return permitted
        }


    }
}