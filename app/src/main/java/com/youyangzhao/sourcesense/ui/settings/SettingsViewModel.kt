package com.youyangzhao.sourcesense.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.repository.StatisticsRepository
import com.youyangzhao.sourcesense.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userSettingsRepository:
    UserSettingsRepository,
    private val statisticsRepository:
    StatisticsRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState()
    )

    val uiState: StateFlow<SettingsUiState> =
        _uiState.asStateFlow()

    init {
        observeUserSettings()
        observeLearningStatistics()
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

    private fun observeLearningStatistics() {
        val repository = statisticsRepository ?: return

        viewModelScope.launch {
            repository
                .observeLearningStatistics()
                .catch {
                    _uiState.update { state ->
                        state.copy(
                            errorMessage =
                                "Learning data could not be loaded."
                        )
                    }
                }
                .collect { statistics ->
                    _uiState.update { state ->
                        state.copy(
                            learningStatistics = statistics
                        )
                    }
                }
        }
    }

    fun updateDifficultyLevel(
        difficultyLevel: DifficultyLevel
    ) {
        updateSetting {
            userSettingsRepository.updateDifficultyLevel(
                difficultyLevel = difficultyLevel
            )
        }
    }

    fun updateUseLargerText(
        enabled: Boolean
    ) {
        updateSetting {
            userSettingsRepository.updateUseLargerText(
                enabled = enabled
            )
        }
    }

    fun updateReduceAnimations(
        enabled: Boolean
    ) {
        updateSetting {
            userSettingsRepository.updateReduceAnimations(
                enabled = enabled
            )
        }
    }

    fun updateSoundFeedback(
        enabled: Boolean
    ) {
        updateSetting {
            userSettingsRepository.updateSoundFeedback(
                enabled = enabled
            )
        }
    }

    fun updateShowStatisticsRecommendation(
        enabled: Boolean
    ) {
        updateSetting {
            userSettingsRepository
                .updateShowStatisticsRecommendation(
                    enabled = enabled
                )
        }
    }

    fun updateShowStatisticsSkillAccuracy(
        enabled: Boolean
    ) {
        updateSetting {
            userSettingsRepository
                .updateShowStatisticsSkillAccuracy(
                    enabled = enabled
                )
        }
    }

    fun updateShowStatisticsSourcePractice(
        enabled: Boolean
    ) {
        updateSetting {
            userSettingsRepository
                .updateShowStatisticsSourcePractice(
                    enabled = enabled
                )
        }
    }

    fun updateShowStatisticsRecentActivity(
        enabled: Boolean
    ) {
        updateSetting {
            userSettingsRepository
                .updateShowStatisticsRecentActivity(
                    enabled = enabled
                )
        }
    }

    fun updateShowStatisticsSectionDescriptions(
        enabled: Boolean
    ) {
        updateSetting {
            userSettingsRepository
                .updateShowStatisticsSectionDescriptions(
                    enabled = enabled
                )
        }
    }

    private fun updateSetting(
        updateAction: suspend () -> Unit
    ) {
        if (!_uiState.value.canChangeSettings) {
            return
        }

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

    fun requestClearEvaluationHistory() {
        requestDataClear(
            target = SettingsDataClearTarget.EVALUATION_HISTORY
        )
    }

    fun requestClearSourceReviews() {
        requestDataClear(
            target = SettingsDataClearTarget.SOURCE_REVIEWS
        )
    }

    fun requestClearAllLearningData() {
        requestDataClear(
            target = SettingsDataClearTarget.ALL_LEARNING_DATA
        )
    }

    private fun requestDataClear(
        target: SettingsDataClearTarget
    ) {
        if (!canClear(target)) {
            return
        }

        _uiState.update { state ->
            state.copy(
                dataClearTarget = target
            )
        }
    }

    fun dismissDataClearConfirmation() {
        _uiState.update { state ->
            state.copy(
                dataClearTarget = null
            )
        }
    }

    fun confirmDataClear() {
        val repository = statisticsRepository ?: return
        val target = _uiState.value.dataClearTarget ?: return

        if (!canClear(target)) {
            return
        }

        _uiState.update { state ->
            state.copy(
                dataClearTarget = null,
                isClearingLearningData = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                when (target) {
                    SettingsDataClearTarget.EVALUATION_HISTORY -> {
                        repository.clearLearningHistory()
                    }

                    SettingsDataClearTarget.SOURCE_REVIEWS -> {
                        repository.clearSourceReviews()
                    }

                    SettingsDataClearTarget.ALL_LEARNING_DATA -> {
                        repository.clearAllLearningData()
                    }
                }
            }.onSuccess {
                // Room emits the new data totals after deletion
                _uiState.update { state ->
                    state.copy(
                        isClearingLearningData = false
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        isClearingLearningData = false,
                        errorMessage =
                            dataClearFailureMessage(target)
                    )
                }
            }
        }
    }

    private fun canClear(
        target: SettingsDataClearTarget
    ): Boolean {
        val state = _uiState.value

        return when (target) {
            SettingsDataClearTarget.EVALUATION_HISTORY -> {
                state.canClearEvaluationHistory
            }

            SettingsDataClearTarget.SOURCE_REVIEWS -> {
                state.canClearSourceReviews
            }

            SettingsDataClearTarget.ALL_LEARNING_DATA -> {
                state.canClearAllLearningData
            }
        }
    }

    private fun dataClearFailureMessage(
        target: SettingsDataClearTarget
    ): String {
        return when (target) {
            SettingsDataClearTarget.EVALUATION_HISTORY -> {
                "Evaluation history could not be cleared."
            }

            SettingsDataClearTarget.SOURCE_REVIEWS -> {
                "Source reviews could not be cleared."
            }

            SettingsDataClearTarget.ALL_LEARNING_DATA -> {
                "Learning data could not be cleared."
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
    UserSettingsRepository,
    private val statisticsRepository:
    StatisticsRepository? = null
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
                    userSettingsRepository,
                statisticsRepository =
                    statisticsRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}

