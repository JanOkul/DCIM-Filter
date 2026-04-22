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
    val rules: List<Rule>
)