package com.janokul.dcimfilter.room.rule

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class ConditionListConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromConditionList(conditions: List<Condition>): String {
        return json.encodeToString(ListSerializer(Condition.serializer()), conditions)
    }

    @TypeConverter
    fun toConditionList(value: String): List<Condition> {
        return json.decodeFromString(ListSerializer(Condition.serializer()), value)
    }
}