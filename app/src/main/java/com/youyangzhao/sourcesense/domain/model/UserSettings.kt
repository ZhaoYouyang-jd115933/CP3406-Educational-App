package com.youyangzhao.sourcesense.domain.model

enum class DifficultyLevel(
    val displayName: String,
    val description: String
) {
    BEGINNER(
        displayName = "Beginner",
        description =
            "Essential concepts with clear source-evaluation guidance"
    ),
    INTERMEDIATE(
        displayName = "Intermediate",
        description =
            "Applied evaluation of methods, samples and evidence quality"
    ),
    ADVANCED(
        displayName = "Advanced",
        description =
            "Deeper analysis of statistics, conflicting evidence, reviews and ethics"
    )
}

data class UserSettings(
    val difficultyLevel: DifficultyLevel =
        DifficultyLevel.INTERMEDIATE,
    val useLargerText: Boolean = false,
    val reduceAnimations: Boolean = false,
    val soundFeedbackEnabled: Boolean = true
)

