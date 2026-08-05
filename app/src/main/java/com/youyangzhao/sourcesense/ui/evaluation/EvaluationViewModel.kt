package com.youyangzhao.sourcesense.ui.evaluation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youyangzhao.sourcesense.domain.model.calculateEvaluationResult
import com.youyangzhao.sourcesense.domain.repository.EvaluationHistoryRepository
import com.youyangzhao.sourcesense.domain.repository.LearningModuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EvaluationViewModel(
    private val learningModuleRepository:
    LearningModuleRepository,
    private val evaluationHistoryRepository:
    EvaluationHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        EvaluationUiState(
            isLoading = false
        )
    )

    val uiState: StateFlow<EvaluationUiState> =
        _uiState.asStateFlow()

    private var activeModuleId: String? = null

    fun startModule(
        moduleId: String
    ) {
        activeModuleId = moduleId

        loadModule(
            moduleId = moduleId
        )
    }

    private fun loadModule(
        moduleId: String
    ) {
        viewModelScope.launch {
            _uiState.value = EvaluationUiState(
                isLoading = true
            )

            runCatching {
                learningModuleRepository
                    .getLearningModule(
                        moduleId = moduleId
                    )
            }.onSuccess { learningModule ->
                if (learningModule == null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage =
                                "The selected learning module is unavailable."
                        )
                    }
                } else {
                    activeModuleId = learningModule.id

                    // Start the module with a clean evaluation state
                    _uiState.value = EvaluationUiState(
                        isLoading = false,
                        evidenceCase =
                            learningModule.evidenceCase,
                        difficultyLevel =
                            learningModule.difficultyLevel
                    )
                }
            }.onFailure {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage =
                            "The learning module could not be loaded."
                    )
                }
            }
        }
    }

    fun selectAnswer(
        optionId: String
    ) {
        val currentState = _uiState.value

        val currentQuestion =
            currentState.currentQuestion ?: return

        // Lock the answer after it has been checked
        if (
            currentState.isCurrentAnswerSubmitted ||
            currentState.result != null ||
            currentState.isSaving
        ) {
            return
        }

        _uiState.update { state ->
            state.copy(
                selectedAnswers =
                    state.selectedAnswers +
                            (
                                    currentQuestion.id to
                                            optionId
                                    ),
                saveErrorMessage = null
            )
        }
    }

    fun submitCurrentAnswer() {
        val currentState = _uiState.value

        val currentQuestion =
            currentState.currentQuestion ?: return

        if (!currentState.canSubmitAnswer) {
            return
        }

        // Mark the answer as submitted before showing feedback
        _uiState.update { state ->
            state.copy(
                submittedQuestionIds =
                    state.submittedQuestionIds +
                            currentQuestion.id,
                saveErrorMessage = null
            )
        }
    }

    fun moveToNextQuestion() {
        val currentState = _uiState.value

        if (!currentState.canContinue) {
            return
        }

        if (currentState.isLastQuestion) {
            completeEvaluation()
            return
        }

        _uiState.update { state ->
            state.copy(
                currentQuestionIndex =
                    state.currentQuestionIndex + 1,
                saveErrorMessage = null
            )
        }
    }

    fun moveToPreviousQuestion() {
        val currentState = _uiState.value

        if (
            currentState.isFirstQuestion ||
            currentState.result != null ||
            currentState.isSaving
        ) {
            return
        }

        _uiState.update { state ->
            state.copy(
                currentQuestionIndex =
                    state.currentQuestionIndex - 1,
                saveErrorMessage = null
            )
        }
    }

    private fun completeEvaluation() {
        val currentState = _uiState.value

        val evidenceCase =
            currentState.evidenceCase ?: return

        if (
            currentState.isSaving ||
            currentState.result != null
        ) {
            return
        }

        // Save only after every question has been answered and checked
        val allQuestionsSubmitted =
            evidenceCase.questions.all { question ->
                currentState.selectedAnswers[
                    question.id
                ] != null &&
                        question.id in
                        currentState.submittedQuestionIds
            }

        if (!allQuestionsSubmitted) {
            return
        }

        val evaluationResult =
            calculateEvaluationResult(
                evidenceCase = evidenceCase,
                selectedAnswers =
                    currentState.selectedAnswers,
                difficultyLevel =
                    currentState.difficultyLevel
            )

        _uiState.update { state ->
            state.copy(
                isSaving = true,
                saveErrorMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                evaluationHistoryRepository
                    .saveEvaluationResult(
                        result = evaluationResult
                    )
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isSaving = false,
                        result = evaluationResult
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        isSaving = false,
                        saveErrorMessage =
                            "The result could not be saved. Please try again."
                    )
                }
            }
        }
    }

    fun retrySavingResult() {
        val currentState = _uiState.value

        if (
            currentState.isLastQuestion &&
            currentState.isCurrentAnswerSubmitted &&
            currentState.result == null &&
            !currentState.isSaving
        ) {
            completeEvaluation()
        }
    }

    fun restartEvaluation() {
        val moduleId = activeModuleId

        if (moduleId == null) {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    errorMessage =
                        "No learning module has been selected."
                )
            }

            return
        }

        loadModule(
            moduleId = moduleId
        )
    }

    fun retryLoading() {
        val moduleId = activeModuleId

        if (moduleId == null) {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    errorMessage =
                        "No learning module has been selected."
                )
            }

            return
        }

        loadModule(
            moduleId = moduleId
        )
    }

    fun clearError() {
        _uiState.update { state ->
            state.copy(
                errorMessage = null
            )
        }
    }
}

class EvaluationViewModelFactory(
    private val learningModuleRepository:
    LearningModuleRepository,
    private val evaluationHistoryRepository:
    EvaluationHistoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                EvaluationViewModel::class.java
            )
        ) {
            return EvaluationViewModel(
                learningModuleRepository =
                    learningModuleRepository,
                evaluationHistoryRepository =
                    evaluationHistoryRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}

