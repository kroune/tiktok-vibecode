package io.github.kroune.tiktokcopy.data.api

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Сервис для работы с API челленджей
 *
 * Эндпоинты:
 * - POST /challenges/generate - генерация новых челленджей с помощью AI
 */
class ChallengeApiService {

    companion object {
        private const val TAG = "ChallengeApiService"
        private const val BASE_URL = "https://vibecode.kroune.tech/api"
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d(TAG, "HTTP: $message")
                }
            }
            level = LogLevel.ALL
        }
    }

    /**
     * Генерирует новые челленджи с помощью AI
     *
     * POST /challenges/generate
     * Response:
     * {
     *   "challenges": ["Челлендж 1", "Челлендж 2", ...]
     * }
     *
     * @return Result со списком текстов челленджей или ошибкой
     */
    suspend fun generateChallenges(): Result<List<String>> {
        Log.d(TAG, "generateChallenges() called")

        return try {
            Log.d(TAG, "Making API request to: $BASE_URL/challenges/generate")
            val response: HttpResponse = client.post("$BASE_URL/challenges/generate") {
                contentType(ContentType.Application.Json)
            }

            val responseBody = response.bodyAsText()
            Log.d(TAG, "API response status: ${response.status}")
            Log.d(TAG, "API response body: $responseBody")

            val result = Json.decodeFromString<GenerateChallengesResponse>(responseBody)
            Log.d(TAG, "Successfully generated ${result.challenges.size} challenges")
            Result.success(result.challenges)
        } catch (e: Exception) {
            Log.e(TAG, "Error generating challenges", e)
            Log.e(TAG, "Error details: ${e.message}")
            Result.failure(e)
        }
    }

    @Serializable
    private data class GenerateChallengesResponse(
        val challenges: List<String>
    )
}

