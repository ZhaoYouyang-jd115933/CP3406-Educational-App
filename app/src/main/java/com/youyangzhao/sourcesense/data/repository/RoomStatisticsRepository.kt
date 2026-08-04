package com.youyangzhao.sourcesense.data.repository

import com.youyangzhao.sourcesense.data.local.dao.EvaluationAttemptDao
import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.LearningStatistics
import com.youyangzhao.sourcesense.domain.model.RecentAttempt
import com.youyangzhao.sourcesense.domain.model.SkillAccuracy
import com.youyangzhao.sourcesense.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.math.roundToInt

class RoomStatisticsRepository(
    private val evaluationAttemptDao: EvaluationAttemptDao
) : StatisticsRepository {

    override fun observeLearningStatistics(): Flow<LearningStatistics> {
        return combine(
            evaluationAttemptDao.observeAttempts(),
            evaluationAttemptDao.observeAttemptCount(),
            evaluationAttemptDao.observeAveragePercentage(),
            evaluationAttemptDao.observeBestPercentage(),
            evaluationAttemptDao.observeDimensionAccuracy()
        ) {
                attempts,
                attemptCount,
                averagePercentage,
                bestPercentage,
                dimensionAccuracy ->

            val recentAttempts = attempts
                .take(5)
                .map { attempt ->
                    RecentAttempt(
                        id = attempt.id,
                        evidenceCaseId = attempt.evidenceCaseId,
                        score = attempt.score,
                        totalQuestions = attempt.totalQuestions,
                        percentage = attempt.percentage,
                        completedAt = attempt.completedAt
                    )
                }

            val skillAccuracies = dimensionAccuracy
                .mapNotNull { accuracy ->
                    // Ignore invalid dimension values safely
                    val dimension = runCatching {
                        EvaluationDimension.valueOf(
                            accuracy.dimension
                        )
                    }.getOrNull() ?: return@mapNotNull null

                    SkillAccuracy(
                        dimension = dimension,
                        correctAnswers = accuracy.correctAnswers,
                        totalAnswers = accuracy.totalAnswers
                    )
                }
                .sortedBy { skillAccuracy ->
                    skillAccuracy.dimension.ordinal
                }

            LearningStatistics(
                completedEvaluations = attemptCount,
                averagePercentage =
                    averagePercentage?.roundToInt() ?: 0,
                bestPercentage = bestPercentage ?: 0,
                recentAttempts = recentAttempts,
                skillAccuracies = skillAccuracies
            )
        }
    }

    override suspend fun clearLearningHistory() {
        // Delete attempts and their linked answers
        evaluationAttemptDao.clearAllAttempts()
    }
}

