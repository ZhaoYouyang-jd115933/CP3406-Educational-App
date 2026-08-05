package com.youyangzhao.sourcesense.ui.evaluation

import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvaluationResult
import com.youyangzhao.sourcesense.domain.model.EvidenceCase

data class EvaluationUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val evidenceCase: EvidenceCase? = null,
    val difficultyLevel: DifficultyLevel =
        DifficultyLevel.INTERMEDIATE,
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, String> = emptyMap(),
    val submittedQuestionIds: Set<String> = emptySet(),
    val result: EvaluationResult? = null,
    val errorMessage: String? = null,
    val saveErrorMessage: String? = null
) {
    val currentQuestion: EvaluationQuestion?
        get() = evidenceCase
            ?.questions
            ?.getOrNull(currentQuestionIndex)

    // Return the selected answer for the current question
    val selectedOptionId: String?
        get() {
            val questionId =
                currentQuestion?.id ?: return null

            return selectedAnswers[questionId]
        }

    val totalQuestions: Int
        get() = evidenceCase?.questions?.size ?: 0

    val questionNumber: Int
        get() {
            if (currentQuestion == null) {
                return 0
            }

            return currentQuestionIndex + 1
        }

    val progress: Float
        get() {
            if (totalQuestions == 0) {
                return 0f
            }

            return questionNumber.toFloat() /
                    totalQuestions.toFloat()
        }

    val isFirstQuestion: Boolean
        get() = currentQuestionIndex == 0

    val isLastQuestion: Boolean
        get() = totalQuestions > 0 &&
                currentQuestionIndex ==
                totalQuestions - 1

    // Check whether the current answer has been submitted
    val isCurrentAnswerSubmitted: Boolean
        get() {
            val questionId =
                currentQuestion?.id ?: return false

            return questionId in submittedQuestionIds
        }

    // Return correctness only after the answer is submitted
    val isCurrentAnswerCorrect: Boolean?
        get() {
            if (!isCurrentAnswerSubmitted) {
                return null
            }

            val question =
                currentQuestion ?: return null

            val selectedOption =
                selectedOptionId ?: return null

            return selectedOption ==
                    question.correctOptionId
        }

    // Enable answer checking after an option is selected
    val canSubmitAnswer: Boolean
        get() = selectedOptionId != null &&
                !isCurrentAnswerSubmitted &&
                !isLoading &&
                !isSaving &&
                result == null

    // Allow navigation only after feedback has been shown
    val canContinue: Boolean
        get() = isCurrentAnswerSubmitted &&
                !isLoading &&
                !isSaving &&
                result == null
}

