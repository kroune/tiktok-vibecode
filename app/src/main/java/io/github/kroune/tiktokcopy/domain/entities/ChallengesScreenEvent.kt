package io.github.kroune.tiktokcopy.domain.entities

sealed interface ChallengesScreenEvent {
    data class CompleteChallenge(val challengeId: String) : ChallengesScreenEvent
    data object GenerateNewChallenges : ChallengesScreenEvent
    data object DismissError : ChallengesScreenEvent
}

