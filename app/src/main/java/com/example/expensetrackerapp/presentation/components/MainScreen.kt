package com.example.expensetrackerapp.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetrackerapp.presentation.viewmodel.ExpenseEntryViewModel
import com.example.expensetrackerapp.presentation.viewmodel.ExpenseListViewModel
import com.example.expensetrackerapp.data.model.Expense
import java.time.LocalDate

sealed class BottomTab(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Spend : BottomTab("spend", "Spend", Icons.Default.Add)
    object Summary : BottomTab("summary", "Summary", Icons.Default.List)
    object Reports : BottomTab("reports", "Reports", Icons.Default.Build)
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf<BottomTab>(BottomTab.Spend) }
    var selectedDateForExpense by remember { mutableStateOf<LocalDate?>(null) }
    var selectedExpenseForDetails by remember { mutableStateOf<com.example.expensetrackerapp.data.model.Expense?>(null) }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                listOf(BottomTab.Spend, BottomTab.Summary, BottomTab.Reports).forEach { tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Center
                            )
                        },
                        selected = selectedTab == tab,
                        onClick = {
                            selectedTab = tab
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            // Show expense details if an expense is selected
            if (selectedExpenseForDetails != null) {
                ExpenseDetailScreen(
                    expense = selectedExpenseForDetails!!,
                    onBackPressed = {
                        selectedExpenseForDetails = null
                    }
                )
            } else {
                when (selectedTab) {
                is BottomTab.Spend -> {
                    val viewModel: ExpenseEntryViewModel = hiltViewModel()
                    ExpenseEntryScreen(
                        viewModel = viewModel,
                        onNavigateToExpenseList = { /* No navigation needed in bottom tabs */ },
                        selectedDate = selectedDateForExpense
                    )
                }
                is BottomTab.Summary -> {
                    val viewModel: ExpenseListViewModel = hiltViewModel()
                    ExpenseListScreen(
                        viewModel = viewModel,
                        onAddExpense = { date ->
                            selectedDateForExpense = date
                            selectedTab = BottomTab.Spend
                        },
                        onViewExpenseDetails = { expense ->
                            selectedExpenseForDetails = expense
                        }
                    )
                }
                is BottomTab.Reports -> {
                    ReportScreen()
                }
                }
            }
        }
    }
}
