package com.youyangzhao.sourcesense.domain.model

data class LearningModule(
    val id: String,
    val difficultyLevel: DifficultyLevel,
    val title: String,
    val description: String,
    val learningFocus: String,
    val evidenceCase: EvidenceCase
) {
    val questionCount: Int
        get() = evidenceCase.questions.size

    init {
        // Require a stable module identifier
        require(id.isNotBlank()) {
            "A learning module must have an ID."
        }

        // Require visible module information
        require(title.isNotBlank()) {
            "A learning module must have a title."
        }

        require(description.isNotBlank()) {
            "A learning module must have a description."
        }

        require(learningFocus.isNotBlank()) {
            "A learning module must have a learning focus."
        }
    }
}

