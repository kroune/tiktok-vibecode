package io.github.kroune.tiktokcopy.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import io.github.kroune.tiktokcopy.component.RootComponent
import io.github.kroune.tiktokcopy.ui.screens.ChatScreen
import io.github.kroune.tiktokcopy.ui.screens.ExpenseScreen
import io.github.kroune.tiktokcopy.ui.screens.WelcomeScreen

@Composable
fun RootContent(component: RootComponent) {
    Children(
        stack = component.stack,
        animation = stackAnimation(fade() + scale())
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.WelcomeScreen -> {
                val state by instance.component.state.collectAsState()
                WelcomeScreen(
                    state = state,
                    onEvent = instance.component::onEvent
                )
            }
            is RootComponent.Child.ExpenseScreen -> {
                val state by instance.component.state.collectAsState()
                ExpenseScreen(
                    state = state,
                    onEvent = instance.component::onEvent
                )
            }
            is RootComponent.Child.ChatScreen -> {
                val state by instance.component.state.collectAsState()
                ChatScreen(
                    state = state,
                    onEvent = instance.component::onEvent
                )
            }
        }
    }
}

