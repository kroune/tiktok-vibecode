package io.github.kroune.tiktokcopy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.kroune.tiktokcopy.domain.entities.ChatMessage
import io.github.kroune.tiktokcopy.domain.entities.ChatScreenEvent
import io.github.kroune.tiktokcopy.domain.entities.ChatScreenState
import io.github.kroune.tiktokcopy.ui.theme.SoftPastelColors
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: ChatScreenState,
    onEvent: (ChatScreenEvent) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Автоматическая прокрутка вниз при добавлении новых сообщений
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(state.messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        ambientColor = SoftPastelColors.SoftShadowBlue,
                        spotColor = SoftPastelColors.PrimaryGradientEnd.copy(alpha = 0.3f)
                    )
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                SoftPastelColors.PrimaryGradientStart,
                                SoftPastelColors.PrimaryGradientEnd
                            )
                        )
                    )
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onEvent(ChatScreenEvent.NavigateBack) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = SoftPastelColors.SurfaceWhite
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                    Text(
                        text = "AI Ассистент",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftPastelColors.SurfaceWhite
                        )
                    )
                }
            }
        },
        containerColor = SoftPastelColors.SoftBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Список сообщений
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.messages) { message ->
                    ChatMessageItem(message = message)
                }

                // Индикатор загрузки
                if (state.isLoading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = SoftPastelColors.SurfaceWhite
                                ),
                                modifier = Modifier.shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    ambientColor = SoftPastelColors.SoftShadowDark,
                                    spotColor = SoftPastelColors.SoftShadowGray
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = SoftPastelColors.PrimaryGradientEnd
                                    )
                                    Text(
                                        "AI думает...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = SoftPastelColors.TextMuted
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Отображение ошибок
            state.error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = SoftPastelColors.SecondaryAccent.copy(alpha = 0.25f),
                            spotColor = SoftPastelColors.SecondaryAccent.copy(alpha = 0.2f)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = SoftPastelColors.SecondaryAccent.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = SoftPastelColors.SecondaryAccent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SoftPastelColors.SurfaceWhite
                                )
                            )
                        }
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SoftPastelColors.TextDark
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Поле ввода сообщения
            Surface(
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                color = SoftPastelColors.SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        ambientColor = SoftPastelColors.SoftShadowDark,
                        spotColor = SoftPastelColors.SoftShadowGray
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextField(
                        value = state.inputText,
                        onValueChange = { onEvent(ChatScreenEvent.UpdateInputText(it)) },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Введите сообщение...",
                                color = SoftPastelColors.TextMuted
                            )
                        },
                        maxLines = 4,
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SoftPastelColors.IceBlueBackground,
                            unfocusedContainerColor = SoftPastelColors.IceBlueBackground,
                            disabledContainerColor = SoftPastelColors.IceBlueBackground,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = SoftPastelColors.PrimaryGradientEnd,
                            focusedTextColor = SoftPastelColors.TextDark,
                            unfocusedTextColor = SoftPastelColors.TextDark
                        )
                    )

                    FilledIconButton(
                        onClick = { onEvent(ChatScreenEvent.SendMessage) },
                        enabled = state.inputText.isNotBlank() && !state.isLoading,
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(
                                elevation = if (state.inputText.isNotBlank() && !state.isLoading) 10.dp else 0.dp,
                                shape = CircleShape,
                                ambientColor = SoftPastelColors.PrimaryGradientEnd.copy(alpha = 0.4f),
                                spotColor = SoftPastelColors.SoftShadowBlue
                            ),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = SoftPastelColors.PrimaryGradientEnd,
                            contentColor = SoftPastelColors.SurfaceWhite,
                            disabledContainerColor = SoftPastelColors.TextMuted.copy(alpha = 0.3f)
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Отправить",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (message.isFromUser) 20.dp else 4.dp,
                bottomEnd = if (message.isFromUser) 4.dp else 20.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser)
                    SoftPastelColors.PrimaryGradientEnd
                else
                    SoftPastelColors.SurfaceWhite
            ),
            modifier = Modifier
                .widthIn(max = 280.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (message.isFromUser) 20.dp else 4.dp,
                        bottomEnd = if (message.isFromUser) 4.dp else 20.dp
                    ),
                    ambientColor = if (message.isFromUser)
                        SoftPastelColors.SoftShadowBlue
                    else
                        SoftPastelColors.SoftShadowDark,
                    spotColor = if (message.isFromUser)
                        SoftPastelColors.PrimaryGradientEnd.copy(alpha = 0.3f)
                    else
                        SoftPastelColors.SoftShadowGray
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (message.isFromUser)
                            SoftPastelColors.SurfaceWhite
                        else
                            SoftPastelColors.TextDark
                    )
                )

                Text(
                    text = message.timestamp.format(timeFormatter),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (message.isFromUser)
                            SoftPastelColors.SurfaceWhite.copy(alpha = 0.8f)
                        else
                            SoftPastelColors.TextMuted
                    )
                )
            }
        }
    }
}

