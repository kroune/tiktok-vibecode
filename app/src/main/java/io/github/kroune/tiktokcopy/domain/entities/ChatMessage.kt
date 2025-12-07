package io.github.kroune.tiktokcopy.domain.entities

import java.time.LocalDateTime

data class ChatMessage(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val isFromUser: Boolean,
    val text: String,
    val id: String = java.util.UUID.randomUUID().toString(),
)


