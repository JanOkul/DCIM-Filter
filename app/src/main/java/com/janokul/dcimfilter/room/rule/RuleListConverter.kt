package com.janokul.dcimfilter.room.rule

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class RuleListConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromRuleList(rules: List<Rule>): String {
        return json.encodeToString(ListSerializer(Rule.serializer()), rules)
    }

    @TypeConverter
    fun toRuleList(value: String): List<Rule> {
        return json.decodeFromString(ListSerializer(Rule.serializer()), value)
    }
}