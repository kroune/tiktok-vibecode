package io.github.kroune.tiktokcopy.component

import com.arkivanov.decompose.ComponentContext
import io.github.kroune.tiktokcopy.base.BaseComponent
import io.github.kroune.tiktokcopy.data.repository.ExpenseRepository
import io.github.kroune.tiktokcopy.domain.entities.DateFilter
import io.github.kroune.tiktokcopy.domain.entities.Expense
import io.github.kroune.tiktokcopy.domain.entities.ExpenseScreenEvent
import io.github.kroune.tiktokcopy.domain.entities.ExpenseScreenState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ExpenseScreenComponent(
    componentContext: ComponentContext,
    private val repository: ExpenseRepository,
    private val onNavigateToChat: (analysis: String?, expenses: List<Expense>) -> Unit = { _, _ -> }
) : BaseComponent<ExpenseScreenState, ExpenseScreenEvent>(
    ExpenseScreenState(),
    componentContext
) {
    init {
        // Загружаем данные из базы при инициализации
        launch {
            repository.getAllExpenses().collect { expenses ->
                val currentFilter = _state.value.dateFilter
                val filtered = filterExpensesByDate(expenses, currentFilter)
                _state.update { it.copy(expenses = expenses, filteredExpenses = filtered) }
            }
        }
    }
    override fun onEvent(event: ExpenseScreenEvent) {
        when (event) {
            is ExpenseScreenEvent.UpdateAmount -> {
                _state.update {
                    it.copy(amountInput = event.amount)
                }
            }


            is ExpenseScreenEvent.UpdateDescription -> {
                _state.update {
                    it.copy(descriptionInput = event.description)
                }
            }

            is ExpenseScreenEvent.UpdateSelectedDate -> {
                _state.update {
                    it.copy(selectedDate = event.date)
                }
            }

            is ExpenseScreenEvent.UpdateDateFilter -> {
                val currentExpenses = _state.value.expenses
                val filtered = filterExpensesByDate(currentExpenses, event.filter)
                _state.update {
                    it.copy(dateFilter = event.filter, filteredExpenses = filtered)
                }
            }

            ExpenseScreenEvent.ToggleDatePicker -> {
                _state.update {
                    it.copy(showDatePicker = !it.showDatePicker)
                }
            }

            ExpenseScreenEvent.AddExpense -> {
                val currentState = _state.value
                val amount = currentState.amountInput.toDoubleOrNull()

                if (amount == null || amount <= 0) {
                    _state.update {
                        it.copy(error = "Введите корректную сумму больше 0")
                    }
                    return
                }

                val description = currentState.descriptionInput.trim()
                if (description.isEmpty()) {
                    _state.update {
                        it.copy(error = "Введите описание расхода")
                    }
                    return
                }

                // Создаем расход с флагом isGeneratingCategory
                val newExpense = Expense(
                    amount = amount,
                    category = null, // Категория будет сгенерирована позже
                    description = description,
                    date = currentState.selectedDate,
                    isGeneratingCategory = true
                )

                _state.update {
                    it.copy(
                        amountInput = "",
                        descriptionInput = "",
                        selectedDate = LocalDateTime.now(),
                        error = null
                    )
                }

                // Сохранение в базу данных и генерация категории
                launch {
                    repository.insertExpense(newExpense).fold(
                        onSuccess = {
                            // Запускаем генерацию категории в фоне
                            launch {
                                repository.generateAndUpdateCategory(
                                    expenseId = newExpense.id,
                                    description = description,
                                    amount = amount
                                )
                                // Не обрабатываем результат - категория обновится через Flow
                            }
                        },
                        onFailure = { error ->
                            _state.update {
                                it.copy(error = error.message)
                            }
                        }
                    )
                }
            }

            is ExpenseScreenEvent.DeleteExpense -> {
                launch {
                    repository.deleteExpense(event.id).fold(
                        onSuccess = {
                            // Данные обновятся автоматически через Flow
                        },
                        onFailure = { error ->
                            _state.update {
                                it.copy(error = error.message)
                            }
                        }
                    )
                }
            }

            ExpenseScreenEvent.ClearForm -> {
                _state.update {
                    it.copy(
                        amountInput = "",
                        descriptionInput = "",
                        selectedDate = LocalDateTime.now()
                    )
                }
            }

            ExpenseScreenEvent.AnalyzeExpenses -> {
                val currentState = _state.value
                val filteredExpenses = currentState.filteredExpenses

                if (filteredExpenses.isEmpty()) {
                    _state.update {
                        it.copy(
                            error = "Нет расходов для анализа в выбранном периоде"
                        )
                    }
                    return
                }

                _state.update {
                    it.copy(
                        isAnalyzing = true,
                        error = null,
                        analysisResult = null
                    )
                }

                launch {
                    repository.analyzeExpenses(filteredExpenses).fold(
                        onSuccess = { result ->
                            _state.update {
                                it.copy(
                                    isAnalyzing = false,
                                    analysisResult = result,
                                    error = null
                                )
                            }
                            // Результат сохранен в состоянии, переход в чат будет по отдельному событию
                        },
                        onFailure = { error ->
                            _state.update {
                                it.copy(
                                    isAnalyzing = false,
                                    analysisResult = null,
                                    error = "Ошибка анализа: ${error.message ?: "Неизвестная ошибка"}"
                                )
                            }
                        }
                    )
                }
            }

            ExpenseScreenEvent.OpenChatWithAnalysis -> {
                val currentState = _state.value
                val analysisResult = currentState.analysisResult
                val filteredExpenses = currentState.filteredExpenses

                if (analysisResult != null && filteredExpenses.isNotEmpty()) {
                    onNavigateToChat(analysisResult, filteredExpenses)
                }
            }
        }
    }

    private fun filterExpensesByDate(expenses: List<Expense>, filter: DateFilter): List<Expense> {
        val now = LocalDateTime.now()
        return when (filter) {
            DateFilter.ALL -> expenses
            DateFilter.TODAY -> {
                val startOfDay = now.toLocalDate().atStartOfDay()
                val endOfDay = now.toLocalDate().plusDays(1).atStartOfDay()
                expenses.filter { it.date >= startOfDay && it.date < endOfDay }
            }
            DateFilter.WEEK -> {
                val weekAgo = now.minusWeeks(1)
                expenses.filter { it.date >= weekAgo }
            }
            DateFilter.MONTH -> {
                val monthAgo = now.minusMonths(1)
                expenses.filter { it.date >= monthAgo }
            }
        }
    }
}
