package com.example.expensetrackerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val notes: String? = null,
    val receiptImageUri: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
