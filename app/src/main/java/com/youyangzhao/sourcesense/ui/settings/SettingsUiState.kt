package com.youyangzhao.sourcesense.ui.settings

import com.youyangzhao.sourcesense.domain.model.LearningStatistics
import com.youyangzhao.sourcesense.domain.model.UserSettings

enum class SettingsDataClearTarget(
    val dialogTitle: String,
    val dialogMessage: String,
    val confirmLabel: String
) {
    EVALUATION_HISTORY(
        dialogTitle = "Clear Evaluation History?",
        dialogMessage =
            "This permanently deletes evaluation attempts, scores, module progress and skill accuracy. Saved source reviews remain.",
        confirmLabel = "Clear Evaluations"
    ),
    SOURCE_REVIEWS(
        dialogTitle = "Clear Source Reviews?",
        dialogMessage =
            "This permanently deletes every structured review of real academic sources. Evaluation history remains.",
        confirmLabel = "Clear Reviews"
    ),
    ALL_LEARNING_DATA(
        dialogTitle = "Clear All Learning Data?",
        dialogMessage =
            "This permanently deletes evaluation history, module progress and all saved source reviews from this device.",
        confirmLabel = "Clear All Data"
    )
}

data class SettingsUiState(
    val isLoading: Boolean = true,
    val userSettings: UserSettings = UserSettings(),
    val learningStatistics:
    LearningStatistics = LearningStatistics(),
    val errorMessage: String? = null,
    val showResetConfirmation: Boolean = false,
    val dataClearTarget:
    SettingsDataClearTarget? = null,
    val isResettingSettings: Boolean = false,
    val isClearingLearningData: Boolean = false
) {
    val canChangeSettings: Boolean
        get() = !isLoading &&
                !isResettingSettings &&
                !isClearingLearningData

    val canResetSettings: Boolean
        get() = canChangeSettings &&
                userSettings != UserSettings()

    val canClearEvaluationHistory: Boolean
        get() = learningStatistics.hasEvaluationData &&
                !isLoading &&
                !isClearingLearningData

    val canClearSourceReviews: Boolean
        get() = learningStatistics.hasSourceReviewData &&
                !isLoading &&
                !isClearingLearningData

    val canClearAllLearningData: Boolean
        get() = learningStatistics.hasData &&
                !isLoading &&
                !isClearingLearningData

    val showDataClearConfirmation: Boolean
        get() = dataClearTarget != null
}
