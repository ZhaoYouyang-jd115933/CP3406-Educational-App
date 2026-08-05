package com.youyangzhao.sourcesense.ui.landing

import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.LearningModule

data class LandingUiState(
    val isLoading: Boolean = true,
    val difficultyLevel: DifficultyLevel =
        DifficultyLevel.INTERMEDIATE,
    val modules: List<LearningModule> = emptyList(),
    val errorMessage: String? = null
) {
    val hasModules: Boolean
        get() = modules.isNotEmpty()
}

