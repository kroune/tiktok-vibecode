package io.github.kroune.tiktokcopy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import io.github.kroune.tiktokcopy.domain.entities.Challenge
import java.time.LocalDateTime

@Entity(tableName = "challenges")
@TypeConverters(LocalDateTimeConverter::class)
data class ChallengeEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val isCompleted: Boolean,
    val createdAt: LocalDateTime,
    val completedAt: LocalDateTime?
) {
    fun toDomain(): Challenge {
        return Challenge(
            id = id,
            text = text,
            isCompleted = isCompleted,
            createdAt = createdAt,
            completedAt = completedAt
        )
    }

    companion object {
        fun fromDomain(challenge: Challenge): ChallengeEntity {
            return ChallengeEntity(
                id = challenge.id,
                text = challenge.text,
                isCompleted = challenge.isCompleted,
                createdAt = challenge.createdAt,
                completedAt = challenge.completedAt
            )
        }
    }
}

