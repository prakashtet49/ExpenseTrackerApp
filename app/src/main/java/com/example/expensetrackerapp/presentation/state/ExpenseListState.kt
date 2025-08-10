package com.example.expensetrackerapp.presentation.state

import com.example.expensetrackerapp.data.model.Expense
import java.time.LocalDate

data class ExpenseListState(
    val expenses: List<Expense> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val totalAmount: Double = 0.0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val groupByCategory: Boolean = false
)
