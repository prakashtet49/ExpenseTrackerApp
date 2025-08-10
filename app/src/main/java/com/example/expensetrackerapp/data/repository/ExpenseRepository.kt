package com.example.expensetrackerapp.data.repository

import com.example.expensetrackerapp.data.model.Expense
import com.example.expensetrackerapp.data.model.ExpenseCategory
import com.example.expensetrackerapp.data.model.CategoryTotal
import com.example.expensetrackerapp.data.model.DailyTotal
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    fun getExpensesByDate(date: LocalDate): Flow<List<Expense>>
    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>>
    fun getTotalSpentByDate(date: LocalDate): Flow<Double>
    fun getExpenseCountByDate(date: LocalDate): Flow<Int>
    
    // New methods for real-time reports
    fun getDailyTotals(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyTotal>>
    fun getCategoryTotals(startDate: LocalDate, endDate: LocalDate): Flow<List<CategoryTotal>>
    fun getTotalAmountInRange(startDate: LocalDate, endDate: LocalDate): Flow<Double>
    fun getTotalCountInRange(startDate: LocalDate, endDate: LocalDate): Flow<Int>
    
    suspend fun insertExpense(expense: Expense)
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    suspend fun deleteExpenseById(id: Long)
}
