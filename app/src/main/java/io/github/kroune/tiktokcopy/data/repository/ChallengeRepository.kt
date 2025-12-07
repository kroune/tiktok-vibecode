package io.github.kroune.tiktokcopy.data.repository

import io.github.kroune.tiktokcopy.data.api.ChallengeApiService
import io.github.kroune.tiktokcopy.data.local.ChallengeDao
import io.github.kroune.tiktokcopy.data.local.ChallengeEntity
import io.github.kroune.tiktokcopy.domain.entities.Challenge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class ChallengeRepository(
    private val challengeDao: ChallengeDao,
    private val apiService: ChallengeApiService = ChallengeApiService()
) {
    fun getAllChallenges(): Flow<List<Challenge>> {
        return challengeDao.getAllChallenges().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getChallengeById(id: String): Challenge? {
        return challengeDao.getChallengeById(id)?.toDomain()
    }

    suspend fun insertChallenge(challenge: Challenge): Result<Unit> {
        return try {
            challengeDao.insertChallenge(ChallengeEntity.fromDomain(challenge))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertChallenges(challenges: List<Challenge>): Result<Unit> {
        return try {
            val entities = challenges.map { ChallengeEntity.fromDomain(it) }
            challengeDao.insertChallenges(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeChallenge(challengeId: String): Result<Unit> {
        return try {
            val challenge = challengeDao.getChallengeById(challengeId)
            if (challenge != null) {
                val updatedChallenge = challenge.copy(
                    isCompleted = true,
                    completedAt = LocalDateTime.now()
                )
                challengeDao.updateChallenge(updatedChallenge)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Challenge not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveCount(): Int {
        return challengeDao.getActiveCount()
    }

    suspend fun generateNewChallenges(): Result<List<Challenge>> {
        return apiService.generateChallenges().fold(
            onSuccess = { challengeTexts ->
                val challenges = challengeTexts.map { text ->
                    Challenge(text = text)
                }
                Result.success(challenges)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }

    suspend fun deleteAllChallenges(): Result<Unit> {
        return try {
            challengeDao.deleteAllChallenges()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

