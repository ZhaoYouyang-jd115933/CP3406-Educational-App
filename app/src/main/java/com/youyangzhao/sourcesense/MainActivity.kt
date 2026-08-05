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
import com.youyangzhao.sourcesense.navigation.SourceSenseNavHost
import com.youyangzhao.sourcesense.ui.theme.SourceSenseTheme
import kotlinx.coroutines.flow.map

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

            val useLargerTextFlow = remember(
                userSettingsRepository
            ) {
                userSettingsRepository
                    .observeUserSettings()
                    .map { userSettings ->
                        userSettings.useLargerText
                    }
            }

            val useLargerText by useLargerTextFlow
                .collectAsStateWithLifecycle(
                    initialValue = false
                )

            SourceSenseTheme(
                useLargerText = useLargerText
            ) {
                SourceSenseNavHost()
            }
        }
    }
}