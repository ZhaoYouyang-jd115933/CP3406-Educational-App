package com.youyangzhao.sourcesense.domain.model

data class RecentAttempt(
    val id: Long,
    val evidenceCaseId: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val completedAt: Long
)

data class SkillAccuracy(
    val dimension: EvaluationDimension,
    val correctAnswers: Long,
    val totalAnswers: Long
) {
    val percentage: Int
        get() {
            if (totalAnswers == 0L) {
                return 0
            }

            return (
                    correctAnswers * 100L / totalAnswers
                    ).toInt()
        }
}

data class LearningStatistics(
    val completedEvaluations: Int = 0,
    val averagePercentage: Int = 0,
    val bestPercentage: Int = 0,
    val recentAttempts: List<RecentAttempt> = emptyList(),
    val skillAccuracies: List<SkillAccuracy> = emptyList()
) {
    val hasData: Boolean
        get() = completedEvaluations > 0
}

