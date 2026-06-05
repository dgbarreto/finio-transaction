package dev.finio.transactions.di

import dev.finio.transactions.data.local.TransactionLocalDataSource
import dev.finio.transactions.data.remote.TransactionRemoteDataSource
import dev.finio.transactions.data.repository.TransactionRepositoryImpl
import dev.finio.transactions.db.DatabaseDriverFactory
import dev.finio.transactions.db.FinioTransactionsDatabase
import dev.finio.transactions.domain.repository.TransactionRepository
import dev.finio.transactions.presentation.TransactionViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

fun transactionModule(
    baseUrl: String,
    driverFactory: DatabaseDriverFactory,
    tokenProvider: () -> String?
): Module = module {
    single {
        HttpClient{
            install(ContentNegotiation){
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging){
                level = LogLevel.BODY
                logger = object: Logger{
                    override fun log(message: String){
                        println("[FInio Transactions] $message")
                    }
                }
            }
        }
    }

    single{
        FinioTransactionsDatabase(driverFactory.createDriver())
    }

    single{ TransactionLocalDataSource(get()) }

    single{ TransactionRemoteDataSource(client = get(), baseUrl = baseUrl) }

    single<TransactionRepository>{
        TransactionRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            tokenProvider = tokenProvider
        )
    }

    factory { TransactionViewModel(repository = get()) }
}