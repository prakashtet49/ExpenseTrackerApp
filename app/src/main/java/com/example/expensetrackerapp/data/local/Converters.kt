package com.example.expensetrackerapp.data.local

import androidx.room.TypeConverter
import com.example.expensetrackerapp.data.model.ExpenseCategory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
    
    @TypeConverter
    fun fromExpenseCategory(category: ExpenseCategory): String {
        return category.name
    }
    
    @TypeConverter
    fun toExpenseCategory(category: String): ExpenseCategory {
        return ExpenseCategory.valueOf(category)
    }
}
