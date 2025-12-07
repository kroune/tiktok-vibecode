package io.github.kroune.tiktokcopy.base

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

abstract class BaseComponent<State, Event>(
    initialState: State,
    componentContext: ComponentContext
) : ComponentContext by componentContext, CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate

    protected val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state

    abstract fun onEvent(event: Event)

    open fun onBackPressed() {}
}

