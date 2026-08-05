package com.youyangzhao.sourcesense.ui.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.EvaluationAttemptSummary
import com.youyangzhao.sourcesense.domain.model.LearningModule
import com.youyangzhao.sourcesense.domain.repository.EvaluationHistoryRepository
import com.youyangzhao.sourcesense.domain.repository.LearningModuleRepository
import com.youyangzhao.sourcesense.domain.repository.UserSettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LandingViewModel(
    private val learningModuleRepository:
    LearningModuleRepository,
    private val userSettingsRepository:
    UserSettingsRepository,
    private val evaluationHistoryRepository:
    EvaluationHistoryRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(LandingUiState())

    val uiState: StateFlow<LandingUiState> =
        _uiState.asStateFlow()

    private var observationJob: Job? = null

    init {
        observeLandingContent()
    }

    private fun observeLandingContent() {
        observationJob?.cancel()

        observationJob = viewModelScope.launch {
            combine(
                userSettingsRepository
                    .observeUserSettings()
                    .map { settings ->
                        settings.difficultyLevel
                    }
                    .distinctUntilChanged(),
                evaluationHistoryRepository
                    .observeEvaluationAttempts()
            ) { difficultyLevel, attempts ->
                difficultyLevel to attempts
            }
                .catch {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage =
                                "Learning progress could not be loaded."
                        )
                    }
                }
                .collect { content ->
                    loadModules(
                        difficultyLevel = content.first,
                        attempts = content.second
                    )
                }
        }
    }

    private suspend fun loadModules(
        difficultyLevel: DifficultyLevel,
        attempts: List<EvaluationAttemptSummary>
    ) {
        val shouldShowLoading =
            _uiState.value.difficultyLevel != difficultyLevel ||
                    !_uiState.value.hasModules

        _uiState.update { currentState ->
            currentState.copy(
                isLoading = shouldShowLoading,
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
            val moduleUiModels = modules.map { module ->
                createModuleUiModel(
                    module = module,
                    attempts = attempts
                )
            }

            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    difficultyLevel = difficultyLevel,
                    modules = moduleUiModels,
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

    private fun createModuleUiModel(
        module: LearningModule,
        attempts: List<EvaluationAttemptSummary>
    ): LearningModuleUiModel {
        // Ignore attempts from an older question set
        val matchingAttempts = attempts.filter { attempt ->
            attempt.evidenceCaseId == module.evidenceCase.id &&
                    attempt.totalQuestions == module.questionCount
        }

        val bestAttempt = matchingAttempts.maxWithOrNull(
            compareBy<EvaluationAttemptSummary> { attempt ->
                attempt.percentage
            }.thenBy { attempt ->
                attempt.score
            }.thenBy { attempt ->
                attempt.completedAt
            }
        )

        return LearningModuleUiModel(
            module = module,
            attemptCount = matchingAttempts.size,
            bestScore = bestAttempt?.score,
            bestTotalQuestions =
                bestAttempt?.totalQuestions,
            bestPercentage = bestAttempt?.percentage
        )
    }

    fun retryLoading() {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        observeLandingContent()
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
    UserSettingsRepository,
    private val evaluationHistoryRepository:
    EvaluationHistoryRepository
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
                    userSettingsRepository,
                evaluationHistoryRepository =
                    evaluationHistoryRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}

