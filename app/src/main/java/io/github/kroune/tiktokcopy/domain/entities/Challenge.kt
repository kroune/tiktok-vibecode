package io.github.kroune.tiktokcopy.domain.entities

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Immutable
@Serializable
data class Challenge(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isCompleted: Boolean = false,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val completedAt: LocalDateTime? = null
)

