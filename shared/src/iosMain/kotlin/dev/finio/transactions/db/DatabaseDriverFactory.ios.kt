package dev.finio.transactions.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory{
    actual fun createDriver(): SqlDriver = NativeSqliteDriver(FinioTransactionsDatabase.Schema, "finio_transactions.db")
}