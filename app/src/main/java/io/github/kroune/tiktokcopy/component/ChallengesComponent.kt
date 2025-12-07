package io.github.kroune.tiktokcopy.component

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChallengesComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val _state = MutableStateFlow(ChallengesState())
    val state: StateFlow<ChallengesState> = _state.asStateFlow()

    fun onEvent(event: ChallengesEvent) {
        when (event) {
            // События будут добавлены позже
            else -> {}
        }
    }

    data class ChallengesState(
        val isLoading: Boolean = false
    )

    sealed interface ChallengesEvent {
        // События будут добавлены позже
    }
}

