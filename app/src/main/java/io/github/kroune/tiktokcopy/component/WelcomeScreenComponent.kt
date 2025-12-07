package io.github.kroune.tiktokcopy.component

import com.arkivanov.decompose.ComponentContext
import io.github.kroune.tiktokcopy.base.BaseComponent
import io.github.kroune.tiktokcopy.data.preferences.FirstLaunchManager
import io.github.kroune.tiktokcopy.domain.entities.WelcomeScreenEvent
import io.github.kroune.tiktokcopy.domain.entities.WelcomeScreenState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WelcomeScreenComponent(
    componentContext: ComponentContext,
    private val firstLaunchManager: FirstLaunchManager,
    private val onComplete: () -> Unit
) : BaseComponent<WelcomeScreenState, WelcomeScreenEvent>(
    WelcomeScreenState(),
    componentContext
) {
    override fun onEvent(event: WelcomeScreenEvent) {
        when (event) {
            is WelcomeScreenEvent.OnPageChange -> {
                _state.update {
                    it.copy(currentPage = event.page)
                }
            }

            WelcomeScreenEvent.OnGetStarted,
            WelcomeScreenEvent.OnSkip -> {
                _state.update { it.copy(isLoading = true) }
                launch {
                    firstLaunchManager.setFirstLaunchCompleted()
                    onComplete()
                }
            }
        }
    }
}

