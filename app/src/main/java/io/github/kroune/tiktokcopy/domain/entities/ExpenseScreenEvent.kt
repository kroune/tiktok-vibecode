package io.github.kroune.tiktokcopy.domain.entities

sealed class ExpenseScreenEvent {
    data class UpdateAmount(val amount: String) : ExpenseScreenEvent()
    data class UpdateCategory(val category: String) : ExpenseScreenEvent()
    data class UpdateDescription(val description: String) : ExpenseScreenEvent()
    object AddExpense : ExpenseScreenEvent()
    data class DeleteExpense(val id: String) : ExpenseScreenEvent()
    object ClearForm : ExpenseScreenEvent()
}

