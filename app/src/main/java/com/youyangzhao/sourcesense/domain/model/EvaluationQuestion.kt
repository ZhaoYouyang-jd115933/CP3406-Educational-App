package com.youyangzhao.sourcesense.domain.model

enum class EvaluationDimension(
    val displayName: String
) {
    RELEVANCE("Relevance"),
    SOURCE_TYPE("Source Type"),
    EVIDENCE_STRENGTH("Evidence Strength"),
    CAUSATION("Correlation and Causation"),
    OVERCLAIMING("Overclaiming"),
    CITATION_SUPPORT("Citation Support"),
    GENERALISATION("Sample and Generalisation"),
    ALTERNATIVE_EXPLANATIONS("Alternative Explanations")
}

data class EvaluationQuestion(
    val id: String,
    val dimension: EvaluationDimension,
    val prompt: String,
    val options: List<AnswerOption>,
    val correctOptionId: String,
    val explanation: String,
    val learningTip: String,
    val minimumDifficulty: DifficultyLevel =
        DifficultyLevel.BEGINNER
) {
    init {
        // Ensure each question has answer options
        require(options.isNotEmpty()) {
            "A question must contain at least one answer option."
        }

        // Ensure the correct answer is available
        require(options.any { option ->
            option.id == correctOptionId
        }) {
            "The correct answer must match an available option."
        }
    }
}