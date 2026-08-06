package com.youyangzhao.sourcesense.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youyangzhao.sourcesense.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val statisticsRepository:
    StatisticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        StatisticsUiState()
    )

    val uiState: StateFlow<StatisticsUiState> =
        _uiState.asStateFlow()

    init {
        observeStatistics()
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

    // Keep the original action for existing tests and UI calls
    fun requestClearHistory() {
        requestClearTarget(
            target =
                StatisticsClearTarget
                    .EVALUATION_HISTORY
        )
    }

    fun requestClearSourceReviews() {
        requestClearTarget(
            target =
                StatisticsClearTarget
                    .SOURCE_REVIEWS
        )
    }

    fun requestClearAllData() {
        requestClearTarget(
            target =
                StatisticsClearTarget
                    .ALL_LEARNING_DATA
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
        val target =
            currentState.clearTarget ?: return

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
                    StatisticsClearTarget
                        .EVALUATION_HISTORY -> {
                        // Use the legacy method to preserve existing repository tests
                        statisticsRepository
                            .clearLearningHistory()
                    }

                    StatisticsClearTarget
                        .SOURCE_REVIEWS -> {
                        statisticsRepository
                            .clearSourceReviews()
                    }

                    StatisticsClearTarget
                        .ALL_LEARNING_DATA -> {
                        statisticsRepository
                            .clearAllLearningData()
                    }
                }
            }.onSuccess {
                // Room emits the updated empty or partial statistics automatically
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
            StatisticsClearTarget
                .EVALUATION_HISTORY -> {
                currentState.canClearHistory
            }

            StatisticsClearTarget
                .SOURCE_REVIEWS -> {
                currentState.canClearSourceReviews
            }

            StatisticsClearTarget
                .ALL_LEARNING_DATA -> {
                currentState.canClearAllData
            }
        }
    }

    private fun clearFailureMessage(
        target: StatisticsClearTarget
    ): String {
        return when (target) {
            StatisticsClearTarget
                .EVALUATION_HISTORY -> {
                // Keep this wording stable for existing ViewModel tests
                "Learning history could not be cleared."
            }

            StatisticsClearTarget
                .SOURCE_REVIEWS -> {
                "Source reviews could not be cleared."
            }

            StatisticsClearTarget
                .ALL_LEARNING_DATA -> {
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
    StatisticsRepository
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
                    statisticsRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}

