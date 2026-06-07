package dev.finio.transactions.data.repository

import dev.finio.transactions.data.dto.CreateTransactionRequestDto
import dev.finio.transactions.data.dto.UpdateTransactionRequestDto
import dev.finio.transactions.data.local.TransactionLocalDataSource
import dev.finio.transactions.data.mapper.toDomain
import dev.finio.transactions.data.remote.TransactionRemoteDataSource
import dev.finio.transactions.domain.model.Transaction
import dev.finio.transactions.domain.model.TransactionCategory
import dev.finio.transactions.domain.model.TransactionSummary
import dev.finio.transactions.domain.model.TransactionType
import dev.finio.transactions.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(
    private val remoteDataSource: TransactionRemoteDataSource,
    private val localDataSource: TransactionLocalDataSource,
    private val tokenProvider: () -> String?
): TransactionRepository{
    override fun getTransactions(): Flow<List<Transaction>> =
        localDataSource.getAll()

    override suspend fun syncTransactions(
        category: TransactionCategory?,
        type: TransactionType?,
        startDate: String?,
        endDate: String?
    ) {
        val token = tokenProvider() ?: return

        val remote = remoteDataSource.getTransactions(
            token = token,
            category = category?.name?.lowercase(),
            type = type?.name?.lowercase(),
            startDate = startDate,
            endDate = endDate
        )
        localDataSource.deleteAll()
        remote.forEach {
            localDataSource.insert(it.toDomain())
        }
    }

    override suspend fun createTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        date: String?,
        notes: String?
    ): Transaction {
        val token = tokenProvider() ?: error("Not authenticated")
        val response = remoteDataSource.createTransaction(
            token = token,
            request = CreateTransactionRequestDto(
                title = title,
                amount = amount,
                type = type.name.lowercase(),
                category = category.name.lowercase(),
                date = date,
                notes = notes
            )
        )
        val transaction = response.toDomain()
        localDataSource.insert(transaction)
        return transaction
    }

    override suspend fun updateTransaction(
        id: String,
        title: String?,
        amount: Double?,
        type: TransactionType?,
        category: TransactionCategory?,
        date: String?,
        notes: String?
    ): Transaction {
        val token = tokenProvider() ?: error("Not authenticated")
        val response = remoteDataSource.updateTransaction(
            token = token,
            id = id,
            request = UpdateTransactionRequestDto(
                title = title,
                amount = amount,
                type = type?.name?.lowercase(),
                category = category?.name?.lowercase(),
                date = date,
                notes = notes
            )
        )
        val transaction = response.toDomain()
        localDataSource.insert(transaction)
        return transaction
    }

    override suspend fun deleteTransaction(id: String) {
        val token = tokenProvider() ?: error("Not authenticated")
        remoteDataSource.deleteTransaction(token = token, id = id)
        localDataSource.deleteById(id)
    }

    override suspend fun getSummary(): TransactionSummary {
        val token = tokenProvider() ?: error("Not authenticated")
        return remoteDataSource.getSummary(token).toDomain()
    }
}