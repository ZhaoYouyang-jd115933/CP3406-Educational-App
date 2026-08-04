package com.youyangzhao.sourcesense.domain.model

enum class EvaluationDimension(
    val displayName: String
) {
    RELEVANCE("Relevance"),
    SOURCE_TYPE("Source Type"),
    EVIDENCE_STRENGTH("Evidence Strength"),
    CAUSATION("Correlation and Causation"),
    OVERCLAIMING("Overclaiming"),
    CITATION_SUPPORT("Citation Support")
}

data class EvaluationQuestion(
    val id: String,
    val dimension: EvaluationDimension,
    val prompt: String,
    val options: List<AnswerOption>,
    val correctOptionId: String,
    val explanation: String,
    val learningTip: String
) {
    init {
        // Ensure every question contains valid answer options
        require(options.isNotEmpty()) {
            "A question must contain at least one answer option."
        }

        // Ensure the correct answer exists in the option list
        require(options.any { option ->
            option.id == correctOptionId
        }) {
            "The correct answer must match an available option."
        }
    }
}