package com.example.expensetrackerapp.data.local

import androidx.room.*
import com.example.expensetrackerapp.data.model.Expense
import com.example.expensetrackerapp.data.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE createdAt LIKE :date || '%' ORDER BY createdAt DESC")
    fun getExpensesByDate(date: String): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY createdAt DESC")
    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>>
    
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE createdAt LIKE :date || '%'")
    fun getTotalSpentByDate(date: String): Flow<Double>
    
    @Query("SELECT COUNT(*) FROM expenses WHERE createdAt LIKE :date || '%'")
    fun getExpenseCountByDate(date: String): Flow<Int>
    
    // New methods for real-time reports
    @Query("""
        SELECT 
            DATE(createdAt) as date,
            COALESCE(SUM(amount), 0.0) as totalAmount,
            COUNT(*) as expenseCount
        FROM expenses 
        WHERE DATE(createdAt) >= DATE(:startDate) AND DATE(createdAt) <= DATE(:endDate)
        GROUP BY DATE(createdAt)
        ORDER BY date DESC
    """)
    fun getDailyTotals(startDate: String, endDate: String): Flow<List<DailyTotalResult>>
    
    @Query("""
        SELECT 
            category,
            COALESCE(SUM(amount), 0.0) as totalAmount,
            COUNT(*) as expenseCount
        FROM expenses 
        WHERE DATE(createdAt) >= DATE(:startDate) AND DATE(createdAt) <= DATE(:endDate)
        GROUP BY category
        ORDER BY totalAmount DESC
    """)
    fun getCategoryTotals(startDate: String, endDate: String): Flow<List<CategoryTotalResult>>
    
    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) 
        FROM expenses 
        WHERE DATE(createdAt) >= DATE(:startDate) AND DATE(createdAt) <= DATE(:endDate)
    """)
    fun getTotalAmountInRange(startDate: String, endDate: String): Flow<Double>
    
    @Query("""
        SELECT COUNT(*) 
        FROM expenses 
        WHERE DATE(createdAt) >= DATE(:startDate) AND DATE(createdAt) <= DATE(:endDate)
    """)
    fun getTotalCountInRange(startDate: String, endDate: String): Flow<Int>
    
    @Insert
    suspend fun insertExpense(expense: Expense)
    
    @Update
    suspend fun updateExpense(expense: Expense)
    
    @Delete
    suspend fun deleteExpense(expense: Expense)
    
    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)
}

// Data classes for query results
data class DailyTotalResult(
    val date: String,
    val totalAmount: Double,
    val expenseCount: Int
)

data class CategoryTotalResult(
    val category: ExpenseCategory,
    val totalAmount: Double,
    val expenseCount: Int
)
