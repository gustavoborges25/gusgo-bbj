package com.gusgo.bbj.resources

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.DayOfWeek

@Converter
class DaysOfWeekConverter : AttributeConverter<List<DayOfWeek>, String> {

    override fun convertToDatabaseColumn(attribute: List<DayOfWeek>?): String? {
        if (attribute.isNullOrEmpty()) return null
        return attribute.joinToString(",") { it.name }
    }

    override fun convertToEntityAttribute(dbData: String?): List<DayOfWeek>? {
        if (dbData.isNullOrBlank()) return mutableListOf()
        return dbData.split(",").map { DayOfWeek.valueOf(it) }.toMutableList()
    }
}