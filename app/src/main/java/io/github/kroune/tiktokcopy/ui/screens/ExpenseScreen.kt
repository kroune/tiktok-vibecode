package io.github.kroune.tiktokcopy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kroune.tiktokcopy.domain.entities.ExpenseScreenEvent
import io.github.kroune.tiktokcopy.domain.entities.ExpenseScreenState

@Composable
fun ExpenseScreen(
    state: ExpenseScreenState,
    onEvent: (ExpenseScreenEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Анализ расходов",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Форма добавления расхода
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = state.amountInput,
                    onValueChange = { onEvent(ExpenseScreenEvent.UpdateAmount(it)) },
                    label = { Text("Сумма") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                TextField(
                    value = state.categoryInput,
                    onValueChange = { onEvent(ExpenseScreenEvent.UpdateCategory(it)) },
                    label = { Text("Категория") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                TextField(
                    value = state.descriptionInput,
                    onValueChange = { onEvent(ExpenseScreenEvent.UpdateDescription(it)) },
                    label = { Text("Описание (опционально)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onEvent(ExpenseScreenEvent.AddExpense) },
                        modifier = Modifier.weight(1f)
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

        // Список расходов
        Text(
            text = "Расходы (${state.expenses.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.expenses) { expense ->
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
                    text = expense.date.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "₽${String.format("%.2f", expense.amount)}",
                    style = MaterialTheme.typography.titleSmall
                )

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                }
            }
        }
    }
}

