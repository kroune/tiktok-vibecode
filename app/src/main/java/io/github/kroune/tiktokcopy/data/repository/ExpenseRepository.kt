package io.github.kroune.tiktokcopy.data.repository

import io.github.kroune.tiktokcopy.data.api.ExpenseApiService
import io.github.kroune.tiktokcopy.data.local.ExpenseDao
import io.github.kroune.tiktokcopy.data.local.ExpenseEntity
import io.github.kroune.tiktokcopy.domain.entities.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val apiService: ExpenseApiService = ExpenseApiService()
) {
    fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getExpenseById(id: String): Expense? {
        return expenseDao.getExpenseById(id)?.toDomain()
    }

    suspend fun insertExpense(expense: Expense): Result<Unit> {
        return try {
            // Сохраняем локально (пока без категории, с флагом isGeneratingCategory)
            expenseDao.insertExpense(ExpenseEntity.fromDomain(expense))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateAndUpdateCategory(expenseId: String, description: String, amount: Double): Result<String> {
        return try {
            // Генерируем категорию на сервере
            apiService.generateCategory(description, amount).fold(
                onSuccess = { category ->
                    // Обновляем расход с полученной категорией
                    val expense = expenseDao.getExpenseById(expenseId)
                    if (expense != null) {
                        val updatedExpense = expense.copy(
                            category = category,
                            isGeneratingCategory = false
                        )
                        expenseDao.insertExpense(updatedExpense)
                    }
                    Result.success(category)
                },
                onFailure = { error ->
                    // Если генерация не удалась, убираем флаг загрузки и ставим категорию по умолчанию
                    val expense = expenseDao.getExpenseById(expenseId)
                    if (expense != null) {
                        val updatedExpense = expense.copy(
                            category = "Прочее",
                            isGeneratingCategory = false
                        )
                        expenseDao.insertExpense(updatedExpense)
                    }
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertExpenses(expenses: List<Expense>): Result<Unit> {
        return try {
            val entities = expenses.map { ExpenseEntity.fromDomain(it) }
            expenseDao.insertExpenses(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteExpense(id: String): Result<Unit> {
        return try {
            expenseDao.deleteExpenseById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllExpenses(): Result<Unit> {
        return try {
            expenseDao.deleteAllExpenses()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analyzeExpenses(expenses: List<Expense>): Result<String> {
        return apiService.analyzeExpenses(expenses)
    }

    suspend fun sendChatMessage(
        message: String,
        expenses: List<Expense>,
        chatHistory: List<Pair<String, String>> = emptyList()
    ): Result<String> {
        return apiService.sendChatMessage(message, expenses, chatHistory)
    }
}

