package dev.finio.transactions.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val _id: String,
    val description: String,
    val amount: Double,
    val type: String,
    val category: String,
    val date: String,
    val notes: String? = null
)

@Serializable
data class CreateTransactionRequestDto(
    val description: String,
    val amount: Double,
    val type: String,
    val category: String,
    val date: String? = null,
    val notes: String? = null
)

@Serializable
data class UpdateTransactionRequestDto(
    val description: String? = null,
    val amount: Double? = null,
    val type: String? = null,
    val category: String? = null,
    val date: String? = null,
    val notes: String? = null
)

@Serializable
data class TransactionSummaryDto(
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double
)