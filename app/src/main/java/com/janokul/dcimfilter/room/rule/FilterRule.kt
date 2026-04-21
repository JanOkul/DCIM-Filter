package com.janokul.dcimfilter.room.rule

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.janokul.dcimfilter.room.rule.types.RuleKeys
import com.janokul.dcimfilter.room.rule.types.RuleOps
import com.janokul.dcimfilter.room.rule.types.RuleValue

@Entity
data class FilterRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromRelativePath: String,
    val toRelativePath: String,
    val rules: List<Rule>
)