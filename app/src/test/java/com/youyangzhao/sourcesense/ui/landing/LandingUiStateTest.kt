package com.youyangzhao.sourcesense.ui.landing

import com.youyangzhao.sourcesense.domain.model.AnswerOption
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvidenceCase
import com.youyangzhao.sourcesense.domain.model.LearningModule
import com.youyangzhao.sourcesense.domain.model.SourceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LandingUiStateTest {

    @Test
    fun emptyState_hasZeroProgress() {
        val state = LandingUiState(
            isLoading = false
        )

        assertFalse(state.hasModules)
        assertEquals(
            0,
            state.totalModuleCount
        )
        assertEquals(
            0,
            state.completedModuleCount
        )
        assertEquals(
            0f,
            state.overallProgress
        )
    }

    @Test
    fun completedModules_areCountedOnce() {
        val state = LandingUiState(
            isLoading = false,
            modules = listOf(
                LearningModuleUiModel(
                    module =
                        createModule(
                            id = "module_1"
                        ),
                    attemptCount = 3
                ),
                LearningModuleUiModel(
                    module =
                        createModule(
                            id = "module_2"
                        )
                )
            )
        )

        assertTrue(state.hasModules)
        assertEquals(
            2,
            state.totalModuleCount
        )
        assertEquals(
            1,
            state.completedModuleCount
        )
        assertEquals(
            0.5f,
            state.overallProgress
        )
    }

    private fun createModule(
        id: String
    ): LearningModule {
        val question =
            EvaluationQuestion(
                id = "${id}_question",
                dimension =
                    EvaluationDimension
                        .RELEVANCE,
                prompt =
                    "Is the source relevant?",
                options = listOf(
                    AnswerOption(
                        id = "yes",
                        text = "Yes"
                    ),
                    AnswerOption(
                        id = "no",
                        text = "No"
                    )
                ),
                correctOptionId = "yes",
                explanation =
                    "Test explanation",
                learningTip =
                    "Test tip"
            )

        return LearningModule(
            id = id,
            difficultyLevel =
                DifficultyLevel.BEGINNER,
            title = "Module $id",
            description =
                "Test description",
            learningFocus =
                "Test focus",
            evidenceCase =
                EvidenceCase(
                    id = "${id}_case",
                    researchQuestion =
                        "Test question",
                    title =
                        "Test source",
                    authors =
                        "Test Author",
                    publication =
                        "Test Journal",
                    publishedYear = 2026,
                    excerpt =
                        "Test excerpt",
                    methodSummary =
                        "Test method",
                    sampleSummary =
                        "Test sample",
                    sourceType =
                        SourceType
                            .PEER_REVIEWED_ARTICLE,
                    sourceNote =
                        "Test note",
                    questions =
                        listOf(question)
                )
        )
    }
}

