package com.example.expensetrackerapp.domain.usecase

import com.example.expensetrackerapp.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetTotalSpentUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(date: LocalDate): Flow<Double> {
        return repository.getTotalSpentByDate(date)
    }
}
