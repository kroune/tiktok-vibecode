package io.github.kroune.tiktokcopy.domain.entities

sealed interface WelcomeScreenEvent {
    data class OnPageChange(val page: Int) : WelcomeScreenEvent
    data object OnGetStarted : WelcomeScreenEvent
    data object OnSkip : WelcomeScreenEvent
}

