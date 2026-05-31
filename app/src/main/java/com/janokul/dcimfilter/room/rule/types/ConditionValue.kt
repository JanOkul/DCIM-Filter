package com.janokul.dcimfilter.room.rule.types

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class ConditionValue<T> {
    abstract var value: T
    abstract val validOps: List<ConditionOp>
    abstract val defaultOp: ConditionOp

    @Serializable
    sealed class StringValue: ConditionValue<String>() {
        @Transient
        override val defaultOp: ConditionOp = ConditionOp.EQUALS

        @Transient
        override val validOps = listOf(
            ConditionOp.EQUALS,
            ConditionOp.NOT_EQUALS
        )

        @Serializable
        data class RawStringValue(override var value: String = "") : StringValue() {
        }

        @Serializable
        data class DateValue(override var value: String = "") : StringValue() {
        }

        @Serializable
        data class PackageValue(override var value: String = "") : StringValue() {
        }
    }

    @Serializable
    sealed class SpecialValue : ConditionValue<Unit>() {
        @Transient
        override val validOps = emptyList<ConditionOp>()
        abstract var permitted: Boolean

        @Serializable
        data class NoneValue(
            override var value: Unit = Unit,
            override var permitted: Boolean = false
        ) : SpecialValue() {
            @Transient
            override val defaultOp: ConditionOp = ConditionOp.NO_OP

        }

        @Serializable
        data class AllValue(
            override var value: Unit = Unit,
            override var permitted: Boolean = false
        ) : SpecialValue() {
            @Transient
            override val defaultOp: ConditionOp = ConditionOp.NO_OP

        }
    }

    @Serializable
    data class LongValue(override var value: String = "0") : ConditionValue<String>() {
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
    }

    @Serializable
    data class BoolValue(override var value: Boolean = false) : ConditionValue<Boolean>() {
        @Transient
        override val validOps = listOf(ConditionOp.EQUALS, ConditionOp.NOT_EQUALS, ConditionOp.NO_OP)

        @Transient
        override var defaultOp: ConditionOp = ConditionOp.EQUALS
    }
}
