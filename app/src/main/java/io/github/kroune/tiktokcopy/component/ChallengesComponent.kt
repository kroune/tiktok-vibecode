package io.github.kroune.tiktokcopy.component

import com.arkivanov.decompose.ComponentContext
import io.github.kroune.tiktokcopy.base.BaseComponent
import io.github.kroune.tiktokcopy.data.repository.ChallengeRepository
import io.github.kroune.tiktokcopy.domain.entities.Challenge
import io.github.kroune.tiktokcopy.domain.entities.ChallengesScreenEvent
import io.github.kroune.tiktokcopy.domain.entities.ChallengesScreenState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChallengesComponent(
    componentContext: ComponentContext,
    private val repository: ChallengeRepository
) : BaseComponent<ChallengesScreenState, ChallengesScreenEvent>(
    ChallengesScreenState(),
    componentContext
) {

    init {
        // Загружаем челленджи из базы при инициализации
        launch {
            repository.getAllChallenges().collect { challenges ->
                _state.update { it.copy(challenges = challenges) }

                // Если нет активных челленджей, генерируем новые
                if (challenges.all { it.isCompleted }) {
                    generateNewChallenges()
                }
            }
        }
    }

    override fun onEvent(event: ChallengesScreenEvent) {
        when (event) {
            is ChallengesScreenEvent.CompleteChallenge -> {
                completeChallenge(event.challengeId)
            }
            is ChallengesScreenEvent.GenerateNewChallenges -> {
                generateNewChallenges()
            }
            is ChallengesScreenEvent.DismissError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun completeChallenge(challengeId: String) {
        launch {
            _state.update { it.copy(isLoading = true) }

            repository.completeChallenge(challengeId).fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false) }

                    // Проверяем, остались ли активные челленджи
                    launch {
                        val activeCount = repository.getActiveCount()
                        if (activeCount == 0) {
                            generateNewChallenges()
                        }
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Ошибка при завершении челленджа: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    private fun generateNewChallenges() {
        launch {
            _state.update { it.copy(isGeneratingChallenges = true, error = null) }

            repository.generateNewChallenges().fold(
                onSuccess = { newChallenges ->
                    // Сохраняем новые челленджи в базу
                    repository.insertChallenges(newChallenges).fold(
                        onSuccess = {
                            _state.update { it.copy(isGeneratingChallenges = false) }
                        },
                        onFailure = { error ->
                            _state.update {
                                it.copy(
                                    isGeneratingChallenges = false,
                                    error = "Ошибка при сохранении челленджей: ${error.message}"
                                )
                            }
                        }
                    )
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isGeneratingChallenges = false,
                            error = "Ошибка при генерации челленджей: ${error.message}"
                        )
                    }
                }
            )
        }
    }
}

