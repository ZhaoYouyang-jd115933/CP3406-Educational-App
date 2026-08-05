package com.youyangzhao.sourcesense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.data.local.preferences.userSettingsDataStore
import com.youyangzhao.sourcesense.data.repository.DataStoreUserSettingsRepository
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.UserSettings
import com.youyangzhao.sourcesense.navigation.SourceSenseNavHost
import com.youyangzhao.sourcesense.ui.theme.SourceSenseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userSettingsRepository = remember {
                DataStoreUserSettingsRepository(
                    dataStore = applicationContext.userSettingsDataStore
                )
            }

            val userSettingsFlow = remember(
                userSettingsRepository
            ) {
                userSettingsRepository.observeUserSettings()
            }

            val userSettings by userSettingsFlow
                .collectAsStateWithLifecycle(
                    initialValue = UserSettings(
                        difficultyLevel = DifficultyLevel.INTERMEDIATE,
                        useLargerText = false,
                        reduceAnimations = false,
                        soundFeedbackEnabled = true
                    )
                )

            SourceSenseTheme(
                useLargerText = userSettings.useLargerText
            ) {
                SourceSenseNavHost(
                    reduceAnimations = userSettings.reduceAnimations
                )
            }
        }
    }
}