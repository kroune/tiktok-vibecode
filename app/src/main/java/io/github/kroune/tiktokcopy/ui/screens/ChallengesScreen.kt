package io.github.kroune.tiktokcopy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kroune.tiktokcopy.component.ChallengesComponent

@Composable
fun ChallengesScreen(
    state: ChallengesComponent.ChallengesState,
    onEvent: (ChallengesComponent.ChallengesEvent) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Челленджи",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Здесь будут челленджи",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

