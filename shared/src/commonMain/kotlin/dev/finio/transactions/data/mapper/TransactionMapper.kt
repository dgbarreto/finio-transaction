package dev.finio.transactions.data.mapper

import dev.finio.transactions.data.dto.TransactionDto
import dev.finio.transactions.data.dto.TransactionSummaryDto
import dev.finio.transactions.domain.model.Transaction
import dev.finio.transactions.domain.model.TransactionCategory
import dev.finio.transactions.domain.model.TransactionSummary
import dev.finio.transactions.domain.model.TransactionType

fun TransactionDto.toDomain(): Transaction = Transaction(
    id = _id,
    title = title,
    amount = amount,
    type = TransactionType.valueOf(type.uppercase()),
    category = TransactionCategory.valueOf(category.uppercase()),
    date = date,
    notes = notes
)

fun TransactionSummaryDto.toDomain(): TransactionSummary = TransactionSummary(
    totalIncome = totalIncome,
    totalExpenses = totalExpenses,
    balance = balance
)