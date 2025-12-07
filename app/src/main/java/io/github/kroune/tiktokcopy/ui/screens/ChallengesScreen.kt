package io.github.kroune.tiktokcopy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.kroune.tiktokcopy.domain.entities.Challenge
import io.github.kroune.tiktokcopy.domain.entities.ChallengesScreenEvent
import io.github.kroune.tiktokcopy.domain.entities.ChallengesScreenState
import io.github.kroune.tiktokcopy.ui.theme.SoftPastelColors

@Composable
fun ChallengesScreen(
    state: ChallengesScreenState,
    onEvent: (ChallengesScreenEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftPastelColors.SoftBackground)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Заголовок
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = SoftPastelColors.SoftShadowBlue,
                            spotColor = SoftPastelColors.PrimaryGradientEnd.copy(alpha = 0.35f)
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    SoftPastelColors.PrimaryGradientStart,
                                    SoftPastelColors.PrimaryGradientEnd
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Челленджи",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = SoftPastelColors.TextWhite
                            )
                            val activeCount = state.challenges.count { !it.isCompleted }
                            Text(
                                text = if (activeCount > 0) "Активных: $activeCount" else "Все выполнены!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftPastelColors.TextWhite.copy(alpha = 0.9f)
                            )
                        }

                        if (state.isGeneratingChallenges) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = SoftPastelColors.TextWhite
                            )
                        } else {
                            IconButton(
                                onClick = { onEvent(ChallengesScreenEvent.GenerateNewChallenges) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Сгенерировать новые",
                                    tint = SoftPastelColors.TextWhite,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Список челленджей
            if (state.challenges.isEmpty() && !state.isGeneratingChallenges) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SoftPastelColors.CardBackground
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Нет челленджей",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = SoftPastelColors.TextPrimary
                            )
                            Text(
                                text = "Нажмите на кнопку обновления, чтобы сгенерировать новые челленджи",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftPastelColors.TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(
                    items = state.challenges,
                    key = { it.id }
                ) { challenge ->
                    ChallengeItem(
                        challenge = challenge,
                        onComplete = { onEvent(ChallengesScreenEvent.CompleteChallenge(challenge.id)) }
                    )
                }
            }

            // Отступ снизу
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Индикатор загрузки
        if (state.isGeneratingChallenges) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoftPastelColors.TextPrimary.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SoftPastelColors.CardBackground
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = SoftPastelColors.PrimaryGradientStart
                        )
                        Text(
                            text = "Генерация челленджей...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = SoftPastelColors.TextPrimary
                        )
                    }
                }
            }
        }

        // Ошибка
        state.error?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Snackbar(
                    modifier = Modifier.fillMaxWidth(),
                    action = {
                        TextButton(onClick = { onEvent(ChallengesScreenEvent.DismissError) }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
fun ChallengeItem(
    challenge: Challenge,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (challenge.isCompleted) 4.dp else 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = SoftPastelColors.SoftShadowBlue
            )
            .alpha(if (challenge.isCompleted) 0.6f else 1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (challenge.isCompleted)
                SoftPastelColors.SuccessBackground
            else
                SoftPastelColors.CardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = challenge.text,
                style = MaterialTheme.typography.bodyLarge,
                color = SoftPastelColors.TextPrimary,
                textDecoration = if (challenge.isCompleted)
                    TextDecoration.LineThrough
                else
                    null,
                modifier = Modifier.weight(1f)
            )

            if (challenge.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Выполнено",
                    tint = SoftPastelColors.Success,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                IconButton(
                    onClick = onComplete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Отметить выполненным",
                        tint = SoftPastelColors.PrimaryGradientStart
                    )
                }
            }
        }
    }
}


