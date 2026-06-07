package dev.finio.transactions.domain.model

data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory,
    val date: String,
    val notes: String?
)