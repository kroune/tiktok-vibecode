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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Сервис для работы с API расходов
 *
 * Базовый URL API: https://your-backend-url.com/api
 *
 * Эндпоинты:
 * - POST /expenses/generate-category - генерация категории для расхода
 * - POST /expenses/analyze - анализ списка расходов
 * - POST /chat - чат с AI ассистентом
 */
class ExpenseApiService {

    companion object {
        private const val BASE_URL = "https://vibecode.kroune.tech/api"
        private const val MOCK_MODE = false // Включен режим mock данных для разработки
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
    }

    /**
     * Генерирует категорию для расхода на основе описания и суммы
     *
     * POST /expenses/generate-category
     * Request body:
     * {
     *   "description": "Купил продукты в магазине",
     *   "amount": 1500.0
     * }
     *
     * Response:
     * {
     *   "category": "Продукты"
     * }
     *
     * @param description описание расхода
     * @param amount сумма расхода
     * @return Result с категорией или ошибкой
     */
    suspend fun generateCategory(description: String, amount: Double): Result<String> {
        return if (MOCK_MODE) {
            generateMockCategory(description, amount)
        } else {
            try {
                val response: HttpResponse = client.post("$BASE_URL/expenses/generate-category") {
                    contentType(ContentType.Application.Json)
                    setBody(GenerateCategoryRequest(description, amount))
                }
                val result = Json.decodeFromString<GenerateCategoryResponse>(response.bodyAsText())
                Result.success(result.category)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    /**
     * Анализирует список расходов
     *
     * POST /expenses/analyze
     * Request body: массив объектов Expense
     *
     * Response:
     * {
     *   "totalAmount": 500.0,
     *   "categoryBreakdown": {"Food": 200.0, "Transport": 100.0, ...},
     *   "averageExpense": 100.0,
     *   "topCategory": "Food",
     *   "recommendations": ["Совет 1", "Совет 2"],
     *   "summary": "Общее описание анализа"
     * }
     *
     * @param expenses список расходов для анализа
     * @return Result с объектом ExpenseAnalysis или ошибкой
     */
    suspend fun analyzeExpenses(expenses: List<Expense>): Result<ExpenseAnalysis> {
        return if (MOCK_MODE) {
            generateMockAnalysis(expenses)
        } else {
            try {
                val response: HttpResponse = client.post("$BASE_URL/expenses/analyze") {
                    contentType(ContentType.Application.Json)
                    setBody(expenses)
                }
                val analysis = Json.decodeFromString<ExpenseAnalysis>(response.bodyAsText())
                Result.success(analysis)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Отправляет сообщение в чат с AI для обсуждения расходов
     *
     * POST /chat
     * Request body:
     * {
     *   "message": "Расскажи о моих расходах",
     *   "expenses": [...],
     *   "chatHistory": [["user message", "ai response"], ...]
     * }
     *
     * Response:
     * {
     *   "message": "Ответ от AI ассистента"
     * }
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
        return if (MOCK_MODE) {
            generateMockChatResponse(message, expenses, chatHistory)
        } else {
            try {
                val response: HttpResponse = client.post("$BASE_URL/chat") {
                    contentType(ContentType.Application.Json)
                    setBody(ChatRequest(message, expenses, chatHistory))
                }
                val chatResponse = Json.decodeFromString<ChatResponse>(response.bodyAsText())
                Result.success(chatResponse.message)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ==================== Mock функции ====================

    private suspend fun generateMockCategory(description: String, amount: Double): Result<String> {
        mockNetworkDelay(1000)

        val category = when {
            description.contains("продукт", ignoreCase = true) ||
            description.contains("магазин", ignoreCase = true) ||
            description.contains("еда", ignoreCase = true) ||
            description.contains("кафе", ignoreCase = true) ||
            description.contains("ресторан", ignoreCase = true) -> "Продукты и питание"

            description.contains("транспорт", ignoreCase = true) ||
            description.contains("такси", ignoreCase = true) ||
            description.contains("метро", ignoreCase = true) ||
            description.contains("автобус", ignoreCase = true) ||
            description.contains("бензин", ignoreCase = true) -> "Транспорт"

            description.contains("развлеч", ignoreCase = true) ||
            description.contains("кино", ignoreCase = true) ||
            description.contains("театр", ignoreCase = true) ||
            description.contains("концерт", ignoreCase = true) -> "Развлечения"

            description.contains("здоровь", ignoreCase = true) ||
            description.contains("аптека", ignoreCase = true) ||
            description.contains("врач", ignoreCase = true) ||
            description.contains("лекарств", ignoreCase = true) -> "Здоровье"

            description.contains("одежд", ignoreCase = true) ||
            description.contains("обув", ignoreCase = true) ||
            description.contains("магазин одежды", ignoreCase = true) -> "Одежда и обувь"

            description.contains("коммунал", ignoreCase = true) ||
            description.contains("свет", ignoreCase = true) ||
            description.contains("вода", ignoreCase = true) ||
            description.contains("газ", ignoreCase = true) ||
            description.contains("интернет", ignoreCase = true) -> "Коммунальные услуги"

            description.contains("образован", ignoreCase = true) ||
            description.contains("курс", ignoreCase = true) ||
            description.contains("книг", ignoreCase = true) ||
            description.contains("учеб", ignoreCase = true) -> "Образование"

            amount > 10000 -> "Крупные покупки"
            amount < 100 -> "Мелкие расходы"

            else -> "Прочее"
        }

        return Result.success(category)
    }

    private suspend fun generateMockAnalysis(expenses: List<Expense>): Result<ExpenseAnalysis> {
        mockNetworkDelay(1000)

        val totalAmount = expenses.sumOf { it.amount }
        val categoryBreakdown = expenses
            .filter { it.category != null }
            .groupBy { it.category!! }
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
            summary = "Ваши расходы за период составили ${String.format("%.2f", totalAmount)} ₽. Основная категория расходов - $topCategory."
        )

        return Result.success(mockAnalysis)
    }

    private suspend fun generateMockChatResponse(
        message: String,
        expenses: List<Expense>,
        chatHistory: List<Pair<String, String>>
    ): Result<String> {
        mockNetworkDelay(1000)

        val totalAmount = expenses.sumOf { it.amount }
        val categoryBreakdown = expenses
            .filter { it.category != null }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
        val topCategory = categoryBreakdown.maxByOrNull { it.value }?.key

        val response = when {
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

        return Result.success(response)
    }

    private suspend fun mockNetworkDelay(ms: Long) {
        kotlinx.coroutines.delay(ms)
    }


    fun close() {
        client.close()
    }
}

// ==================== Data Transfer Objects ====================

@Serializable
private data class GenerateCategoryRequest(
    val description: String,
    val amount: Double
)

@Serializable
private data class GenerateCategoryResponse(
    val category: String
)

@Serializable
private data class ChatRequest(
    val message: String,
    val expenses: List<Expense>,
    val chatHistory: List<Pair<String, String>>
)

@Serializable
private data class ChatResponse(
    val message: String
)

