package io.github.kroune.tiktokcopy.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.github.kroune.tiktokcopy.component.MainComponent
import io.github.kroune.tiktokcopy.ui.screens.ChallengesScreen
import io.github.kroune.tiktokcopy.ui.screens.ExpenseScreen

@Composable
fun MainContent(component: MainComponent) {
    val childStack by component.stack.subscribeAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Финансы") },
                    selected = childStack.active.instance is MainComponent.Child.Expenses,
                    onClick = { component.navigateToExpenses() }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("Челленджи") },
                    selected = childStack.active.instance is MainComponent.Child.Challenges,
                    onClick = { component.navigateToChallenges() }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Children(
                stack = component.stack,
                animation = stackAnimation(fade())
            ) { child ->
                when (val instance = child.instance) {
                    is MainComponent.Child.Expenses -> {
                        val state by instance.component.state.collectAsState()
                        ExpenseScreen(
                            state = state,
                            onEvent = instance.component::onEvent
                        )
                    }
                    is MainComponent.Child.Challenges -> {
                        val state by instance.component.state.collectAsState()
                        ChallengesScreen(
                            state = state,
                            onEvent = instance.component::onEvent
                        )
                    }
                }
            }
        }
    }
}

