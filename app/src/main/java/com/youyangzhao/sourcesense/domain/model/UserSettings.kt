package com.youyangzhao.sourcesense.domain.model

enum class DifficultyLevel(
    val displayName: String,
    val description: String
) {
    BEGINNER(
        displayName = "Beginner",
        description = "Clear guidance and simpler evidence cases"
    ),
    INTERMEDIATE(
        displayName = "Intermediate",
        description = "Balanced guidance and moderate complexity"
    ),
    ADVANCED(
        displayName = "Advanced",
        description = "Less guidance and more complex evidence cases"
    )
}

data class UserSettings(
    val difficultyLevel: DifficultyLevel =
        DifficultyLevel.INTERMEDIATE,
    val useLargerText: Boolean = false,
    val reduceAnimations: Boolean = false,
    val soundFeedbackEnabled: Boolean = true
)

