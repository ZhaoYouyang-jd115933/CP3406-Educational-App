package com.youyangzhao.sourcesense.domain.repository

import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {

    fun observeUserSettings(): Flow<UserSettings>

    suspend fun updateDifficultyLevel(
        difficultyLevel: DifficultyLevel
    )

    suspend fun updateUseLargerText(
        enabled: Boolean
    )

    suspend fun updateReduceAnimations(
        enabled: Boolean
    )

    suspend fun updateSoundFeedback(
        enabled: Boolean
    )

    suspend fun updateShowStatisticsRecommendation(
        enabled: Boolean
    ) {
        // Existing repositories can keep the default display behaviour
    }

    suspend fun updateShowStatisticsSkillAccuracy(
        enabled: Boolean
    ) {
        // Existing repositories can keep the default display behaviour
    }

    suspend fun updateShowStatisticsSourcePractice(
        enabled: Boolean
    ) {
        // Existing repositories can keep the default display behaviour
    }

    suspend fun updateShowStatisticsRecentActivity(
        enabled: Boolean
    ) {
        // Existing repositories can keep the default display behaviour
    }

    suspend fun updateShowStatisticsSectionDescriptions(
        enabled: Boolean
    ) {
        // Existing repositories can keep the default display behaviour
    }

    suspend fun resetUserSettings()
}
