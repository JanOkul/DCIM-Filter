package com.janokul.dcimfilter.room.rule

import com.janokul.dcimfilter.room.rule.types.RuleKeys
import com.janokul.dcimfilter.room.rule.types.RuleOps
import com.janokul.dcimfilter.room.rule.types.RuleValue
import kotlinx.serialization.Serializable

@Serializable
data class Rule(
    val key: RuleKeys,
    val ops: RuleOps,
    val value: RuleValue
)
