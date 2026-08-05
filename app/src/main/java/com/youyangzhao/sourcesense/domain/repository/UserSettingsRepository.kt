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

    suspend fun resetUserSettings()
}

