package com.example.expensetrackerapp.domain.usecase

import com.example.expensetrackerapp.data.model.Expense
import com.example.expensetrackerapp.data.model.ExpenseCategory
import com.example.expensetrackerapp.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetExpensesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<Expense>> {
        return repository.getExpensesByDate(date)
    }
    
    fun getAllExpenses(): Flow<List<Expense>> {
        return repository.getAllExpenses()
    }
    
    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>> {
        return repository.getExpensesByCategory(category)
    }
}
