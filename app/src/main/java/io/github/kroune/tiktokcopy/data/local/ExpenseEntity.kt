package io.github.kroune.tiktokcopy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import io.github.kroune.tiktokcopy.domain.entities.Expense
import java.time.LocalDateTime

@Entity(tableName = "expenses")
@TypeConverters(LocalDateTimeConverter::class)
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val category: String,
    val description: String,
    val date: LocalDateTime
) {
    fun toDomain(): Expense {
        return Expense(
            id = id,
            amount = amount,
            category = category,
            description = description,
            date = date
        )
    }

    companion object {
        fun fromDomain(expense: Expense): ExpenseEntity {
            return ExpenseEntity(
                id = expense.id,
                amount = expense.amount,
                category = expense.category,
                description = expense.description,
                date = expense.date
            )
        }
    }
}

