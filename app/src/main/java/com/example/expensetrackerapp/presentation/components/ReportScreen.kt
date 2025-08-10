package com.example.expensetrackerapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetrackerapp.data.model.ExpenseCategory
import com.example.expensetrackerapp.data.model.DailyTotal
import com.example.expensetrackerapp.data.model.CategoryTotal
import com.example.expensetrackerapp.presentation.viewmodel.ReportViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter

// Helper functions
fun getCategoryColor(category: ExpenseCategory): Color {
    return when (category) {
        ExpenseCategory.FOOD -> Color(0xFFE57373)      // Red
        ExpenseCategory.TRAVEL -> Color(0xFF81C784)    // Green
        ExpenseCategory.STAFF -> Color(0xFF64B5F6)     // Blue
        ExpenseCategory.UTILITY -> Color(0xFFFFB74D)   // Orange
        ExpenseCategory.OTHER -> Color(0xFF9C27B0)    // Purple
    }
}

fun getCategoryIcon(category: ExpenseCategory): String {
    return when (category) {
        ExpenseCategory.FOOD -> "🍕"
        ExpenseCategory.TRAVEL -> "🚗"
        ExpenseCategory.STAFF -> "👥"
        ExpenseCategory.UTILITY -> "⚡"
        ExpenseCategory.OTHER -> "📦"
    }
}

