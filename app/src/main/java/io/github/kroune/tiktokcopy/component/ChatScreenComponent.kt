package io.github.kroune.tiktokcopy.component

import com.arkivanov.decompose.ComponentContext
import io.github.kroune.tiktokcopy.base.BaseComponent
import io.github.kroune.tiktokcopy.data.repository.ExpenseRepository
import io.github.kroune.tiktokcopy.domain.entities.ChatMessage
import io.github.kroune.tiktokcopy.domain.entities.ChatScreenEvent
import io.github.kroune.tiktokcopy.domain.entities.ChatScreenState
import io.github.kroune.tiktokcopy.domain.entities.Expense
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ChatScreenComponent(
    componentContext: ComponentContext,
    private val repository: ExpenseRepository,
    initialAnalysis: String?,
    expenses: List<Expense>,
    private val onNavigateBack: () -> Unit
) : BaseComponent<ChatScreenState, ChatScreenEvent>(
    ChatScreenState(
        initialAnalysis = initialAnalysis,
        expenses = expenses
    ),
    componentContext
) {
    init {
        // Добавляем начальное сообщение от AI с анализом
        if (initialAnalysis != null) {
            _state.update {
                it.copy(
                    messages = listOf(
                        ChatMessage(
                            timestamp = LocalDateTime.now(),
                            isFromUser = false,
                            text = initialAnalysis
                        )
                    )
                )
            }
        }
    }

    override fun onEvent(event: ChatScreenEvent) {
        when (event) {
            is ChatScreenEvent.UpdateInputText -> {
                _state.update {
                    it.copy(inputText = event.text)
                }
            }

            ChatScreenEvent.SendMessage -> {
                val currentState = _state.value
                val messageText = currentState.inputText.trim()

                if (messageText.isEmpty()) {
                    return
                }

                // Добавляем сообщение пользователя
                val userMessage = ChatMessage(
                    timestamp = LocalDateTime.now(),
                    isFromUser = true,
                    text = messageText
                )

                _state.update {
                    it.copy(
                        messages = it.messages + userMessage,
                        inputText = "",
                        isLoading = true,
                        error = null
                    )
                }

                // Отправляем сообщение на сервер
                launch {
                    // Формируем историю чата для контекста
                    val chatHistory = currentState.messages.map { msg ->
                        if (msg.isFromUser) {
                            "user" to msg.text
                        } else {
                            "assistant" to msg.text
                        }
                    }

                    repository.sendChatMessage(
                        message = messageText,
                        expenses = currentState.expenses,
                        chatHistory = chatHistory
                    ).fold(
                        onSuccess = { aiResponse ->
                            val aiMessage = ChatMessage(
                                timestamp = LocalDateTime.now(),
                                isFromUser = false,
                                text = aiResponse
                            )
                            _state.update {
                                it.copy(
                                    messages = it.messages + aiMessage,
                                    isLoading = false
                                )
                            }
                        },
                        onFailure = { error ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Ошибка отправки сообщения: ${error.message ?: "Неизвестная ошибка"}"
                                )
                            }
                        }
                    )
                }
            }

            ChatScreenEvent.NavigateBack -> {
                onNavigateBack()
            }
        }
    }
}

