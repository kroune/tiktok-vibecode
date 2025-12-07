package io.github.kroune.tiktokcopy.domain.entities

sealed class ChatScreenEvent {
    data class UpdateInputText(val text: String) : ChatScreenEvent()
    object SendMessage : ChatScreenEvent()
    object NavigateBack : ChatScreenEvent()
}

