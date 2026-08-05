package com.youyangzhao.sourcesense.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.UserSettings
import com.youyangzhao.sourcesense.domain.repository.UserSettingsRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreUserSettingsRepository(
    private val dataStore: DataStore<Preferences>
) : UserSettingsRepository {

    private object PreferenceKeys {
        val difficultyLevel = stringPreferencesKey(
            "difficulty_level"
        )

        val useLargerText = booleanPreferencesKey(
            "use_larger_text"
        )

        val reduceAnimations = booleanPreferencesKey(
            "reduce_animations"
        )

        val soundFeedbackEnabled = booleanPreferencesKey(
            "sound_feedback_enabled"
        )
    }

    override fun observeUserSettings(): Flow<UserSettings> {
        return dataStore.data
            .catch { exception ->
                // Use default settings when preference data cannot be read
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val difficultyLevel = preferences[
                    PreferenceKeys.difficultyLevel
                ]?.let { storedValue ->
                    runCatching {
                        DifficultyLevel.valueOf(storedValue)
                    }.getOrNull()
                } ?: DifficultyLevel.INTERMEDIATE

                UserSettings(
                    difficultyLevel = difficultyLevel,
                    useLargerText = preferences[
                        PreferenceKeys.useLargerText
                    ] ?: false,
                    reduceAnimations = preferences[
                        PreferenceKeys.reduceAnimations
                    ] ?: false,
                    soundFeedbackEnabled = preferences[
                        PreferenceKeys.soundFeedbackEnabled
                    ] ?: true
                )
            }
    }

    override suspend fun updateDifficultyLevel(
        difficultyLevel: DifficultyLevel
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.difficultyLevel] =
                difficultyLevel.name
        }
    }

    override suspend fun updateUseLargerText(
        enabled: Boolean
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.useLargerText] = enabled
        }
    }

    override suspend fun updateReduceAnimations(
        enabled: Boolean
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.reduceAnimations] = enabled
        }
    }

    override suspend fun updateSoundFeedback(
        enabled: Boolean
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.soundFeedbackEnabled] =
                enabled
        }
    }

    override suspend fun resetUserSettings() {
        // Remove stored values so the default settings are restored
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