@Composable
fun DateRangeSelector(
    onLast7Days: () -> Unit,
    onLast30Days: () -> Unit,
    onLast3Months: () -> Unit,
    onCurrentMonth: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Select Date Range",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onLast7Days,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("7 Days", style = MaterialTheme.typography.labelSmall)
                }
                
                OutlinedButton(
                    onClick = onLast30Days,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("30 Days", style = MaterialTheme.typography.labelSmall)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onLast3Months,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("3 Months", style = MaterialTheme.typography.labelSmall)
                }
                
                OutlinedButton(
                    onClick = onCurrentMonth,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("This Month", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun ReportSummaryCard(
    totalAmount: Double,
    totalCount: Int,
    period: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = period,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "₹${String.format("%.2f", totalAmount)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = "$totalCount expenses",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun DailyTotalItem(dailyTotal: DailyTotal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dailyTotal.date.format(DateTimeFormatter.ofPattern("EEEE")),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dailyTotal.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${String.format("%.2f", dailyTotal.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${dailyTotal.count} expenses",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CategoryTotalItem(categoryTotal: CategoryTotal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category icon/color
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(getCategoryColor(categoryTotal.category)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getCategoryIcon(categoryTotal.category),
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
                
                Column {
                    Text(
                        text = categoryTotal.category.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${categoryTotal.count} expenses",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = "₹${String.format("%.2f", categoryTotal.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ChartSection(
    dailyTotals: List<DailyTotal>,
    categoryTotals: List<CategoryTotal>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Expense Trends",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Mock bar chart for daily totals
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                dailyTotals.forEach { dailyTotal ->
                    val maxAmount = dailyTotals.maxOfOrNull { it.amount } ?: 1.0
                    val barHeight = (dailyTotal.amount / maxAmount * 100).toFloat()
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(barHeight.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = dailyTotal.date.format(DateTimeFormatter.ofPattern("dd")),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mock pie chart representation for categories
            Text(
                text = "Category Distribution",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                categoryTotals.forEach { categoryTotal ->
                    val totalAmount = categoryTotals.sumOf { it.amount }
                    val percentage = if (totalAmount > 0) (categoryTotal.amount / totalAmount * 100).toInt() else 0
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(getCategoryColor(categoryTotal.category)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = categoryTotal.category.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExportOptionsSection(
    onExportPDF: () -> Unit,
    onExportCSV: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Export Options",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onExportPDF,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PDF")
                }
                
                OutlinedButton(
                    onClick = onExportCSV,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CSV")
                }
                
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share")
                }
            }
        }
    }
}

@Composable
fun LastUpdatedIndicator(lastUpdated: Long) {
    val currentTime = System.currentTimeMillis()
    val timeDifference = currentTime - lastUpdated
    
    val timeAgo = when {
        timeDifference < 60000 -> "Just now"
        timeDifference < 3600000 -> "${timeDifference / 60000} minutes ago"
        timeDifference < 86400000 -> "${timeDifference / 3600000} hours ago"
        else -> "${timeDifference / 86400000} days ago"
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Last updated: $timeAgo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DataValidationIndicator(dailyTotal: Double, categoryTotal: Double) {
    val isValid = kotlin.math.abs(dailyTotal - categoryTotal) < 0.01
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (isValid) "Data consistency verified" else "Data inconsistency detected",
            style = MaterialTheme.typography.bodySmall,
            color = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun TrendIndicator(trend: ReportViewModel.ExpenseTrend) {
    val (icon, text, color) = when (trend) {
        ReportViewModel.ExpenseTrend.INCREASING -> Triple(
            Icons.Default.KeyboardArrowUp,
            "Expenses increasing",
            MaterialTheme.colorScheme.error
        )
        ReportViewModel.ExpenseTrend.DECREASING -> Triple(
            Icons.Default.KeyboardArrowDown,
            "Expenses decreasing",
            MaterialTheme.colorScheme.primary
        )
        ReportViewModel.ExpenseTrend.STABLE -> Triple(
            Icons.Default.Done,
            "Expenses stable",
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

@Composable
fun ExpenseAlertItem(alert: ReportViewModel.ExpenseAlert) {
    val (icon, text, color) = when (alert) {
        is ReportViewModel.ExpenseAlert.HighDailySpending -> Triple(
            Icons.Default.Warning,
            "High spending on ${alert.date.format(DateTimeFormatter.ofPattern("dd MMM"))}: ₹${String.format("%.2f", alert.amount)}",
            MaterialTheme.colorScheme.error
        )
        is ReportViewModel.ExpenseAlert.HighCategorySpending -> Triple(
            Icons.Default.Info,
            "High ${alert.category.displayName} spending: ₹${String.format("%.2f", alert.amount)}",
            MaterialTheme.colorScheme.error
        )
        is ReportViewModel.ExpenseAlert.RapidSpendingIncrease -> Triple(
            Icons.Default.KeyboardArrowUp,
            "Rapid spending increase detected",
            MaterialTheme.colorScheme.error
        )
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = color
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Simplified refresh handling without pull-to-refresh
    val isRefreshing = state.isLoading
    
    LaunchedEffect(Unit) {
        viewModel.loadReportData()
        viewModel.startPeriodicRefresh()
    }

    Scaffold(
        topBar = {
                    TopAppBar(
            title = { 
                Text(
                    text = "Expense Report",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            ),
            actions = {
                // Refresh button
                IconButton(onClick = { viewModel.loadReportData() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Data"
                    )
                }
//                    // Export button
//                    IconButton(onClick = { viewModel.exportReport(context) }) {
//                        Icon(
//                            imageVector = Icons.Default.Info,
//                            contentDescription = "Export Report"
//                        )
//                    }
//                    // Share button
//                    IconButton(onClick = { viewModel.shareReport(context) }) {
//                        Icon(
//                            imageVector = Icons.Default.Share,
//                            contentDescription = "Share Report"
//                        )
//                    }
            }
        )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Date Range Selection
                item {
                    DateRangeSelector(
                        onLast7Days = { viewModel.getLast7Days() },
                        onLast30Days = { viewModel.getLast30Days() },
                        onLast3Months = { viewModel.getLast3Months() },
                        onCurrentMonth = { viewModel.getCurrentMonth() }
                    )
                }
                
                // Last Updated Indicator
                item {
                    LastUpdatedIndicator(lastUpdated = state.lastUpdated)
                }
                
                // Data Validation Indicator
                if (state.dailyTotals.isNotEmpty() && state.categoryTotals.isNotEmpty()) {
                    item {
                        DataValidationIndicator(
                            dailyTotal = state.dailyTotals.sumOf { it.amount },
                            categoryTotal = state.categoryTotals.sumOf { it.amount }
                        )
                    }
                }
                
                // Trend Indicator
                if (state.dailyTotals.isNotEmpty()) {
                    item {
                        TrendIndicator(trend = viewModel.getExpenseTrend())
                    }
                }
                
                // Alerts Section
                val alerts = viewModel.checkExpenseAlerts()
                if (alerts.isNotEmpty()) {
                    item {
                        Text(
                            text = "Expense Alerts (Top 2)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    
                    items(alerts) { alert ->
                        ExpenseAlertItem(alert = alert)
                    }
                }
                
                // Loading State
                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                
                // Error State
                state.error?.let { error ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                
                // Report Summary Card
                item {
                    ReportSummaryCard(
                        totalAmount = state.totalAmount,
                        totalCount = state.totalCount,
                        period = "Selected Period"
                    )
                }
                
                // Daily Totals Section
                if (state.dailyTotals.isNotEmpty()) {
                    item {
                        Text(
                            text = "Daily Breakdown",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    // Daily totals list
                    items(state.dailyTotals) { dailyTotal ->
                        DailyTotalItem(dailyTotal = dailyTotal)
                    }
                }
                
                // Category Breakdown Section
                if (state.categoryTotals.isNotEmpty()) {
                    item {
                        Text(
                            text = "Category Breakdown",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    
                    // Category totals list
                    items(state.categoryTotals) { categoryTotal ->
                        CategoryTotalItem(categoryTotal = categoryTotal)
                    }
                }
                
                // Chart Section
                if (state.dailyTotals.isNotEmpty() || state.categoryTotals.isNotEmpty()) {
                    item {
                        ChartSection(
                            dailyTotals = state.dailyTotals,
                            categoryTotals = state.categoryTotals
                        )
                    }
                }
                
                // Export Options
                item {
                    ExportOptionsSection(
                        onExportPDF = { viewModel.exportReport(context) },
                        onExportCSV = { viewModel.exportCSV(context) },
                        onShare = { viewModel.shareReport(context) }
                    )
                }
            }
            
            // Loading indicator
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}


