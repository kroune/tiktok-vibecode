package io.github.kroune.tiktokcopy.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.kroune.tiktokcopy.domain.entities.DateFilter
import io.github.kroune.tiktokcopy.domain.entities.ExpenseScreenEvent
import io.github.kroune.tiktokcopy.domain.entities.ExpenseScreenState
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Анализ расходов",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Фильтры по периоду
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Фильтр для анализа:",
                        style = MaterialTheme.typography.titleSmall
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
                                            DateFilter.CUSTOM -> "Свой период"
                                        }
                                    )
                                }
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
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val amountError = state.amountInput.isNotEmpty() &&
                            state.amountInput.toDoubleOrNull() == null
                    val categoryError =
                        state.categoryInput.isEmpty() && state.amountInput.isNotEmpty()

                    TextField(
                        value = state.amountInput,
                        onValueChange = { newValue ->
                            // Разрешаем только цифры, точку и запятую
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*[.,]?\\d*$"))) {
                                onEvent(ExpenseScreenEvent.UpdateAmount(newValue.replace(',', '.')))
                            }
                        },
                        label = { Text("Сумма") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = amountError,
                        supportingText = if (amountError) {
                            { Text("Введите корректную сумму") }
                        } else null
                    )

                    TextField(
                        value = state.categoryInput,
                        onValueChange = { onEvent(ExpenseScreenEvent.UpdateCategory(it)) },
                        label = { Text("Категория") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = categoryError,
                        supportingText = if (categoryError) {
                            { Text("Категория обязательна") }
                        } else null
                    )

                    TextField(
                        value = state.descriptionInput,
                        onValueChange = { onEvent(ExpenseScreenEvent.UpdateDescription(it)) },
                        label = { Text("Описание (опционально)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    // Выбор даты
                    OutlinedButton(
                        onClick = { onEvent(ExpenseScreenEvent.ToggleDatePicker) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Выбрать дату",
                            modifier = Modifier.size(20.dp)
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val canAdd = state.amountInput.isNotEmpty() &&
                                state.amountInput.toDoubleOrNull() != null &&
                                state.amountInput.toDoubleOrNull()!! > 0 &&
                                state.categoryInput.isNotEmpty()

                        Button(
                            onClick = { onEvent(ExpenseScreenEvent.AddExpense) },
                            modifier = Modifier.weight(1f),
                            enabled = canAdd
                        ) {
                            Text("Добавить")
                        }

                        OutlinedButton(
                            onClick = { onEvent(ExpenseScreenEvent.ClearForm) },
                            modifier = Modifier.weight(1f)
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
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Кнопка анализа
        item {
            Button(
                onClick = { onEvent(ExpenseScreenEvent.AnalyzeExpenses) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.filteredExpenses.isNotEmpty() && !state.isAnalyzing
            ) {
                if (state.isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (state.isAnalyzing) "Анализируем..." else "Анализировать расходы")
            }
        }

        // Отображение ошибок
        state.error?.let { errorMessage ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "❌ ",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        onClick = {
                            onEvent(ExpenseScreenEvent.OpenChatWithAnalysis)
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Результат анализа:",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "💬 Открыть чат",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = result,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 5,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Нажмите, чтобы обсудить детали с AI ассистентом",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
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

@Composable
fun ExpenseItem(
    expense: io.github.kroune.tiktokcopy.domain.entities.Expense,
    onDelete: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = expense.category,
                    style = MaterialTheme.typography.titleSmall
                )
                if (expense.description.isNotEmpty()) {
                    Text(
                        text = expense.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = expense.date.format(dateFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "₽${String.format(Locale.getDefault(), "%.2f", expense.amount)}",
                    style = MaterialTheme.typography.titleSmall
                )

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
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

