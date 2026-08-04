package com.youyangzhao.sourcesense.ui.evaluation

import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvaluationResult
import com.youyangzhao.sourcesense.domain.model.EvidenceCase

data class EvaluationUiState(
    val isLoading: Boolean = true,
    val evidenceCase: EvidenceCase? = null,
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, String> = emptyMap(),
    val result: EvaluationResult? = null,
    val errorMessage: String? = null
) {
    val currentQuestion: EvaluationQuestion?
        get() = evidenceCase
            ?.questions
            ?.getOrNull(currentQuestionIndex)

    val selectedOptionId: String?
        get() {
            val questionId = currentQuestion?.id ?: return null
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

            return questionNumber.toFloat() / totalQuestions.toFloat()
        }

    val isFirstQuestion: Boolean
        get() = currentQuestionIndex == 0

    val isLastQuestion: Boolean
        get() = totalQuestions > 0 &&
                currentQuestionIndex == totalQuestions - 1

    val canContinue: Boolean
        get() = selectedOptionId != null &&
                !isLoading &&
                result == null
}

