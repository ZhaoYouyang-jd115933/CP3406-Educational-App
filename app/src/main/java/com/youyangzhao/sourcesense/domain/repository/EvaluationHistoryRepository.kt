package com.youyangzhao.sourcesense.domain.repository

import com.youyangzhao.sourcesense.domain.model.EvaluationResult

interface EvaluationHistoryRepository {

    suspend fun saveEvaluationResult(
        result: EvaluationResult
    ): Long
}

