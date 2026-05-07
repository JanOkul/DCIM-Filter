package com.janokul.dcimfilter.room.rule

import com.janokul.dcimfilter.room.rule.types.ConditionAttribute
import com.janokul.dcimfilter.room.rule.types.ConditionOp
import com.janokul.dcimfilter.room.rule.types.ConditionValue
import kotlinx.serialization.Serializable

@Serializable
data class Condition(
    val attribute: ConditionAttribute,
    val op: ConditionOp,
    val value: ConditionValue<*>
)
