package com.janokul.dcimfilter.room.rule.types

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class RuleValue {
    abstract val validOps: Set<RuleOps>

    @Serializable
    data class StringValue(val value: String) : RuleValue() {
        @Transient
        override val validOps = setOf(
            RuleOps.EQUALS,
            RuleOps.NOT_EQUALS
        )
    }

    @Serializable
    data class IntValue(val value: Int) : RuleValue() {
        @Transient
        override val validOps = setOf(
            RuleOps.EQUALS,
            RuleOps.NOT_EQUALS,
            RuleOps.GREATER_THAN,
            RuleOps.GREATER_THAN_OR_EQUAL,
            RuleOps.LESS_THAN,
            RuleOps.LESS_THAN_OR_EQUAL
        )
    }
}