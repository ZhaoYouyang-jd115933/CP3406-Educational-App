package com.youyangzhao.sourcesense.ui.statistics

import com.youyangzhao.sourcesense.domain.model.LearningStatistics
import com.youyangzhao.sourcesense.domain.model.UserSettings

enum class StatisticsClearTarget(
    val dialogTitle: String,
    val dialogMessage: String,
    val confirmLabel: String
) {
    EVALUATION_HISTORY(
        dialogTitle = "Clear Evaluation History?",
        dialogMessage =
            "This will permanently delete evaluation attempts, scores and skill accuracy stored on this device.",
        confirmLabel = "Clear Evaluations"
    ),
    SOURCE_REVIEWS(
        dialogTitle = "Clear Source Reviews?",
        dialogMessage =
            "This will permanently delete every structured review of real academic sources stored on this device.",
        confirmLabel = "Clear Reviews"
    ),
    ALL_LEARNING_DATA(
        dialogTitle = "Clear All Learning Data?",
        dialogMessage =
            "This will permanently delete evaluation history, progress statistics and all saved source reviews.",
        confirmLabel = "Clear All Data"
    )
}

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val statistics:
    LearningStatistics = LearningStatistics(),
    val userSettings:
    UserSettings = UserSettings(),
    val errorMessage: String? = null,
    val clearTarget:
    StatisticsClearTarget? = null,
    val isClearingHistory: Boolean = false
) {
    val hasData: Boolean
        get() = statistics.hasData

    val showLearningRecommendation: Boolean
        get() = userSettings
            .showStatisticsRecommendation

    val showSkillAccuracy: Boolean
        get() = userSettings
            .showStatisticsSkillAccuracy

    val showRealSourcePractice: Boolean
        get() = userSettings
            .showStatisticsSourcePractice

    val showRecentActivity: Boolean
        get() = userSettings
            .showStatisticsRecentActivity

    val showSectionDescriptions: Boolean
        get() = userSettings
            .showStatisticsSectionDescriptions

    // Keep these properties for existing ViewModel tests
    val showClearConfirmation: Boolean
        get() = clearTarget != null

    val canClearHistory: Boolean
        get() = statistics.hasEvaluationData &&
                !isLoading &&
                !isClearingHistory

    val canClearSourceReviews: Boolean
        get() = statistics.hasSourceReviewData &&
                !isLoading &&
                !isClearingHistory

    val canClearAllData: Boolean
        get() = hasData &&
                !isLoading &&
                !isClearingHistory
}

