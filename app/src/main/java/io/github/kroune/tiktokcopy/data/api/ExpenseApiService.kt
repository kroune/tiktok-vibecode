package io.github.kroune.tiktokcopy.data.api

import io.github.kroune.tiktokcopy.domain.entities.Expense
import io.github.kroune.tiktokcopy.domain.entities.ExpenseAnalysis
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ExpenseApiService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun sendExpenses(expenses: List<Expense>): Result<Unit> {
        // TODO: Временная заглушка - сервер не работает
        return try {
            // Реальный код (закомментирован):
            // client.post("https://your-backend-url.com/api/expenses") {
            //     contentType(ContentType.Application.Json)
            //     setBody(expenses)
            // }

            // Имитируем задержку сети
            kotlinx.coroutines.delay(500)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Анализирует список расходов
     *
     * Отправляет на сервер POST запрос с массивом расходов в формате JSON:
     * [
     *   {
     *     "id": "uuid",
     *     "amount": 100.0,
     *     "category": "Food",
     *     "description": "Groceries",
     *     "date": "2023-12-07T10:30:00"
     *   },
     *   ...
     * ]
     *
     * Ожидается ответ в одном из форматов:
     * 1. JSON объект ExpenseAnalysis:
     * {
     *   "totalAmount": 500.0,
     *   "categoryBreakdown": {"Food": 200.0, "Transport": 100.0, ...},
     *   "averageExpense": 100.0,
     *   "topCategory": "Food",
     *   "recommendations": ["Совет 1", "Совет 2"],
     *   "summary": "Общее описание анализа"
     * }
     *
     * 2. Простой текстовый ответ с результатами анализа
     *
     * @param expenses список расходов для анализа
     * @return Result с текстовым результатом анализа или ошибкой
     */
    suspend fun analyzeExpenses(expenses: List<Expense>): Result<String> {
        // TODO: Временная заглушка - сервер не работает
        return try {
            // Имитируем задержку сети
            kotlinx.coroutines.delay(1000)

            // Создаем мок-данные для анализа
            val totalAmount = expenses.sumOf { it.amount }
            val categoryBreakdown = expenses.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
            val averageExpense = if (expenses.isNotEmpty()) totalAmount / expenses.size else 0.0
            val topCategory = categoryBreakdown.maxByOrNull { it.value }?.key ?: "Нет данных"

            val mockAnalysis = ExpenseAnalysis(
                totalAmount = totalAmount,
                categoryBreakdown = categoryBreakdown,
                averageExpense = averageExpense,
                topCategory = topCategory,
                recommendations = listOf(
                    "Попробуйте сократить расходы на категорию '$topCategory'",
                    "Рассмотрите возможность использования бюджета",
                    "Отслеживайте ежедневные траты для лучшего контроля"
                ),
                summary = "Ваши расходы за период составили $totalAmount. Основная категория расходов - $topCategory."
            )

            val analysisResult = formatAnalysisResult(mockAnalysis)

            // Реальный код (закомментирован):
            // val response: HttpResponse = client.post("https://your-backend-url.com/api/expenses/analyze") {
            //     contentType(ContentType.Application.Json)
            //     setBody(expenses)
            // }
            // val responseText = response.bodyAsText()
            // val analysisResult = try {
            //     val analysis: ExpenseAnalysis = Json.decodeFromString(responseText)
            //     formatAnalysisResult(analysis)
            // } catch (e: Exception) {
            //     responseText
            // }

            Result.success(analysisResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Форматирует структурированный результат анализа в читаемый текст
     */
    private fun formatAnalysisResult(analysis: ExpenseAnalysis): String {
        val builder = StringBuilder()
        builder.appendLine("📊 Анализ расходов")
        builder.appendLine()
        builder.appendLine("💰 Общая сумма: ${analysis.totalAmount}")
        builder.appendLine("📈 Средний расход: ${analysis.averageExpense}")
        builder.appendLine("🏆 Топ категория: ${analysis.topCategory}")
        builder.appendLine()
        builder.appendLine("📂 Разбивка по категориям:")
        analysis.categoryBreakdown.forEach { (category, amount) ->
            builder.appendLine("  • $category: $amount")
        }
        builder.appendLine()
        if (analysis.recommendations.isNotEmpty()) {
            builder.appendLine("💡 Рекомендации:")
            analysis.recommendations.forEach { recommendation ->
                builder.appendLine("  • $recommendation")
            }
            builder.appendLine()
        }
        builder.appendLine("📝 ${analysis.summary}")

        return builder.toString()
    }

    /**
     * Отправляет сообщение в чат с AI для обсуждения расходов
     *
     * @param message сообщение пользователя
     * @param expenses контекст расходов для AI
     * @param chatHistory история предыдущих сообщений для контекста
     * @return Result с ответом AI или ошибкой
     */
    suspend fun sendChatMessage(
        message: String,
        expenses: List<Expense>,
        chatHistory: List<Pair<String, String>> = emptyList()
    ): Result<String> {
        return try {
            // Имитируем задержку сети
            kotlinx.coroutines.delay(1000)

            // Мок-ответ AI бота
            val aiResponse = generateMockAiResponse(message, expenses, chatHistory)

            // Реальный код (закомментирован):
            // val response: HttpResponse = client.post("https://your-backend-url.com/api/chat") {
            //     contentType(ContentType.Application.Json)
            //     setBody(mapOf(
            //         "message" to message,
            //         "expenses" to expenses,
            //         "chatHistory" to chatHistory
            //     ))
            // }
            // val aiResponse = response.bodyAsText()

            Result.success(aiResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateMockAiResponse(
        message: String,
        expenses: List<Expense>,
        chatHistory: List<Pair<String, String>>
    ): String {
        val totalAmount = expenses.sumOf { it.amount }
        val categoryBreakdown = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
        val topCategory = categoryBreakdown.maxByOrNull { it.value }?.key

        // Простая логика для генерации ответов на основе ключевых слов
        return when {
            message.contains("категор", ignoreCase = true) -> {
                val categories = categoryBreakdown.entries.joinToString("\n") {
                    "• ${it.key}: ${String.format("%.2f", it.value)} ₽"
                }
                "Вот разбивка ваших расходов по категориям:\n\n$categories\n\nБольше всего вы тратите на: $topCategory"
            }
            message.contains("сколько", ignoreCase = true) || message.contains("итого", ignoreCase = true) -> {
                "Общая сумма ваших расходов составляет ${String.format("%.2f", totalAmount)} ₽ за выбранный период. Это включает ${expenses.size} транзакций."
            }
            message.contains("совет", ignoreCase = true) || message.contains("рекоменд", ignoreCase = true) -> {
                """
                |Вот несколько советов по оптимизации ваших расходов:
                |
                |💡 Основные рекомендации:
                |1. Больше всего вы тратите на "$topCategory" - попробуйте найти способы экономии в этой категории
                |2. Отслеживайте ежедневные расходы для лучшего контроля
                |3. Установите лимиты на каждую категорию
                |4. Проанализируйте необязательные траты
                |
                |Хотите узнать больше о какой-то конкретной категории?
                """.trimMargin()
            }
            message.contains("сэконом", ignoreCase = true) || message.contains("сократ", ignoreCase = true) -> {
                """
                |Чтобы сократить расходы, рекомендую:
                |
                |✂️ В категории "$topCategory" (${String.format("%.2f", categoryBreakdown[topCategory] ?: 0.0)} ₽):
                |• Планируйте покупки заранее
                |• Ищите альтернативы или акции
                |• Отслеживайте импульсивные траты
                |
                |Установите цель - сократить расходы на 10-15% в этой категории!
                """.trimMargin()
            }
            message.contains("привет", ignoreCase = true) || message.contains("здравств", ignoreCase = true) -> {
                "Здравствуйте! 👋 Я AI-ассистент по анализу расходов. Я проанализировал ваши ${expenses.size} транзакций на общую сумму ${String.format("%.2f", totalAmount)} ₽. Чем могу помочь?"
            }
            message.contains("спасибо", ignoreCase = true) || message.contains("благодар", ignoreCase = true) -> {
                "Пожалуйста! Рад помочь с анализом ваших финансов. Если будут еще вопросы - обращайтесь! 😊"
            }
            else -> {
                """
                |Я проанализировал ваши расходы:
                |• Всего транзакций: ${expenses.size}
                |• Общая сумма: ${String.format("%.2f", totalAmount)} ₽
                |• Средний чек: ${String.format("%.2f", if (expenses.isNotEmpty()) totalAmount / expenses.size else 0.0)} ₽
                |• Главная категория расходов: $topCategory
                |
                |Я могу рассказать подробнее о категориях, дать советы по экономии или помочь с анализом конкретных трат. Что вас интересует?
                """.trimMargin()
            }
        }
    }

    fun close() {
        client.close()
    }
}

