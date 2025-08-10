package com.example.expensetrackerapp.data.model

import java.time.LocalDate

data class DailyTotal(
    val date: LocalDate,
    val amount: Double,
    val count: Int
)

data class CategoryTotal(
    val category: ExpenseCategory,
    val amount: Double,
    val count: Int,
    val percentage: Double
)
