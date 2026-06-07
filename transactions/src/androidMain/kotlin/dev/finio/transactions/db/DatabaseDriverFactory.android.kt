package dev.finio.transactions.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(private val context: Context){
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(FinioTransactionsDatabase.Schema, context, "finio_transactions.db")
}