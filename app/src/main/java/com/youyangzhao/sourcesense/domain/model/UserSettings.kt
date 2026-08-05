package com.youyangzhao.sourcesense.domain.model

enum class DifficultyLevel(
    val displayName: String,
    val description: String
) {
    BEGINNER(
        displayName = "Beginner",
        description = "Four core questions covering essential concepts"
    ),
    INTERMEDIATE(
        displayName = "Intermediate",
        description = "Six questions covering the complete evaluation"
    ),
    ADVANCED(
        displayName = "Advanced",
        description = "Eight questions including deeper evidence limitations"
    )
}

data class UserSettings(
    val difficultyLevel: DifficultyLevel =
        DifficultyLevel.INTERMEDIATE,
    val useLargerText: Boolean = false,
    val reduceAnimations: Boolean = false,
    val soundFeedbackEnabled: Boolean = true
)

