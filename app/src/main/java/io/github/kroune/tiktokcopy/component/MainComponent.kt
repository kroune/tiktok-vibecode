package io.github.kroune.tiktokcopy.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import io.github.kroune.tiktokcopy.data.repository.ChallengeRepository
import io.github.kroune.tiktokcopy.data.repository.ExpenseRepository
import io.github.kroune.tiktokcopy.domain.entities.Expense
import io.github.kroune.tiktokcopy.domain.entities.ExpenseAnalysis
import kotlinx.serialization.Serializable

class MainComponent(
    componentContext: ComponentContext,
    private val repository: ExpenseRepository,
    private val challengeRepository: ChallengeRepository,
    private val onNavigateToChat: (analysis: ExpenseAnalysis?, expenses: List<Expense>) -> Unit = { _, _ -> }
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Expenses,
        handleBackButton = false,
        childFactory = ::child,
    )

    fun navigateToExpenses() {
        navigation.bringToFront(Config.Expenses)
    }

    fun navigateToChallenges() {
        navigation.bringToFront(Config.Challenges)
    }

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Expenses -> Child.Expenses(
                component = ExpenseScreenComponent(
                    componentContext = componentContext,
                    repository = repository,
                    onNavigateToChat = onNavigateToChat
                )
            )
            is Config.Challenges -> Child.Challenges(
                component = ChallengesComponent(
                    componentContext = componentContext,
                    repository = challengeRepository
                )
            )
        }

    sealed class Child {
        class Expenses(val component: ExpenseScreenComponent) : Child()
        class Challenges(val component: ChallengesComponent) : Child()
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object Expenses : Config

        @Serializable
        data object Challenges : Config
    }
}

