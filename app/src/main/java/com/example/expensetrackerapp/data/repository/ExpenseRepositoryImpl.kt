package com.example.expensetrackerapp.data.repository

import com.example.expensetrackerapp.data.local.ExpenseDao
import com.example.expensetrackerapp.data.local.DailyTotalResult
import com.example.expensetrackerapp.data.local.CategoryTotalResult
import com.example.expensetrackerapp.data.model.Expense
import com.example.expensetrackerapp.data.model.ExpenseCategory
import com.example.expensetrackerapp.data.model.CategoryTotal
import com.example.expensetrackerapp.data.model.DailyTotal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    
    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses()
    }
    
    override fun getExpensesByDate(date: LocalDate): Flow<List<Expense>> {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        // For single date queries, we need to include the entire day
        android.util.Log.d("ExpenseRepository", "getExpensesByDate: date='$dateString'")
        return expenseDao.getExpensesByDate(dateString)
    }
    
    override fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategory(category)
    }
    
    override fun getTotalSpentByDate(date: LocalDate): Flow<Double> {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        // For single date queries, we need to include the entire day
        android.util.Log.d("ExpenseRepository", "getTotalSpentByDate: date='$dateString'")
        return expenseDao.getTotalSpentByDate(dateString)
    }
    
    override fun getExpenseCountByDate(date: LocalDate): Flow<Int> {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        // For single date queries, we need to include the entire day
        android.util.Log.d("ExpenseRepository", "getExpenseCountByDate: date='$dateString'")
        return expenseDao.getExpenseCountByDate(dateString)
    }
    
    // New methods for real-time reports
    override fun getDailyTotals(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyTotal>> {
        val startDateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endDateString = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        android.util.Log.d("ExpenseRepository", "getDailyTotals: startDate='$startDateString', endDate='$endDateString'")
        
        return expenseDao.getDailyTotals(startDateString, endDateString).map { results ->
            android.util.Log.d("ExpenseRepository", "getDailyTotals result: ${results.size} entries")
            results.map { result ->
                DailyTotal(
                    date = LocalDate.parse(result.date),
                    amount = result.totalAmount,
                    count = result.expenseCount
                )
            }.sortedByDescending { it.date }
        }
    }
    
    override fun getCategoryTotals(startDate: LocalDate, endDate: LocalDate): Flow<List<CategoryTotal>> {
        val startDateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endDateString = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        android.util.Log.d("ExpenseRepository", "getCategoryTotals: startDate='$startDateString', endDate='$endDateString'")
        
        return expenseDao.getCategoryTotals(startDateString, endDateString).map { results ->
            android.util.Log.d("ExpenseRepository", "getCategoryTotals result: ${results.size} entries")
            results.map { result ->
                CategoryTotal(
                    category = result.category,
                    amount = result.totalAmount,
                    count = result.expenseCount,
                    percentage = 0.0 // Will be calculated in the UI
                )
            }.sortedByDescending { it.amount }
        }
    }
    
    override fun getTotalAmountInRange(startDate: LocalDate, endDate: LocalDate): Flow<Double> {
        val startDateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endDateString = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        android.util.Log.d("ExpenseRepository", "getTotalAmountInRange: startDate='$startDateString', endDate='$endDateString'")
        
        return expenseDao.getTotalAmountInRange(startDateString, endDateString)
    }
    
    override fun getTotalCountInRange(startDate: LocalDate, endDate: LocalDate): Flow<Int> {
        val startDateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endDateString = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        android.util.Log.d("ExpenseRepository", "getTotalCountInRange: startDate='$startDateString', endDate='$endDateString'")
        
        return expenseDao.getTotalCountInRange(startDateString, endDateString)
    }
    
    override suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }
    
    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }
    
    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
    
    override suspend fun deleteExpenseById(id: Long) {
        expenseDao.deleteExpenseById(id)
    }
}
