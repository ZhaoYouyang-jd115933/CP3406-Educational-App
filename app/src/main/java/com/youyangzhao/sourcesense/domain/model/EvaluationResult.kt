package com.youyangzhao.sourcesense.domain.model

data class QuestionResult(
    val questionId: String,
    val dimension: EvaluationDimension,
    val selectedOptionId: String?,
    val selectedOptionText: String?,
    val correctOptionId: String,
    val correctOptionText: String,
    val isCorrect: Boolean,
    val explanation: String,
    val learningTip: String
)

data class EvaluationResult(
    val evidenceCaseId: String,
    val score: Int,
    val totalQuestions: Int,
    val questionResults: List<QuestionResult>,
    val difficultyLevel: DifficultyLevel =
        DifficultyLevel.INTERMEDIATE
) {
    val percentage: Int
        get() {
            if (totalQuestions == 0) {
                return 0
            }

            return score * 100 / totalQuestions
        }
}

// Calculate the result using the active difficulty
fun calculateEvaluationResult(
    evidenceCase: EvidenceCase,
    selectedAnswers: Map<String, String>,
    difficultyLevel: DifficultyLevel =
        DifficultyLevel.INTERMEDIATE
): EvaluationResult {
    val questionResults = evidenceCase.questions.map { question ->
        val selectedOptionId =
            selectedAnswers[question.id]

        val selectedOption =
            question.options.firstOrNull { option ->
                option.id == selectedOptionId
            }

        val correctOption =
            question.options.first { option ->
                option.id == question.correctOptionId
            }

        QuestionResult(
            questionId = question.id,
            dimension = question.dimension,
            selectedOptionId = selectedOptionId,
            selectedOptionText = selectedOption?.text,
            correctOptionId = question.correctOptionId,
            correctOptionText = correctOption.text,
            isCorrect =
                selectedOptionId == question.correctOptionId,
            explanation = question.explanation,
            learningTip = question.learningTip
        )
    }

    return EvaluationResult(
        evidenceCaseId = evidenceCase.id,
        score = questionResults.count { result ->
            result.isCorrect
        },
        totalQuestions = questionResults.size,
        questionResults = questionResults,
        difficultyLevel = difficultyLevel
    )
}

