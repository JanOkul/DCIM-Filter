package com.janokul.dcimfilter.room.rule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FilterRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val enabled: Boolean,
    val fromRelativePath: String,
    val toRelativePath: String,
    val conditions: List<Condition>
) {
    companion object {
        fun empty(): FilterRule {
            return FilterRule(
                enabled = false,
                fromRelativePath = "",
                toRelativePath = "",
                conditions = emptyList()
            )
        }
    }
}