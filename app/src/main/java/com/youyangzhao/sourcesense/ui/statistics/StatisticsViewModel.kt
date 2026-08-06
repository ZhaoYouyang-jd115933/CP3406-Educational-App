package com.youyangzhao.sourcesense.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youyangzhao.sourcesense.domain.repository.StatisticsRepository
import com.youyangzhao.sourcesense.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val statisticsRepository:
    StatisticsRepository,
    private val userSettingsRepository:
    UserSettingsRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        StatisticsUiState()
    )

    val uiState: StateFlow<StatisticsUiState> =
        _uiState.asStateFlow()

    init {
        observeStatistics()
        observeDisplaySettings()
    }

    private fun observeStatistics() {
        viewModelScope.launch {
            statisticsRepository
                .observeLearningStatistics()
                .catch {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage =
                                "Learning statistics could not be loaded."
                        )
                    }
                }
                .collect { statistics ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            statistics = statistics,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    private fun observeDisplaySettings() {
        val repository = userSettingsRepository ?: return

        viewModelScope.launch {
            repository
                .observeUserSettings()
                .catch {
                    // Keep default display settings if DataStore cannot be read
                }
                .collect { userSettings ->
                    _uiState.update { state ->
                        state.copy(
                            userSettings = userSettings
                        )
                    }
                }
        }
    }

    // Keep the original clear actions for existing tests
    fun requestClearHistory() {
        requestClearTarget(
            target =
                StatisticsClearTarget.EVALUATION_HISTORY
        )
    }

    fun requestClearSourceReviews() {
        requestClearTarget(
            target =
                StatisticsClearTarget.SOURCE_REVIEWS
        )
    }

    fun requestClearAllData() {
        requestClearTarget(
            target =
                StatisticsClearTarget.ALL_LEARNING_DATA
        )
    }

    private fun requestClearTarget(
        target: StatisticsClearTarget
    ) {
        if (!canClear(target)) {
            return
        }

        _uiState.update { state ->
            state.copy(
                clearTarget = target
            )
        }
    }

    fun dismissClearConfirmation() {
        _uiState.update { state ->
            state.copy(
                clearTarget = null
            )
        }
    }

    fun confirmClearHistory() {
        val currentState = _uiState.value
        val target = currentState.clearTarget ?: return

        if (!canClear(target)) {
            return
        }

        _uiState.update { state ->
            state.copy(
                clearTarget = null,
                isClearingHistory = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                when (target) {
                    StatisticsClearTarget.EVALUATION_HISTORY -> {
                        statisticsRepository.clearLearningHistory()
                    }

                    StatisticsClearTarget.SOURCE_REVIEWS -> {
                        statisticsRepository.clearSourceReviews()
                    }

                    StatisticsClearTarget.ALL_LEARNING_DATA -> {
                        statisticsRepository.clearAllLearningData()
                    }
                }
            }.onSuccess {
                // Room emits the updated statistics after deletion
                _uiState.update { state ->
                    state.copy(
                        isClearingHistory = false
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        isClearingHistory = false,
                        errorMessage =
                            clearFailureMessage(target)
                    )
                }
            }
        }
    }

    private fun canClear(
        target: StatisticsClearTarget
    ): Boolean {
        val currentState = _uiState.value

        return when (target) {
            StatisticsClearTarget.EVALUATION_HISTORY -> {
                currentState.canClearHistory
            }

            StatisticsClearTarget.SOURCE_REVIEWS -> {
                currentState.canClearSourceReviews
            }

            StatisticsClearTarget.ALL_LEARNING_DATA -> {
                currentState.canClearAllData
            }
        }
    }

    private fun clearFailureMessage(
        target: StatisticsClearTarget
    ): String {
        return when (target) {
            StatisticsClearTarget.EVALUATION_HISTORY -> {
                "Learning history could not be cleared."
            }

            StatisticsClearTarget.SOURCE_REVIEWS -> {
                "Source reviews could not be cleared."
            }

            StatisticsClearTarget.ALL_LEARNING_DATA -> {
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

class StatisticsViewModelFactory(
    private val statisticsRepository:
    StatisticsRepository,
    private val userSettingsRepository:
    UserSettingsRepository? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                StatisticsViewModel::class.java
            )
        ) {
            return StatisticsViewModel(
                statisticsRepository =
                    statisticsRepository,
                userSettingsRepository =
                    userSettingsRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}


