package com.youyangzhao.sourcesense.data.mapper

import com.youyangzhao.sourcesense.data.local.entity.EvaluationAttemptEntity
import com.youyangzhao.sourcesense.domain.model.EvaluationAttemptSummary

// Keep Room entities out of the domain and UI layers
fun EvaluationAttemptEntity.toEvaluationAttemptSummary():
        EvaluationAttemptSummary {
    return EvaluationAttemptSummary(
        attemptId = id,
        evidenceCaseId = evidenceCaseId,
        score = score,
        totalQuestions = totalQuestions,
        percentage = percentage,
        completedAt = completedAt
    )
}

