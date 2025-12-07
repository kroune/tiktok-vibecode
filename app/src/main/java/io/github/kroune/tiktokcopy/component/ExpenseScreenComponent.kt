package io.github.kroune.tiktokcopy.component

import com.arkivanov.decompose.ComponentContext
import io.github.kroune.tiktokcopy.base.BaseComponent
import io.github.kroune.tiktokcopy.domain.entities.Expense
import io.github.kroune.tiktokcopy.domain.entities.ExpenseScreenEvent
import io.github.kroune.tiktokcopy.domain.entities.ExpenseScreenState
import kotlinx.coroutines.flow.update

class ExpenseScreenComponent(
    componentContext: ComponentContext
) : BaseComponent<ExpenseScreenState, ExpenseScreenEvent>(
    ExpenseScreenState(),
    componentContext
) {

    override fun onEvent(event: ExpenseScreenEvent) {
        when (event) {
            is ExpenseScreenEvent.UpdateAmount -> {
                _state.update {
                    it.copy(amountInput = event.amount)
                }
            }

            is ExpenseScreenEvent.UpdateCategory -> {
                _state.update {
                    it.copy(categoryInput = event.category)
                }
            }

            is ExpenseScreenEvent.UpdateDescription -> {
                _state.update {
                    it.copy(descriptionInput = event.description)
                }
            }

            ExpenseScreenEvent.AddExpense -> {
                val currentState = _state.value
                if (currentState.amountInput.isNotEmpty() && currentState.categoryInput.isNotEmpty()) {
                    val newExpense = Expense(
                        amount = currentState.amountInput.toDoubleOrNull() ?: 0.0,
                        category = currentState.categoryInput,
                        description = currentState.descriptionInput
                    )
                    _state.update {
                        it.copy(
                            expenses = it.expenses + newExpense,
                            amountInput = "",
                            categoryInput = "",
                            descriptionInput = ""
                        )
                    }
                }
            }

            is ExpenseScreenEvent.DeleteExpense -> {
                _state.update {
                    it.copy(
                        expenses = it.expenses.filter { expense -> expense.id != event.id }
                    )
                }
            }

            ExpenseScreenEvent.ClearForm -> {
                _state.update {
                    it.copy(
                        amountInput = "",
                        categoryInput = "",
                        descriptionInput = ""
                    )
                }
            }
        }
    }
}

