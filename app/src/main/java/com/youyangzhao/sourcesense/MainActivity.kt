package com.youyangzhao.sourcesense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.di.AppContainer
import com.youyangzhao.sourcesense.domain.model.UserSettings
import com.youyangzhao.sourcesense.navigation.SourceSenseNavHost
import com.youyangzhao.sourcesense.ui.theme.SourceSenseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Create one container for app dependencies
            val appContainer = remember {
                AppContainer(
                    context = applicationContext
                )
            }

            val userSettingsFlow = remember(
                appContainer.userSettingsRepository
            ) {
                appContainer
                    .userSettingsRepository
                    .observeUserSettings()
            }

            val userSettings by userSettingsFlow
                .collectAsStateWithLifecycle(
                    initialValue = UserSettings()
                )

            SourceSenseTheme(
                useLargerText =
                    userSettings.useLargerText
            ) {
                SourceSenseNavHost(
                    appContainer = appContainer,
                    reduceAnimations =
                        userSettings.reduceAnimations,
                    soundFeedbackEnabled =
                        userSettings.soundFeedbackEnabled
                )
            }
        }
    }
}