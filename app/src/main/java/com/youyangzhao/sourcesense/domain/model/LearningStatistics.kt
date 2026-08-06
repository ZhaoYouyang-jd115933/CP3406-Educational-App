package com.youyangzhao.sourcesense.domain.model

enum class SkillProgressStatus(
    val displayName: String
) {
    STRONG(
        displayName = "Strong"
    ),
    DEVELOPING(
        displayName = "Developing"
    ),
    NEEDS_PRACTICE(
        displayName = "Needs Practice"
    ),
    NOT_ENOUGH_DATA(
        displayName = "Not Enough Data"
    )
}

data class RecentAttempt(
    val id: Long,
    val evidenceCaseId: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val completedAt: Long,
    val moduleId: String? = null,
    val moduleTitle: String? = null,
    val difficultyLevel: DifficultyLevel? = null
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
                    correctAnswers * 100L /
                            totalAnswers
                    ).toInt()
        }

    val status: SkillProgressStatus
        get() {
            return when {
                totalAnswers < 3L -> {
                    SkillProgressStatus.NOT_ENOUGH_DATA
                }

                percentage >= 80 -> {
                    SkillProgressStatus.STRONG
                }

                percentage >= 60 -> {
                    SkillProgressStatus.DEVELOPING
                }

                else -> {
                    SkillProgressStatus.NEEDS_PRACTICE
                }
            }
        }
}

data class DifficultyProgress(
    val difficultyLevel: DifficultyLevel,
    val completedModules: Int,
    val totalModules: Int
) {
    val percentage: Int
        get() {
            if (totalModules == 0) {
                return 0
            }

            return completedModules * 100 /
                    totalModules
        }
}

data class RecommendedFocus(
    val dimension: EvaluationDimension,
    val percentage: Int,
    val totalAnswers: Long,
    val moduleId: String,
    val moduleTitle: String,
    val reason: String
)

data class SourceReviewStatistics(
    val totalReviews: Int = 0,
    val reviewDepthCounts:
    Map<SourceReviewDepth, Int> = emptyMap(),
    val citationDecisionCounts:
    Map<SourceCitationDecision, Int> = emptyMap(),
    val verificationItemCounts:
    Map<SourceVerificationItem, Int> = emptyMap()
)

enum class LearningActivityType {
    EVALUATION,
    SOURCE_REVIEW
}

data class RecentLearningActivity(
    val id: String,
    val activityType: LearningActivityType,
    val title: String,
    val subtitle: String,
    val outcome: String,
    val completedAt: Long
)

data class LearningStatistics(
    val completedEvaluations: Int = 0,
    val averagePercentage: Int = 0,
    val bestPercentage: Int = 0,
    val recentAttempts: List<RecentAttempt> = emptyList(),
    val skillAccuracies: List<SkillAccuracy> = emptyList(),
    val progressByDifficulty:
    List<DifficultyProgress> = emptyList(),
    val recommendedFocus: RecommendedFocus? = null,
    val sourceReviewStatistics:
    SourceReviewStatistics = SourceReviewStatistics(),
    val recentActivities:
    List<RecentLearningActivity> = emptyList()
) {
    val totalModulesCompleted: Int
        get() = progressByDifficulty.sumOf { progress ->
            progress.completedModules
        }

    val totalModules: Int
        get() = progressByDifficulty.sumOf { progress ->
            progress.totalModules
        }

    val sourceReviewCount: Int
        get() = sourceReviewStatistics.totalReviews

    val hasEvaluationData: Boolean
        get() = completedEvaluations > 0

    val hasSourceReviewData: Boolean
        get() = sourceReviewCount > 0

    val hasData: Boolean
        get() = hasEvaluationData ||
                hasSourceReviewData
}

