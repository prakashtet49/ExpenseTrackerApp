package com.example.expensetrackerapp.presentation.state

import com.example.expensetrackerapp.data.model.ExpenseCategory
import java.time.LocalDate

data class ExpenseEntryState(
    val title: String = "",
    val amount: String = "",
    val category: ExpenseCategory = ExpenseCategory.FOOD_DINING,
    val notes: String = "",
    val receiptImagePath: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val totalSpentToday: Double = 0.0,
    val selectedDate: LocalDate? = null
)
