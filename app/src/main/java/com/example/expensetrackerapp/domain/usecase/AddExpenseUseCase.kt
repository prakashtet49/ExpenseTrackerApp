package com.example.expensetrackerapp.domain.usecase

import com.example.expensetrackerapp.data.model.Expense
import com.example.expensetrackerapp.data.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense) {
        repository.insertExpense(expense)
    }
}
