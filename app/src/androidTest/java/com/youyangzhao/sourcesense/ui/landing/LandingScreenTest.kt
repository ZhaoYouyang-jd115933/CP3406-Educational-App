package com.youyangzhao.sourcesense.ui.landing
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.youyangzhao.sourcesense.domain.model.AnswerOption
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvidenceCase
import com.youyangzhao.sourcesense.domain.model.LearningModule
import com.youyangzhao.sourcesense.domain.model.SourceType
import com.youyangzhao.sourcesense.ui.theme.SourceSenseTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun moduleList_showsOverallProgressAndBestResult() {
        composeTestRule.setContent {
            SourceSenseTheme(
                dynamicColor = false
            ) {
                LandingScreen(
                    uiState = createLandingState(),
                    onStartModule = {},
                    onRetry = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText(
                "1 of 2 modules completed"
            )
            .performScrollTo()
            .assertExists()

        composeTestRule
            .onNodeWithText("Completed")
            .performScrollTo()
            .assertExists()

        composeTestRule
            .onNodeWithText(
                "Best score: 4 / 4 (100%)"
            )
            .performScrollTo()
            .assertExists()

        composeTestRule
            .onNodeWithText("Attempts: 2")
            .assertExists()

        composeTestRule
            .onNodeWithText("Not Started")
            .performScrollTo()
            .assertExists()

        composeTestRule
            .onNodeWithText("Try Again")
            .performScrollTo()
            .assertExists()

        composeTestRule
            .onNodeWithText("Start Module")
            .performScrollTo()
            .assertExists()
    }

    @Test
    fun startModuleButton_returnsSelectedModuleId() {
        var selectedModuleId: String? = null

        composeTestRule.setContent {
            SourceSenseTheme(
                dynamicColor = false
            ) {
                LandingScreen(
                    uiState = createLandingState(),
                    onStartModule = { moduleId ->
                        selectedModuleId = moduleId
                    },
                    onRetry = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Start Module")
            .performScrollTo()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                NOT_STARTED_MODULE_ID,
                selectedModuleId
            )
        }
    }

    private fun createLandingState():
            LandingUiState {
        return LandingUiState(
            isLoading = false,
            difficultyLevel =
                DifficultyLevel.BEGINNER,
            modules = listOf(
                LearningModuleUiModel(
                    module = createModule(
                        id =
                            COMPLETED_MODULE_ID,
                        title =
                            "Relevance"
                    ),
                    attemptCount = 2,
                    bestScore = 4,
                    bestTotalQuestions = 4,
                    bestPercentage = 100
                ),
                LearningModuleUiModel(
                    module = createModule(
                        id =
                            NOT_STARTED_MODULE_ID,
                        title =
                            "Source Type"
                    )
                )
            )
        )
    }

    private fun createModule(
        id: String,
        title: String
    ): LearningModule {
        val questions =
            (1..4).map { index ->
                EvaluationQuestion(
                    id =
                        "${id}_question_$index",
                    dimension =
                        EvaluationDimension
                            .RELEVANCE,
                    prompt =
                        "Question $index",
                    options = listOf(
                        AnswerOption(
                            id =
                                "correct_$index",
                            text =
                                "Correct answer"
                        ),
                        AnswerOption(
                            id =
                                "wrong_$index",
                            text =
                                "Wrong answer"
                        )
                    ),
                    correctOptionId =
                        "correct_$index",
                    explanation =
                        "Explanation $index",
                    learningTip =
                        "Learning tip $index"
                )
            }

        return LearningModule(
            id = id,
            difficultyLevel =
                DifficultyLevel.BEGINNER,
            title = title,
            description =
                "Practice evaluating academic evidence.",
            learningFocus =
                "Identify the strongest evidence-based conclusion.",
            evidenceCase =
                EvidenceCase(
                    id = "${id}_case",
                    researchQuestion =
                        "What evidence supports the claim?",
                    title =
                        "Practice Source",
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
                        "Test source note",
                    questions =
                        questions
                )
        )
    }

    private companion object {
        const val COMPLETED_MODULE_ID =
            "completed_module"

        const val NOT_STARTED_MODULE_ID =
            "not_started_module"
    }
}

