package dev.finio.transactions.domain.model

data class TransactionSummary(
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double
)