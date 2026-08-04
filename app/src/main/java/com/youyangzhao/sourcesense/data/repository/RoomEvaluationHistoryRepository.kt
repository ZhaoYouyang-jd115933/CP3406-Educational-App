package com.youyangzhao.sourcesense.data.repository

import com.youyangzhao.sourcesense.data.local.dao.EvaluationAttemptDao
import com.youyangzhao.sourcesense.data.mapper.toAnswerEntities
import com.youyangzhao.sourcesense.data.mapper.toAttemptEntity
import com.youyangzhao.sourcesense.domain.model.EvaluationResult
import com.youyangzhao.sourcesense.domain.repository.EvaluationHistoryRepository

class RoomEvaluationHistoryRepository(
    private val evaluationAttemptDao: EvaluationAttemptDao,
    private val currentTimeProvider: () -> Long = {
        System.currentTimeMillis()
    }
) : EvaluationHistoryRepository {

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

