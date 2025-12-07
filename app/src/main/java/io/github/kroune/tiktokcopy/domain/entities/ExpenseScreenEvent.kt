package io.github.kroune.tiktokcopy.domain.entities

import java.time.LocalDateTime

sealed class ExpenseScreenEvent {
    data class UpdateAmount(val amount: String) : ExpenseScreenEvent()
    data class UpdateCategory(val category: String) : ExpenseScreenEvent()
    data class UpdateDescription(val description: String) : ExpenseScreenEvent()
    data class UpdateSelectedDate(val date: LocalDateTime) : ExpenseScreenEvent()
    data class UpdateDateFilter(val filter: DateFilter) : ExpenseScreenEvent()
    object ToggleDatePicker : ExpenseScreenEvent()
    object AddExpense : ExpenseScreenEvent()
    data class DeleteExpense(val id: String) : ExpenseScreenEvent()
    object ClearForm : ExpenseScreenEvent()
    object AnalyzeExpenses : ExpenseScreenEvent()
    object OpenChatWithAnalysis : ExpenseScreenEvent()
}
