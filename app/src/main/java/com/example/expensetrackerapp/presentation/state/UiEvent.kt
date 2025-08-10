package com.example.expensetrackerapp.presentation.state

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    object NavigateToExpenseList : UiEvent()
}
