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
            // Сохраняем локально
            expenseDao.insertExpense(ExpenseEntity.fromDomain(expense))

            // Пытаемся отправить на сервер
            apiService.sendExpenses(listOf(expense)).fold(
                onSuccess = { Result.success(Unit) },
                onFailure = {
                    // Даже если отправка на сервер не удалась, локально данные сохранены
                    Result.success(Unit)
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

            // Пытаемся отправить на сервер
            apiService.sendExpenses(expenses).fold(
                onSuccess = { Result.success(Unit) },
                onFailure = { Result.success(Unit) }
            )
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

