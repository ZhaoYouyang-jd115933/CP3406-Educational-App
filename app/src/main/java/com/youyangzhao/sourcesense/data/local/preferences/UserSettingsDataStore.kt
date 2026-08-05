package com.youyangzhao.sourcesense.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private const val USER_SETTINGS_DATA_STORE_NAME =
    "user_settings"

// Create one DataStore instance for application settings
val Context.userSettingsDataStore by preferencesDataStore(
    name = USER_SETTINGS_DATA_STORE_NAME
)

