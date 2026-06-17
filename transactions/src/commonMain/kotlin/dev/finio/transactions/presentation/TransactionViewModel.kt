package dev.finio.transactions.presentation

import dev.finio.transactions.domain.model.Transaction
import dev.finio.transactions.domain.model.TransactionCategory
import dev.finio.transactions.domain.model.TransactionSummary
import dev.finio.transactions.domain.model.TransactionType
import dev.finio.transactions.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed class TransactionState{
    object Loading: TransactionState()
    data class Success(val transactions: List<Transaction>): TransactionState()
    data class Error(val message: String): TransactionState()
}

class TransactionViewModel(
    private val repository: TransactionRepository
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow<TransactionState>(TransactionState.Loading)
    val state: StateFlow<TransactionState> = _state.asStateFlow()

    private val _summary = MutableStateFlow<TransactionSummary?>(null)
    val summary: StateFlow<TransactionSummary?> = _summary.asStateFlow()

    init {
        observeTransactions()
        sync()
    }

    private fun observeTransactions(){
        repository.getTransactions()
            .onEach { _state.value = TransactionState.Success(it) }
            .launchIn(viewModelScope)
    }

    fun sync(
        category: TransactionCategory? = null,
        type: TransactionType? = null,
        startDate: String? = null,
        endDate: String? = null
    ){
        viewModelScope.launch {
            try {
                repository.syncTransactions(category, type, startDate, endDate)
            }catch (e: Exception){
                _state.value = TransactionState.Error(e.message ?: "Sync failed")
            }
        }
    }

    fun createTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        date: String? = null,
        notes: String? = null
    ){
        viewModelScope.launch {
            try {
                repository.createTransaction(title, amount, type, category, date, notes)
            } catch (e: Exception){
                _state.value = TransactionState.Error(e.message ?: "Create failed")
            }
        }
    }

    fun updateTransaction(
        id: String,
        title: String?,
        amount: Double?,
        type: TransactionType?,
        category: TransactionCategory?,
        date: String? = null,
        notes: String? = null
    ){
        viewModelScope.launch {
            try{
                repository.updateTransaction(
                    id =  id,
                    title = title,
                    amount = amount,
                    type = type,
                    category = category,
                    date = date,
                    notes = notes
                )
            } catch (e: Exception){
                _state.value = TransactionState.Error(e.message ?: "Update failed")
            }
        }
    }

    fun deleteTransaction(id: String){
        viewModelScope.launch {
            try {
                repository.deleteTransaction(id)
            } catch (e: Exception){
                _state.value = TransactionState.Error(e.message ?: "Delete failed")
            }
        }
    }

    fun loadSummary(){
        viewModelScope.launch {
            try {
                _summary.value = repository.getSummary()
            } catch (e: Exception){
                _state.value = TransactionState.Error(e.message ?: "Summaty failed")
            }
        }
    }
}