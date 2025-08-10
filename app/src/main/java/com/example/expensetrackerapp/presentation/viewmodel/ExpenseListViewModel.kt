package com.example.expensetrackerapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerapp.data.model.Expense
import com.example.expensetrackerapp.data.model.ExpenseCategory
import com.example.expensetrackerapp.domain.usecase.GetExpensesUseCase
import com.example.expensetrackerapp.presentation.state.ExpenseListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ExpenseListState())
    val state: StateFlow<ExpenseListState> = _state.asStateFlow()
    
    init {
        loadExpenses()
    }
    
    fun onDateSelected(date: LocalDate) {
        _state.value = _state.value.copy(selectedDate = date)
        loadExpenses()
    }
    
    fun onPreviousDay() {
        val currentDate = _state.value.selectedDate ?: LocalDate.now()
        val previousDate = currentDate.minusDays(1)
        _state.value = _state.value.copy(selectedDate = previousDate)
        loadExpenses()
    }
    
    fun onNextDay() {
        val currentDate = _state.value.selectedDate ?: LocalDate.now()
        val nextDate = currentDate.plusDays(1)
        val today = LocalDate.now()
        
        // Only allow navigation to next day if it's not in the future
        if (!nextDate.isAfter(today)) {
            _state.value = _state.value.copy(selectedDate = nextDate)
            loadExpenses()
        }
    }
    
    fun onTodayClick() {
        val today = LocalDate.now()
        _state.value = _state.value.copy(selectedDate = today)
        loadExpenses()
    }
    
    fun onGroupByCategoryToggle(groupByCategory: Boolean) {
        _state.value = _state.value.copy(groupByCategory = groupByCategory)
    }
    
    private fun loadExpenses() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)
                
                val selectedDate = _state.value.selectedDate ?: LocalDate.now()
                getExpensesUseCase(selectedDate).collect { expenses ->
                    val totalAmount = expenses.sumOf { it.amount }
                    val totalCount = expenses.size
                    
                    _state.value = _state.value.copy(
                        expenses = expenses,
                        totalAmount = totalAmount,
                        totalCount = totalCount,
                        isLoading = false,
                        error = null // Clear any previous errors
                    )
                }
            } catch (e: Exception) {
                val selectedDate = _state.value.selectedDate ?: LocalDate.now()
                _state.value = _state.value.copy(
                    isLoading = false,
                    expenses = emptyList(), // Clear expenses on error
                    totalAmount = 0.0,
                    totalCount = 0,
                    error = "Failed to load expenses for ${selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"))}: ${e.message ?: "Unknown error"}"
                )
                // Log the error for debugging
                android.util.Log.e("ExpenseListViewModel", "Error loading expenses for date: $selectedDate", e)
            }
        }
    }
    
    fun retryLoading() {
        loadExpenses()
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
