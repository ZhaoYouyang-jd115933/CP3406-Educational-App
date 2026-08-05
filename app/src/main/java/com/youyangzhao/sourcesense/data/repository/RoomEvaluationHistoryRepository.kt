package com.youyangzhao.sourcesense.data.repository

import com.youyangzhao.sourcesense.data.local.dao.EvaluationAttemptDao
import com.youyangzhao.sourcesense.data.mapper.toAnswerEntities
import com.youyangzhao.sourcesense.data.mapper.toAttemptEntity
import com.youyangzhao.sourcesense.data.mapper.toEvaluationAttemptSummary
import com.youyangzhao.sourcesense.domain.model.EvaluationAttemptSummary
import com.youyangzhao.sourcesense.domain.model.EvaluationResult
import com.youyangzhao.sourcesense.domain.repository.EvaluationHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomEvaluationHistoryRepository(
    private val evaluationAttemptDao: EvaluationAttemptDao,
    private val currentTimeProvider: () -> Long = {
        System.currentTimeMillis()
    }
) : EvaluationHistoryRepository {

    override fun observeEvaluationAttempts():
            Flow<List<EvaluationAttemptSummary>> {
        return evaluationAttemptDao
            .observeAttempts()
            .map { attempts ->
                attempts.map { attempt ->
                    attempt.toEvaluationAttemptSummary()
                }
            }
    }

    override suspend fun saveEvaluationResult(
        result: EvaluationResult
    ): Long {
        val attempt = result.toAttemptEntity(
            completedAt = currentTimeProvider()
        )

        val answers = result.toAnswerEntities()

        // Save the attempt and its answers in one transaction
        return evaluationAttemptDao.insertAttemptWithAnswers(
            attempt = attempt,
            answers = answers
        )
    }
}

