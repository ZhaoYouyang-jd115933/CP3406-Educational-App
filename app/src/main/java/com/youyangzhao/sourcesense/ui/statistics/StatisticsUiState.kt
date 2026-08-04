package com.youyangzhao.sourcesense.ui.statistics

import com.youyangzhao.sourcesense.domain.model.LearningStatistics

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val statistics: LearningStatistics = LearningStatistics(),
    val errorMessage: String? = null,
    val showClearConfirmation: Boolean = false,
    val isClearingHistory: Boolean = false
) {
    val hasData: Boolean
        get() = statistics.hasData

    val canClearHistory: Boolean
        get() = hasData &&
                !isLoading &&
                !isClearingHistory
}

