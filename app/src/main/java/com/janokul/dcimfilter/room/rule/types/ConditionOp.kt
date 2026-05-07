package com.janokul.dcimfilter.room.rule.types

import kotlinx.serialization.Serializable

@Serializable
enum class ConditionOp(val op: String) {
    EQUALS("="),
    NOT_EQUALS("!="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    NO_OP("NO OP")
}