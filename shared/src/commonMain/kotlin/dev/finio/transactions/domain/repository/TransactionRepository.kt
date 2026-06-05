package dev.finio.transactions.domain.repository

import dev.finio.transactions.domain.model.Transaction
import dev.finio.transactions.domain.model.TransactionCategory
import dev.finio.transactions.domain.model.TransactionSummary
import dev.finio.transactions.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository{
    fun getTransactions(): Flow<List<Transaction>>
    suspend fun syncTransactions(
        category: TransactionCategory? = null,
        type: TransactionType? = null,
        startDate: String? = null,
        endDate: String? = null
    )
    suspend fun createTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        date: String? = null,
        notes: String? = null
    ): Transaction
    suspend fun updateTransaction(
        id: String,
        title: String? = null,
        amount: Double? = null,
        type: TransactionType? = null,
        category: TransactionCategory? = null,
        date: String? = null,
        notes: String? = null
    ): Transaction
    suspend fun deleteTransaction(id: String)
    suspend fun getSummary(): TransactionSummary
}