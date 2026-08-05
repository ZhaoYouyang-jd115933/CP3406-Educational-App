package com.youyangzhao.sourcesense.ui.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.repository.LearningModuleRepository
import com.youyangzhao.sourcesense.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LandingViewModel(
    private val learningModuleRepository:
    LearningModuleRepository,
    private val userSettingsRepository:
    UserSettingsRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(LandingUiState())

    val uiState: StateFlow<LandingUiState> =
        _uiState.asStateFlow()

    init {
        observeDifficultyLevel()
    }

    private fun observeDifficultyLevel() {
        viewModelScope.launch {
            userSettingsRepository
                .observeUserSettings()
                .map { settings ->
                    settings.difficultyLevel
                }
                .distinctUntilChanged()
                .collect { difficultyLevel ->
                    loadModules(
                        difficultyLevel = difficultyLevel
                    )
                }
        }
    }

    private suspend fun loadModules(
        difficultyLevel: DifficultyLevel
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = true,
                difficultyLevel = difficultyLevel,
                errorMessage = null
            )
        }

        runCatching {
            learningModuleRepository
                .getModulesForDifficulty(
                    difficultyLevel = difficultyLevel
                )
        }.onSuccess { modules ->
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    difficultyLevel = difficultyLevel,
                    modules = modules,
                    errorMessage = null
                )
            }
        }.onFailure {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    difficultyLevel = difficultyLevel,
                    modules = emptyList(),
                    errorMessage =
                        "Learning modules could not be loaded."
                )
            }
        }
    }

    fun retryLoading() {
        viewModelScope.launch {
            loadModules(
                difficultyLevel =
                    _uiState.value.difficultyLevel
            )
        }
    }

    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(
                errorMessage = null
            )
        }
    }
}

class LandingViewModelFactory(
    private val learningModuleRepository:
    LearningModuleRepository,
    private val userSettingsRepository:
    UserSettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                LandingViewModel::class.java
            )
        ) {
            return LandingViewModel(
                learningModuleRepository =
                    learningModuleRepository,
                userSettingsRepository =
                    userSettingsRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}

