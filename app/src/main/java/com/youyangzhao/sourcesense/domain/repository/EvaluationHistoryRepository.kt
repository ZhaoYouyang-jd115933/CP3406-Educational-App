package com.youyangzhao.sourcesense.domain.repository

import com.youyangzhao.sourcesense.domain.model.EvaluationAttemptSummary
import com.youyangzhao.sourcesense.domain.model.EvaluationResult
import kotlinx.coroutines.flow.Flow

interface EvaluationHistoryRepository {

    fun observeEvaluationAttempts():
            Flow<List<EvaluationAttemptSummary>>

    suspend fun saveEvaluationResult(
        result: EvaluationResult
    ): Long
}

