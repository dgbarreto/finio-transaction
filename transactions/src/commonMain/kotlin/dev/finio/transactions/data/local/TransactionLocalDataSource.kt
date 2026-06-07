package dev.finio.transactions.data.local

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.finio.transactions.TransactionEntity
import dev.finio.transactions.db.FinioTransactionsDatabase
import dev.finio.transactions.domain.model.Transaction
import dev.finio.transactions.domain.model.TransactionCategory
import dev.finio.transactions.domain.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionLocalDataSource(database: FinioTransactionsDatabase){
    private val queries = database.transactionEntityQueries

    fun getAll(): Flow<List<Transaction>> =
        queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toDomain() } }

    fun insert(transaction: Transaction){
        queries.insert(
            id = transaction.id,
            title = transaction.title,
            amount = transaction.amount,
            type = transaction.type.name.lowercase(),
            category = transaction.category.name.lowercase(),
            date = transaction.date,
            notes = transaction.notes
        )
    }

    fun deleteById(id: String){
        queries.deleteById(id)
    }

    fun deleteAll(){
        queries.deleteAll()
    }

    private fun TransactionEntity.toDomain(): Transaction = Transaction(
        id = id,
        title = title,
        amount = amount,
        type = TransactionType.valueOf(type.uppercase()),
        category = TransactionCategory.valueOf(category.uppercase()),
        date = date,
        notes = notes
    )
}