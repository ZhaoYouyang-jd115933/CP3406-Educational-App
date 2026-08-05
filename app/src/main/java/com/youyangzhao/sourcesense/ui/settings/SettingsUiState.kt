package com.youyangzhao.sourcesense.ui.settings

import com.youyangzhao.sourcesense.domain.model.UserSettings

data class SettingsUiState(
    val isLoading: Boolean = true,
    val userSettings: UserSettings = UserSettings(),
    val errorMessage: String? = null,
    val showResetConfirmation: Boolean = false,
    val isResettingSettings: Boolean = false
) {
    val canChangeSettings: Boolean
        get() = !isLoading &&
                !isResettingSettings

    val canResetSettings: Boolean
        get() = canChangeSettings &&
                userSettings != UserSettings()
}

