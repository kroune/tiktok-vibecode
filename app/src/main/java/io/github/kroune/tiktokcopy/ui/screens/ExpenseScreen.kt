package io.github.kroune.tiktokcopy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.kroune.tiktokcopy.domain.entities.DateFilter
import io.github.kroune.tiktokcopy.domain.entities.ExpenseScreenEvent
import io.github.kroune.tiktokcopy.domain.entities.ExpenseScreenState
import io.github.kroune.tiktokcopy.ui.theme.SoftPastelColors
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ExpenseScreen(
    state: ExpenseScreenState,
    onEvent: (ExpenseScreenEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftPastelColors.SoftBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header with gradient
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
                    Text(
                        text = "Анализ расходов",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftPastelColors.SurfaceWhite
                        )
                    )
                }
            }

        // Фильтры по периоду
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = SoftPastelColors.SoftShadowDark,
                        spotColor = SoftPastelColors.SoftShadowGray
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = SoftPastelColors.SurfaceWhite
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Фильтр для анализа:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftPastelColors.TextDark
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DateFilter.entries.forEach { filter ->
                            FilterChip(
                                selected = state.dateFilter == filter,
                                onClick = { onEvent(ExpenseScreenEvent.UpdateDateFilter(filter)) },
                                label = {
                                    Text(
                                        when (filter) {
                                            DateFilter.ALL -> "Все"
                                            DateFilter.TODAY -> "Сегодня"
                                            DateFilter.WEEK -> "Неделя"
                                            DateFilter.MONTH -> "Месяц"
                                        }
                                    )
                                },
                                shape = RoundedCornerShape(50),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SoftPastelColors.PrimaryGradientEnd,
                                    selectedLabelColor = SoftPastelColors.SurfaceWhite,
                                    containerColor = SoftPastelColors.IceBlueBackground,
                                    labelColor = SoftPastelColors.TextMuted
                                )
                            )
                        }
                    }
                }
            }
        }

        // Форма добавления расхода
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = SoftPastelColors.SoftShadowDark,
                        spotColor = SoftPastelColors.SoftShadowGray
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = SoftPastelColors.SurfaceWhite
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val amountError = state.amountInput.isNotEmpty() &&
                            state.amountInput.toDoubleOrNull() == null

                    TextField(
                        value = state.amountInput,
                        onValueChange = { newValue ->
                            // Разрешаем только цифры, точку и запятую
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*[.,]?\\d*$"))) {
                                onEvent(ExpenseScreenEvent.UpdateAmount(newValue.replace(',', '.')))
                            }
                        },
                        label = { Text("Сумма", color = SoftPastelColors.TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = amountError,
                        supportingText = if (amountError) {
                            { Text("Введите корректную сумму", color = SoftPastelColors.SecondaryAccent) }
                        } else null,
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = SoftPastelColors.IceBlueBackground,
                            focusedContainerColor = SoftPastelColors.IceBlueBackground,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = SoftPastelColors.PrimaryGradientEnd,
                            cursorColor = SoftPastelColors.PrimaryGradientEnd,
                            focusedTextColor = SoftPastelColors.TextDark,
                            unfocusedTextColor = SoftPastelColors.TextDark
                        )
                    )

                    TextField(
                        value = state.descriptionInput,
                        onValueChange = { onEvent(ExpenseScreenEvent.UpdateDescription(it)) },
                        label = { Text("Описание", color = SoftPastelColors.TextMuted) },
                        placeholder = { Text("Например: Купил продукты в магазине", color = SoftPastelColors.TextMuted.copy(alpha = 0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = SoftPastelColors.IceBlueBackground,
                            focusedContainerColor = SoftPastelColors.IceBlueBackground,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = SoftPastelColors.PrimaryGradientEnd,
                            cursorColor = SoftPastelColors.PrimaryGradientEnd,
                            focusedTextColor = SoftPastelColors.TextDark,
                            unfocusedTextColor = SoftPastelColors.TextDark
                        )
                    )

                    // Выбор даты
                    OutlinedButton(
                        onClick = { onEvent(ExpenseScreenEvent.ToggleDatePicker) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = SoftPastelColors.IceBlueBackground,
                            contentColor = SoftPastelColors.TextDark
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Выбрать дату",
                            modifier = Modifier.size(20.dp),
                            tint = SoftPastelColors.PrimaryGradientEnd
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Дата: ${state.selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}"
                        )
                    }

                    // DatePicker диалог
                    if (state.showDatePicker) {
                        DatePickerDialog(
                            selectedDate = state.selectedDate,
                            onDateSelected = { date ->
                                onEvent(ExpenseScreenEvent.UpdateSelectedDate(date))
                                onEvent(ExpenseScreenEvent.ToggleDatePicker)
                            },
                            onDismiss = { onEvent(ExpenseScreenEvent.ToggleDatePicker) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val canAdd = state.amountInput.isNotEmpty() &&
                                state.amountInput.toDoubleOrNull() != null &&
                                state.amountInput.toDoubleOrNull()!! > 0 &&
                                state.descriptionInput.isNotEmpty()

                        Button(
                            onClick = { onEvent(ExpenseScreenEvent.AddExpense) },
                            modifier = Modifier.weight(1f),
                            enabled = canAdd,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftPastelColors.PrimaryGradientEnd,
                                contentColor = SoftPastelColors.SurfaceWhite,
                                disabledContainerColor = SoftPastelColors.TextMuted.copy(alpha = 0.3f)
                            )
                        ) {
                            Text("Добавить", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onEvent(ExpenseScreenEvent.ClearForm) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SoftPastelColors.TextMuted
                            )
                        ) {
                            Text("Очистить")
                        }
                    }
                }
            }
        }

        // Список расходов
        item {
            Text(
                text = "Расходы (${state.filteredExpenses.size})",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = SoftPastelColors.TextDark
                )
            )
        }

        // Круговой график по категориям
        if (state.filteredExpenses.isNotEmpty()) {
            item {
                CategoryPieChart(expenses = state.filteredExpenses)
            }
        }

        // Кнопка анализа
        item {
            Button(
                onClick = { onEvent(ExpenseScreenEvent.AnalyzeExpenses) },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (state.filteredExpenses.isNotEmpty() && !state.isAnalyzing) 8.dp else 0.dp,
                        shape = RoundedCornerShape(50),
                        ambientColor = SoftPastelColors.PrimaryGradientEnd.copy(alpha = 0.3f),
                        spotColor = SoftPastelColors.PrimaryGradientEnd.copy(alpha = 0.3f)
                    ),
                enabled = state.filteredExpenses.isNotEmpty() && !state.isAnalyzing,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftPastelColors.PrimaryGradientEnd,
                    contentColor = SoftPastelColors.SurfaceWhite,
                    disabledContainerColor = SoftPastelColors.TextMuted.copy(alpha = 0.3f)
                )
            ) {
                if (state.isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = SoftPastelColors.SurfaceWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    if (state.isAnalyzing) "Анализируем..." else "Анализировать расходы",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Отображение ошибок
        state.error?.let { errorMessage ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = SoftPastelColors.SecondaryAccent.copy(alpha = 0.25f),
                            spotColor = SoftPastelColors.SecondaryAccent.copy(alpha = 0.2f)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = SoftPastelColors.SecondaryAccent.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = SoftPastelColors.SecondaryAccent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "!",
                                style = MaterialTheme.typography.titleLarge.copy(
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
        }

        // Результат анализа (показываем только если результат непустой)
        state.analysisResult?.let { result ->
            if (result.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = SoftPastelColors.PastelMint.copy(alpha = 0.3f),
                                spotColor = SoftPastelColors.SoftShadowDark
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = SoftPastelColors.SurfaceWhite
                        ),
                        shape = RoundedCornerShape(24.dp),
                        onClick = {
                            onEvent(ExpenseScreenEvent.OpenChatWithAnalysis)
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        SoftPastelColors.PrimaryGradientStart,
                                                        SoftPastelColors.PrimaryGradientEnd
                                                    )
                                                ),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "✓",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = SoftPastelColors.SurfaceWhite
                                            )
                                        )
                                    }
                                    Text(
                                        text = "Результат анализа",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SoftPastelColors.TextDark
                                        )
                                    )
                                }
                                Text(
                                    text = "💬",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Text(
                                text = result,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SoftPastelColors.TextDark
                                ),
                                maxLines = 5,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Нажмите, чтобы обсудить детали с AI ассистентом",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SoftPastelColors.TextMuted,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            )
                        }
                    }
                }
            }
        }

        // Элементы расходов
        items(state.filteredExpenses) { expense ->
            ExpenseItem(
                expense = expense,
                onDelete = { onEvent(ExpenseScreenEvent.DeleteExpense(expense.id)) }
            )
        }
    }
    }
}

