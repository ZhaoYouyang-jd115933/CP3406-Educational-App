package com.youyangzhao.sourcesense.domain.repository

import com.youyangzhao.sourcesense.domain.model.LearningStatistics
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {

    fun observeLearningStatistics():
            Flow<LearningStatistics>

    // Keep this method for existing tests and evaluation-history actions
    suspend fun clearLearningHistory()

    // Clear only structured reviews of real academic sources
    suspend fun clearSourceReviews() {
        // Repositories without source-review storage can keep the default behaviour
    }

    // Clear both evaluation attempts and real-source reviews
    suspend fun clearAllLearningData() {
        clearLearningHistory()
        clearSourceReviews()
    }
}