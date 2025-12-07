package io.github.kroune.tiktokcopy.domain.entities

import androidx.compose.runtime.Immutable

@Immutable
data class ExpenseScreenState(
    val expenses: List<Expense> = emptyList(),
    val amountInput: String = "",
    val categoryInput: String = "",
    val descriptionInput: String = ""
)

