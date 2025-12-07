package io.github.kroune.tiktokcopy.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import io.github.kroune.tiktokcopy.data.preferences.FirstLaunchManager
import io.github.kroune.tiktokcopy.data.repository.ExpenseRepository
import io.github.kroune.tiktokcopy.domain.entities.Expense
import io.github.kroune.tiktokcopy.domain.entities.ExpenseAnalysis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext,
    private val repository: ExpenseRepository,
    private val firstLaunchManager: FirstLaunchManager
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val stack: Value<ChildStack<*, Child>>

    init {
        // Проверяем первый запуск асинхронно и определяем начальную конфигурацию
        var initialConfig: Config = Config.WelcomeScreen

        scope.launch {
            val isFirstLaunch = firstLaunchManager.isFirstLaunchValue()
            if (!isFirstLaunch) {
                // Если это не первый запуск, заменяем welcome screen на expense screen
                @OptIn(DelicateDecomposeApi::class)
                navigation.replaceAll(Config.ExpenseScreen)
            }
        }

        stack = childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = initialConfig,
            handleBackButton = true,
            childFactory = ::child,
        )
    }

    @OptIn(DelicateDecomposeApi::class)
    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.WelcomeScreen -> Child.WelcomeScreen(
                component = WelcomeScreenComponent(
                    componentContext = componentContext,
                    firstLaunchManager = firstLaunchManager,
                    onComplete = {
                        navigation.replaceAll(Config.ExpenseScreen)
                    }
                )
            )
            is Config.ExpenseScreen -> Child.ExpenseScreen(
                component = ExpenseScreenComponent(
                    componentContext = componentContext,
                    repository = repository,
                    onNavigateToChat = { analysis, expenses ->
                        navigation.push(Config.ChatScreen(analysis, expenses))
                    }
                )
            )
            is Config.ChatScreen -> Child.ChatScreen(
                component = ChatScreenComponent(
                    componentContext = componentContext,
                    repository = repository,
                    initialAnalysis = config.initialAnalysis,
                    expenses = config.expenses,
                    onNavigateBack = {
                        navigation.pop()
                    }
                )
            )
        }

    sealed class Child {
        class WelcomeScreen(val component: WelcomeScreenComponent) : Child()
        class ExpenseScreen(val component: ExpenseScreenComponent) : Child()
        class ChatScreen(val component: ChatScreenComponent) : Child()
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object WelcomeScreen : Config

        @Serializable
        data object ExpenseScreen : Config

        @Serializable
        data class ChatScreen(
            val initialAnalysis: ExpenseAnalysis?,
            val expenses: List<Expense>
        ) : Config
    }
}

