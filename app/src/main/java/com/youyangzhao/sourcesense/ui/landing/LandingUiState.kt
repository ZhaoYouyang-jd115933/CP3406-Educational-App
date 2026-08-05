package com.youyangzhao.sourcesense.ui.landing

import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.LearningModule

data class LearningModuleUiModel(
    val module: LearningModule,
    val attemptCount: Int = 0,
    val bestScore: Int? = null,
    val bestTotalQuestions: Int? = null,
    val bestPercentage: Int? = null
) {
    val isCompleted: Boolean
        get() = attemptCount > 0
}

data class LandingUiState(
    val isLoading: Boolean = true,
    val difficultyLevel: DifficultyLevel =
        DifficultyLevel.INTERMEDIATE,
    val modules: List<LearningModuleUiModel> = emptyList(),
    val errorMessage: String? = null
) {
    val hasModules: Boolean
        get() = modules.isNotEmpty()

    val totalModuleCount: Int
        get() = modules.size

    val completedModuleCount: Int
        get() = modules.count { module ->
            module.isCompleted
        }

    val overallProgress: Float
        get() {
            if (totalModuleCount == 0) {
                return 0f
            }

            return completedModuleCount.toFloat() /
                    totalModuleCount.toFloat()
        }
}

