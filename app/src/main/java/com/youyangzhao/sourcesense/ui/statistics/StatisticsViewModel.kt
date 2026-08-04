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
    private val statisticsRepository: StatisticsRepository
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

    fun requestClearHistory() {
        val currentState = _uiState.value

        if (!currentState.canClearHistory) {
            return
        }

        _uiState.update { state ->
            state.copy(
                showClearConfirmation = true
            )
        }
    }

    fun dismissClearConfirmation() {
        _uiState.update { state ->
            state.copy(
                showClearConfirmation = false
            )
        }
    }

    fun confirmClearHistory() {
        val currentState = _uiState.value

        if (!currentState.canClearHistory) {
            return
        }

        _uiState.update { state ->
            state.copy(
                showClearConfirmation = false,
                isClearingHistory = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                statisticsRepository.clearLearningHistory()
            }.onSuccess {
                // Room automatically emits the empty statistics
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
                            "Learning history could not be cleared."
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

class StatisticsViewModelFactory(
    private val statisticsRepository: StatisticsRepository
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
                statisticsRepository = statisticsRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}

