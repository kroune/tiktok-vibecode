package io.github.kroune.tiktokcopy.domain.entities

import androidx.compose.runtime.Immutable

@Immutable
data class ChallengesScreenState(
    val error: String? = null,
    val isGeneratingChallenges: Boolean = false,
    val isLoading: Boolean = false,
    val challenges: List<Challenge> = emptyList(),
)


