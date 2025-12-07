package io.github.kroune.tiktokcopy.domain.entities

import kotlinx.serialization.Serializable

/**
 * Модель данных для результата анализа расходов
 * Сервер должен возвращать JSON в этом формате
 */
@Serializable
data class ExpenseAnalysis(
    val totalAmount: Double,
    val categoryBreakdown: Map<String, Double>,
    val averageExpense: Double,
    val topCategory: String,
    val recommendations: List<String>,
    val summary: String
)

