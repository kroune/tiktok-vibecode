package io.github.kroune.tiktokcopy.domain.entities

import androidx.compose.runtime.Immutable
import java.time.LocalDateTime

@Immutable
data class ExpenseScreenState(
    val expenses: List<Expense> = emptyList(),
    val filteredExpenses: List<Expense> = emptyList(),
    val amountInput: String = "",
    val categoryInput: String = "",
    val descriptionInput: String = "",
    val selectedDate: LocalDateTime = LocalDateTime.now(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAnalyzing: Boolean = false,
    val analysisResult: String? = null,
    val dateFilter: DateFilter = DateFilter.ALL,
    val showDatePicker: Boolean = false
)
