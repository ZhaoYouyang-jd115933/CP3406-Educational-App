package com.youyangzhao.sourcesense.domain.repository

import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.LearningModule

interface LearningModuleRepository {

    suspend fun getModulesForDifficulty(
        difficultyLevel: DifficultyLevel
    ): List<LearningModule>

    suspend fun getLearningModule(
        moduleId: String
    ): LearningModule?
}

