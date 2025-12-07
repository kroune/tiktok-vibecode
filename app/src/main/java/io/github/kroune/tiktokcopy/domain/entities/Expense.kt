package io.github.kroune.tiktokcopy.domain.entities

import androidx.compose.runtime.Immutable
import java.time.LocalDateTime

@Immutable
data class Expense(
    val id: String = java.util.UUID.randomUUID().toString(),
    val amount: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val date: LocalDateTime = LocalDateTime.now()
)

