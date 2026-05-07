package com.janokul.dcimfilter.room.rule.types

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class ConditionValue<T> {
    abstract var value: T
    abstract val validOps: List<ConditionOp>
    abstract val defaultOp: ConditionOp
    abstract fun setImmutable(new: T): ConditionValue<T>

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
        data class RawStringValue(override var value: String = ""): StringValue() {
            override fun setImmutable(new: String): ConditionValue<String> {
                return RawStringValue(value = new)
            }
        }

        @Serializable
        data class DateValue(override var value: String = ""): StringValue() {
            override fun setImmutable(new: String): ConditionValue<String> {
                return DateValue(value = new)
            }
        }

        @Serializable
        data class PackageValue(override var value: String = ""): StringValue() {
            override fun setImmutable(new: String): ConditionValue<String> {
                return PackageValue(value = new)
            }
        }
    }

    @Serializable
    data class LongValue(override var value: Long = 0) : ConditionValue<Long>() {
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

        override fun setImmutable(new: Long): ConditionValue<Long> {
            return LongValue(value = new)
        }
    }

    @Serializable
    data class BoolValue(override var value: Boolean = false) : ConditionValue<Boolean>() {
        @Transient

        override val validOps = listOf(ConditionOp.NO_OP)

        @Transient
        override val defaultOp: ConditionOp = ConditionOp.NO_OP

        override fun setImmutable(new: Boolean): ConditionValue<Boolean> {
            return BoolValue(value = new)
        }
    }
}