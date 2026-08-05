package com.youyangzhao.sourcesense.domain.model

data class EvaluationAttemptSummary(
    val attemptId: Long,
    val evidenceCaseId: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val completedAt: Long
)

