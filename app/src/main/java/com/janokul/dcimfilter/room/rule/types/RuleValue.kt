package com.janokul.dcimfilter.room.rule.types

sealed class RuleValue {
    abstract val validOps: Set<RuleOps>

    data class StringValue(val value: String) : RuleValue() {
        override val validOps = setOf(
            RuleOps.EQUALS,
            RuleOps.NOT_EQUALS
        )
    }

    data class IntValue(val value: Int) : RuleValue() {
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