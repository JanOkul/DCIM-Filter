package com.janokul.dcimfilter.room.rule.types.value

import com.janokul.dcimfilter.room.rule.types.ConditionOp
import kotlinx.serialization.Serializable

@Serializable
sealed class ConditionValue<T> {
    abstract var value: String
    abstract val validOps: List<ConditionOp>
    abstract val defaultOp: ConditionOp
    abstract fun asSelf(): T
    abstract fun matches(op: ConditionOp, other: ConditionValue<T>): Boolean
}