@Composable
fun ExpenseItem(
    expense: io.github.kroune.tiktokcopy.domain.entities.Expense,
    onDelete: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = SoftPastelColors.SoftShadowDark,
                spotColor = SoftPastelColors.SoftShadowGray
            ),
        colors = CardDefaults.cardColors(
            containerColor = SoftPastelColors.SurfaceWhite
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка категории с индикатором загрузки
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (expense.isGeneratingCategory)
                            SoftPastelColors.TextMuted.copy(alpha = 0.1f)
                        else
                            SoftPastelColors.IceBlueBackground,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (expense.isGeneratingCategory) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = SoftPastelColors.PrimaryGradientEnd,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = (expense.category ?: "??").take(2).uppercase(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftPastelColors.PrimaryGradientEnd
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (expense.isGeneratingCategory) {
                    Text(
                        text = "Определяем категорию...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftPastelColors.TextMuted
                        )
                    )
                } else {
                    Text(
                        text = expense.category ?: "Без категории",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftPastelColors.TextDark
                        )
                    )
                }
                if (expense.description.isNotEmpty()) {
                    Text(
                        text = expense.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SoftPastelColors.TextMuted
                        )
                    )
                }
                Text(
                    text = expense.date.format(dateFormatter),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SoftPastelColors.TextMuted
                    )
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "₽${String.format(Locale.getDefault(), "%.2f", expense.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SoftPastelColors.TextDark
                    )
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = SoftPastelColors.SecondaryAccent.copy(alpha = 0.15f),
                        contentColor = SoftPastelColors.SecondaryAccent
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Удалить",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    selectedDate: LocalDateTime,
    onDateSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    var selectedHour by remember { mutableStateOf(selectedDate.hour) }
    var selectedMinute by remember { mutableStateOf(selectedDate.minute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        val dateTime = localDate.atTime(selectedHour, selectedMinute)
                        onDateSelected(dateTime)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        text = {
            Column {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Время:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Выбор часа
                    OutlinedTextField(
                        value = selectedHour.toString().padStart(2, '0'),
                        onValueChange = { newValue ->
                            newValue.toIntOrNull()?.let { hour ->
                                if (hour in 0..23) {
                                    selectedHour = hour
                                }
                            }
                        },
                        label = { Text("Час") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Text(":")

                    // Выбор минуты
                    OutlinedTextField(
                        value = selectedMinute.toString().padStart(2, '0'),
                        onValueChange = { newValue ->
                            newValue.toIntOrNull()?.let { minute ->
                                if (minute in 0..59) {
                                    selectedMinute = minute
                                }
                            }
                        },
                        label = { Text("Минута") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }
    )
}

@Composable
fun CategoryPieChart(expenses: List<io.github.kroune.tiktokcopy.domain.entities.Expense>) {
    // Группируем расходы по категориям и считаем суммы
    val categoryTotals = expenses
        .filter { it.category != null && !it.isGeneratingCategory }
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    if (categoryTotals.isEmpty()) {
        return
    }

    val totalAmount = categoryTotals.sumOf { it.second }

    // Цвета для категорий
    val categoryColors = listOf(
        SoftPastelColors.PrimaryGradientEnd,
        SoftPastelColors.PastelMint,
        SoftPastelColors.SecondaryAccent,
        Color(0xFF9C88FF),  // Пастельный фиолетовый
        Color(0xFFFFB8D1),  // Пастельный розовый
        Color(0xFFFFC785),  // Пастельный оранжевый
        Color(0xFF85E3FF),  // Пастельный голубой
        Color(0xFFB4F8C8)   // Пастельный зеленый
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = SoftPastelColors.SoftShadowDark,
                spotColor = SoftPastelColors.SoftShadowGray
            ),
        colors = CardDefaults.cardColors(
            containerColor = SoftPastelColors.SurfaceWhite
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Распределение по категориям",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = SoftPastelColors.TextDark
                )
            )

            // Рисуем круговой график
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                PieChartVisual(
                    categoryTotals = categoryTotals,
                    totalAmount = totalAmount,
                    colors = categoryColors
                )
            }

            // Легенда
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryTotals.forEachIndexed { index, (category, amount) ->
                    val percentage = (amount / totalAmount * 100)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        color = categoryColors[index % categoryColors.size],
                                        shape = CircleShape
                                    )
                            )
                            Text(
                                text = category ?: "Без категории",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SoftPastelColors.TextDark
                                )
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "₽${String.format(Locale.getDefault(), "%.2f", amount)}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SoftPastelColors.TextDark
                                )
                            )
                            Text(
                                text = "${String.format(Locale.getDefault(), "%.1f", percentage)}%",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SoftPastelColors.TextMuted
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PieChartVisual(
    categoryTotals: List<Pair<String?, Double>>,
    totalAmount: Double,
    colors: List<Color>
) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .size(250.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = minOf(canvasWidth, canvasHeight) / 2f * 0.8f
        val center = androidx.compose.ui.geometry.Offset(canvasWidth / 2f, canvasHeight / 2f)

        var currentAngle = -90f // Начинаем сверху

        categoryTotals.forEachIndexed { index, (_, amount) ->
            val sweepAngle = (amount / totalAmount * 360f).toFloat()

            drawArc(
                color = colors[index % colors.size],
                startAngle = currentAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = androidx.compose.ui.geometry.Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
            )

            currentAngle += sweepAngle
        }

        // Белый круг в центре для эффекта "пончика"
        drawCircle(
            color = SoftPastelColors.SurfaceWhite,
            radius = radius * 0.5f,
            center = center
        )
    }

    // Текст в центре
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Всего",
            style = MaterialTheme.typography.bodySmall.copy(
                color = SoftPastelColors.TextMuted
            )
        )
        Text(
            text = "₽${String.format(Locale.getDefault(), "%.2f", totalAmount)}",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = SoftPastelColors.TextDark
            )
        )
    }
}

