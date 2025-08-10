package com.example.expensetrackerapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerapp.data.model.Expense
import com.example.expensetrackerapp.data.model.ExpenseCategory
import com.example.expensetrackerapp.domain.usecase.AddExpenseUseCase
import com.example.expensetrackerapp.domain.usecase.GetTotalSpentUseCase
import com.example.expensetrackerapp.presentation.state.ExpenseEntryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import com.example.expensetrackerapp.presentation.state.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDate

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val getTotalSpentUseCase: GetTotalSpentUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ExpenseEntryState())
    val state: StateFlow<ExpenseEntryState> = _state.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    
    init {
        loadTotalSpentForDate(null) // Load today's total by default
    }
    
    fun onTitleChange(title: String) {
        _state.value = _state.value.copy(title = title)
    }
    
    fun onAmountChange(amount: String) {
        _state.value = _state.value.copy(amount = amount)
    }
    
    fun onNotesChanged(notes: String) {
        if (notes.length <= 100) {
            _state.value = _state.value.copy(notes = notes)
        }
    }

    fun onCategoryChanged(category: ExpenseCategory) {
        _state.value = _state.value.copy(category = category)
    }
    
    fun setSelectedDate(date: LocalDate?) {
        _state.value = _state.value.copy(selectedDate = date)
        // Reload total spent for the new date
        loadTotalSpentForDate(date)
    }
    
    private fun loadTotalSpentForDate(date: LocalDate?) {
        val targetDate = date ?: LocalDate.now()
        viewModelScope.launch {
            try {
                getTotalSpentUseCase(targetDate).collect { total ->
                    _state.value = _state.value.copy(totalSpentToday = total)
                }
            } catch (e: Exception) {
                // Handle error silently, keep previous value
                android.util.Log.e("ExpenseEntryViewModel", "Error loading total spent for date: $targetDate", e)
            }
        }
    }

    fun submitExpense() {
        val currentState = _state.value
        
        if (currentState.title.isBlank()) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowToast("Please enter a title"))
            }
            return
        }
        
        if (currentState.amount.isBlank()) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowToast("Please enter an amount"))
            }
            return
        }
        
        val amount = currentState.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowToast("Please enter a valid amount"))
            }
            return
        }
        
        viewModelScope.launch {
            try {
                // Use selected date if available, otherwise use current time
                val createdAt = currentState.selectedDate?.let { date ->
                    LocalDateTime.of(date, LocalDateTime.now().toLocalTime())
                } ?: LocalDateTime.now()
                
                addExpenseUseCase(
                    Expense(
                        id = 0,
                        title = currentState.title,
                        amount = amount,
                        category = currentState.category,
                        notes = currentState.notes.ifBlank { null },
                        receiptImageUri = currentState.receiptImagePath,
                        createdAt = createdAt
                    )
                )
                
                _uiEvent.emit(UiEvent.ShowToast("Expense added successfully!"))
                _uiEvent.emit(UiEvent.NavigateToExpenseList)
                
                // Reset form
                _state.value = ExpenseEntryState()
                
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowToast("Failed to add expense: ${e.message}"))
            }
        }
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    
    fun clearSuccess() {
        _state.value = _state.value.copy(isSuccess = false)
    }
    
    fun setReceiptImage(imagePath: String?) {
        _state.value = _state.value.copy(receiptImagePath = imagePath)
    }
    
    fun clearReceiptImage() {
        _state.value = _state.value.copy(receiptImagePath = null)
    }
}
