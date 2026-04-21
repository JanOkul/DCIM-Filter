package com.janokul.dcimfilter.room.rule

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class RuleListConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromRuleList(rules: List<Rule>): String {
        return json.encodeToString(rules)
    }

    @TypeConverter
    fun toRuleList(value: String): List<Rule> {
        return json.decodeFromString(value)
    }
}