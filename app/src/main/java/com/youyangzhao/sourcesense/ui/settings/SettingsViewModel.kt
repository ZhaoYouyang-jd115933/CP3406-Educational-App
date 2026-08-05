package com.youyangzhao.sourcesense.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState()
    )

    val uiState: StateFlow<SettingsUiState> =
        _uiState.asStateFlow()

    init {
        observeUserSettings()
    }

    private fun observeUserSettings() {
        viewModelScope.launch {
            userSettingsRepository
                .observeUserSettings()
                .catch {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage =
                                "User settings could not be loaded."
                        )
                    }
                }
                .collect { userSettings ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            userSettings = userSettings,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun updateDifficultyLevel(
        difficultyLevel: DifficultyLevel
    ) {
        if (!_uiState.value.canChangeSettings) {
            return
        }

        updateSetting {
            userSettingsRepository.updateDifficultyLevel(
                difficultyLevel = difficultyLevel
            )
        }
    }

    fun updateUseLargerText(
        enabled: Boolean
    ) {
        if (!_uiState.value.canChangeSettings) {
            return
        }

        updateSetting {
            userSettingsRepository.updateUseLargerText(
                enabled = enabled
            )
        }
    }

    fun updateReduceAnimations(
        enabled: Boolean
    ) {
        if (!_uiState.value.canChangeSettings) {
            return
        }

        updateSetting {
            userSettingsRepository.updateReduceAnimations(
                enabled = enabled
            )
        }
    }

    fun updateSoundFeedback(
        enabled: Boolean
    ) {
        if (!_uiState.value.canChangeSettings) {
            return
        }

        updateSetting {
            userSettingsRepository.updateSoundFeedback(
                enabled = enabled
            )
        }
    }

    private fun updateSetting(
        updateAction: suspend () -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                updateAction()
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        errorMessage =
                            "The setting could not be updated."
                    )
                }
            }
        }
    }

    fun requestResetSettings() {
        if (!_uiState.value.canResetSettings) {
            return
        }

        _uiState.update { state ->
            state.copy(
                showResetConfirmation = true
            )
        }
    }

    fun dismissResetConfirmation() {
        _uiState.update { state ->
            state.copy(
                showResetConfirmation = false
            )
        }
    }

    fun confirmResetSettings() {
        val currentState = _uiState.value

        if (!currentState.canResetSettings) {
            return
        }

        _uiState.update { state ->
            state.copy(
                showResetConfirmation = false,
                isResettingSettings = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                userSettingsRepository.resetUserSettings()
            }.onSuccess {
                // DataStore emits the restored default settings
                _uiState.update { state ->
                    state.copy(
                        isResettingSettings = false
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        isResettingSettings = false,
                        errorMessage =
                            "Default settings could not be restored."
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { state ->
            state.copy(
                errorMessage = null
            )
        }
    }
}

class SettingsViewModelFactory(
    private val userSettingsRepository:
    UserSettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                SettingsViewModel::class.java
            )
        ) {
            return SettingsViewModel(
                userSettingsRepository =
                    userSettingsRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}

