package com.example.expensetrackerapp.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Main : Screen("main")
    object ExpenseEntry : Screen("expense_entry")
    object ExpenseList : Screen("expense_list")
}
