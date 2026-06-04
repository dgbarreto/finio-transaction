package dev.finio.transactions.data.remote

import dev.finio.transactions.data.dto.CreateTransactionRequestDto
import dev.finio.transactions.data.dto.TransactionDto
import dev.finio.transactions.data.dto.TransactionSummaryDto
import dev.finio.transactions.data.dto.UpdateTransactionRequestDto
import dev.finio.transactions.domain.model.TransactionCategory
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class TransactionRemoteDataSource(
    private val client: HttpClient,
    private val baseUrl: String
){
    suspend fun getTransactions(
        token: String,
        category: String? = null,
        type: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): List<TransactionDto>{
        val params = buildList {
            category?.let { add("category=$it") }
            type?.let { add("type=$it") }
            startDate?.let { add("startDate=$it") }
            endDate?.let { add("endDate=$it") }
        }.joinToString("&")

        val url = if(params.isNotEmpty()) "$baseUrl/transactions?$params" else "$baseUrl/transactions"

        return client.get(url){
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    suspend fun createTransaction(
        token: String,
        request: CreateTransactionRequestDto
    ): TransactionDto =
        client.post("$baseUrl/transactions"){
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun updateTransaction(
        token: String,
        id: String,
        request: UpdateTransactionRequestDto
    ): TransactionDto =
        client.put("$baseUrl/transactions/$id"){
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun deleteTransaction(token: String, id: String) =
        client.delete("$baseUrl/transactions/$id"){
            header(HttpHeaders.Authorization, "Bearer $token")
        }

    suspend fun getSummary(token: String): TransactionSummaryDto =
        client.get("$baseUrl/transactions/summary"){
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
}