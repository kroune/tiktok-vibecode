package io.github.kroune.tiktokcopy.domain.entities

data class ChatScreenState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val initialAnalysis: String? = null,
    val expenses: List<Expense> = emptyList()
)

