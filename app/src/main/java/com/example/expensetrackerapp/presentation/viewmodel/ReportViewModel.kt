package com.example.expensetrackerapp.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerapp.data.model.ExpenseCategory
import com.example.expensetrackerapp.data.repository.ExpenseRepository
import com.example.expensetrackerapp.data.model.CategoryTotal
import com.example.expensetrackerapp.data.model.DailyTotal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileWriter
import java.io.FileOutputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

// PDF generation imports

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state.asStateFlow()

    // Date range for reports (default: last 7 days)
    // Note: The DAO layer uses DATE() function to ensure proper date comparison
    // This fixes the issue where endDate expenses were excluded from reports
    private var reportStartDate: LocalDate = LocalDate.now().minusDays(6)
    private var reportEndDate: LocalDate = LocalDate.now()

    fun loadReportData() {
        loadReportData(reportStartDate, reportEndDate)
    }
    
    fun startPeriodicRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(30000) // Refresh every 30 seconds
                loadReportData()
            }
        }
    }

    fun loadReportData(startDate: LocalDate, endDate: LocalDate) {
        reportStartDate = startDate
        reportEndDate = endDate
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Log the date range being queried for debugging
                android.util.Log.d("ReportViewModel", "Loading report data for range: $startDate to $endDate")
                
                // Collect real-time data from repository
                expenseRepository.getDailyTotals(startDate, endDate).collect { dailyTotals ->
                    expenseRepository.getCategoryTotals(startDate, endDate).collect { categoryTotals ->
                        expenseRepository.getTotalAmountInRange(startDate, endDate).collect { totalAmount ->
                            expenseRepository.getTotalCountInRange(startDate, endDate).collect { totalCount ->
                                android.util.Log.d("ReportViewModel", "Loaded data: daily=${dailyTotals.size}, category=${categoryTotals.size}, total=₹$totalAmount, count=$totalCount")
                                _state.update { 
                                    it.copy(
                                        dailyTotals = dailyTotals,
                                        categoryTotals = categoryTotals,
                                        totalAmount = totalAmount,
                                        totalCount = totalCount,
                                        isLoading = false,
                                        error = null,
                                        lastUpdated = System.currentTimeMillis()
                                    )
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ReportViewModel", "Failed to load report data", e)
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load report data: ${e.message}"
                    )
                }
            }
        }
    }

    fun setDateRange(startDate: LocalDate, endDate: LocalDate) {
        loadReportData(startDate, endDate)
    }

    fun getLast7Days() {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(6)
        loadReportData(startDate, endDate)
    }

    fun getLast30Days() {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(29)
        loadReportData(startDate, endDate)
    }

    fun getLast3Months() {
        val endDate = LocalDate.now()
        val startDate = endDate.minusMonths(3)
        loadReportData(startDate, endDate)
    }

    fun getCurrentMonth() {
        val now = LocalDate.now()
        val startDate = now.withDayOfMonth(1)
        val endDate = now
        loadReportData(startDate, endDate)
    }
    
    // Helper method to get a more descriptive date range string
    fun getDateRangeDescription(): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        return "${reportStartDate.format(formatter)} - ${reportEndDate.format(formatter)}"
    }
    
    private fun validateDataConsistency(dailyTotals: List<DailyTotal>, categoryTotals: List<CategoryTotal>): Boolean {
        val totalFromDaily = dailyTotals.sumOf { it.amount }
        val totalFromCategory = categoryTotals.sumOf { it.amount }
        
        // Check if totals match (allow for small floating point differences)
        val difference = kotlin.math.abs(totalFromDaily - totalFromCategory)
        return difference < 0.01
    }
    
    fun getExpenseTrend(): ExpenseTrend {
        val dailyTotals = state.value.dailyTotals
        if (dailyTotals.size < 2) return ExpenseTrend.STABLE
        
        val recentDays = dailyTotals.take(3).sumOf { it.amount }
        val previousDays = dailyTotals.drop(3).take(3).sumOf { it.amount }
        
        if (previousDays == 0.0) return ExpenseTrend.STABLE
        
        val percentageChange = ((recentDays - previousDays) / previousDays) * 100
        
        return when {
            percentageChange > 10 -> ExpenseTrend.INCREASING
            percentageChange < -10 -> ExpenseTrend.DECREASING
            else -> ExpenseTrend.STABLE
        }
    }
    
    enum class ExpenseTrend {
        INCREASING, DECREASING, STABLE
    }
    
    fun checkExpenseAlerts(): List<ExpenseAlert> {
        val alerts = mutableListOf<ExpenseAlert>()
        val dailyTotals = state.value.dailyTotals
        val categoryTotals = state.value.categoryTotals
        
        // Check for high daily spending
        dailyTotals.forEach { daily ->
            if (daily.amount > 1000) { // Alert if daily spending > ₹1000
                alerts.add(ExpenseAlert.HighDailySpending(daily.date, daily.amount))
            }
        }
        
        // Check for high category spending
        categoryTotals.forEach { category ->
            if (category.amount > 5000) { // Alert if category spending > ₹5000
                alerts.add(ExpenseAlert.HighCategorySpending(category.category, category.amount))
            }
        }
        
        // Check for rapid spending increase
        if (getExpenseTrend() == ExpenseTrend.INCREASING) {
            alerts.add(ExpenseAlert.RapidSpendingIncrease)
        }
        
        // Return only top 2 most important alerts
        return alerts.sortedWith(
            compareByDescending<ExpenseAlert> { alert ->
                when (alert) {
                    is ExpenseAlert.HighDailySpending -> alert.amount
                    is ExpenseAlert.HighCategorySpending -> alert.amount
                    is ExpenseAlert.RapidSpendingIncrease -> 1000.0 // Medium priority
                }
            }
        ).take(2)
    }
    
    sealed class ExpenseAlert {
        data class HighDailySpending(val date: LocalDate, val amount: Double) : ExpenseAlert()
        data class HighCategorySpending(val category: ExpenseCategory, val amount: Double) : ExpenseAlert()
        object RapidSpendingIncrease : ExpenseAlert()
    }

    fun exportReport(context: Context) {
        viewModelScope.launch {
            try {
                val htmlContent = generatePDFContent()
                val file = createPDFFile(context, htmlContent, "expense_report.html")
                shareFile(context, file, "text/html")
            } catch (e: Exception) {
                _state.update { it.copy(error = "Export failed: ${e.message}") }
            }
        }
    }

    fun exportCSV(context: Context) {
        viewModelScope.launch {
            try {
                val csvContent = generateCSVContent()
                val file = createReportFile(context, csvContent, "expense_report.csv")
                shareFile(context, file, "text/csv")
            } catch (e: Exception) {
                _state.update { it.copy(error = "CSV export failed: ${e.message}") }
            }
        }
    }

    fun shareReport(context: Context) {
        viewModelScope.launch {
            try {
                val reportContent = generateReportContent()
                val file = createReportFile(context, reportContent, "expense_report.txt")
                shareFile(context, file, "text/plain")
            } catch (e: Exception) {
                _state.update { it.copy(error = "Share failed: ${e.message}") }
            }
        }
    }

    private fun generateReportContent(): String {
        val state = state.value
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        
        return buildString {
            appendLine("EXPENSE REPORT")
            appendLine("Period: ${reportStartDate.format(formatter)} - ${reportEndDate.format(formatter)}")
            appendLine("Generated on: ${LocalDate.now().format(formatter)}")
            appendLine("=".repeat(50))
            appendLine()
            
            appendLine("SUMMARY")
            appendLine("Total Amount: ₹${String.format("%.2f", state.totalAmount)}")
            appendLine("Total Expenses: ${state.totalCount}")
            appendLine()
            
            appendLine("DAILY BREAKDOWN")
            state.dailyTotals.forEach { daily ->
                appendLine("${daily.date.format(formatter)}: ₹${String.format("%.2f", daily.amount)} (${daily.count} expenses)")
            }
            appendLine()
            
            appendLine("CATEGORY BREAKDOWN")
            state.categoryTotals.forEach { category ->
                appendLine("${category.category.displayName}: ₹${String.format("%.2f", category.amount)} (${category.count} expenses)")
            }
        }
    }

    private fun generatePDFContent(): ByteArray {
        val state = state.value
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        
        // Create HTML content that can be opened as PDF
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Expense Report</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .title { font-size: 24px; font-weight: bold; margin-bottom: 10px; }
                    .subtitle { font-size: 16px; color: #666; }
                    .section { margin: 20px 0; }
                    .section-title { font-size: 18px; font-weight: bold; margin-bottom: 15px; color: #333; }
                    .item { margin: 10px 0; padding: 8px; background-color: #f5f5f5; border-radius: 4px; }
                    .total { font-weight: bold; color: #2196F3; }
                    .separator { border-top: 2px solid #ddd; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="header">
                    <div class="title">EXPENSE REPORT</div>
                    <div class="subtitle">Period: ${reportStartDate.format(formatter)} - ${reportEndDate.format(formatter)}</div>
                    <div class="subtitle">Generated on: ${LocalDate.now().format(formatter)}</div>
                </div>
                
                <div class="separator"></div>
                
                <div class="section">
                    <div class="section-title">SUMMARY</div>
                    <div class="item total">Total Amount: ₹${String.format("%.2f", state.totalAmount)}</div>
                    <div class="item total">Total Expenses: ${state.totalCount}</div>
                </div>
                
                <div class="section">
                    <div class="section-title">DAILY BREAKDOWN</div>
                    ${state.dailyTotals.joinToString("") { daily ->
                        "<div class='item'>${daily.date.format(formatter)}: ₹${String.format("%.2f", daily.amount)} (${daily.count} expenses)</div>"
                    }}
                </div>
                
                <div class="section">
                    <div class="section-title">CATEGORY BREAKDOWN</div>
                    ${state.categoryTotals.joinToString("") { category ->
                        "<div class='item'>${category.category.displayName}: ₹${String.format("%.2f", category.amount)} (${category.count} expenses)</div>"
                    }}
                </div>
            </body>
            </html>
        """.trimIndent()
        
        return htmlContent.toByteArray()
    }

    private fun generateCSVContent(): String {
        val state = state.value
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        
        return buildString {
            appendLine("Date,Amount,Count,Category,CategoryAmount,CategoryCount")
            
            state.dailyTotals.forEach { daily ->
                val categoryInfo = state.categoryTotals.joinToString(";") { 
                    "${it.category.name}:${String.format("%.2f", it.amount)}:${it.count}"
                }
                appendLine("${daily.date.format(formatter)},${daily.amount},${daily.count},$categoryInfo")
            }
        }
    }

    private fun createReportFile(context: Context, content: String, filename: String): File {
        val file = File(context.cacheDir, filename)
        FileWriter(file).use { writer ->
            writer.write(content)
        }
        return file
    }

    private fun createPDFFile(context: Context, content: ByteArray, filename: String): File {
        val file = File(context.cacheDir, filename)
        FileOutputStream(file).use { output ->
            output.write(content)
        }
        return file
    }

    private fun shareFile(context: Context, file: File, mimeType: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Expense Report")
            putExtra(Intent.EXTRA_TEXT, "Here's your expense report for ${reportStartDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))} - ${reportEndDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(shareIntent, "Share Expense Report")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}

data class ReportState(
    val dailyTotals: List<DailyTotal> = emptyList(),
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val totalAmount: Double = 0.0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
