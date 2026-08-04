package com.youyangzhao.sourcesense.domain.repository

import com.youyangzhao.sourcesense.domain.model.LearningStatistics
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {

    fun observeLearningStatistics(): Flow<LearningStatistics>

    suspend fun clearLearningHistory()
}

