package io.github.kroune.tiktokcopy.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.tiktokcopy.domain.entities.WelcomeScreenEvent
import io.github.kroune.tiktokcopy.domain.entities.WelcomeScreenState

data class WelcomePage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val backgroundColor: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(
    state: WelcomeScreenState,
    onEvent: (WelcomeScreenEvent) -> Unit
) {
    val pages = listOf(
        WelcomePage(
            title = "Добро пожаловать!",
            description = "Управляйте своими расходами легко и эффективно. Контролируйте финансы в одном месте.",
            icon = Icons.Default.AccountBalance,
            backgroundColor = Color(0xFF6C63FF)
        ),
        WelcomePage(
            title = "Умная аналитика",
            description = "Получайте детальные отчеты о ваших расходах. AI-ассистент поможет оптимизировать траты.",
            icon = Icons.Default.Analytics,
            backgroundColor = Color(0xFF4CAF50)
        ),
        WelcomePage(
            title = "AI-чат помощник",
            description = "Задавайте вопросы о финансах и получайте персонализированные советы от ИИ.",
            icon = Icons.Default.ChatBubble,
            backgroundColor = Color(0xFFFF6B6B)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        onEvent(WelcomeScreenEvent.OnPageChange(pagerState.currentPage))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(
                        onClick = { onEvent(WelcomeScreenEvent.OnSkip) }
                    ) {
                        Text("Пропустить", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                WelcomePageContent(pages[page])
            }

            // Page indicator
            Row(
                modifier = Modifier
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    }

                    val width by animateDpAsState(
                        targetValue = if (pagerState.currentPage == iteration) 24.dp else 8.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "indicator_width"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Bottom button
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        // Переход к следующей странице с анимацией
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onEvent(WelcomeScreenEvent.OnGetStarted)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = !state.isLoading
            ) {
                Text(
                    text = if (pagerState.currentPage < pages.size - 1) "Далее" else "Начать",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun WelcomePageContent(page: WelcomePage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(page.backgroundColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = page.backgroundColor
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

