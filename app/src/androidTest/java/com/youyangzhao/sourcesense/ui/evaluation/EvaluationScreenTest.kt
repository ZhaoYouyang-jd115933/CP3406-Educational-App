package com.youyangzhao.sourcesense.ui.evaluation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.youyangzhao.sourcesense.domain.model.AnswerOption
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvidenceCase
import com.youyangzhao.sourcesense.domain.model.SourceType
import com.youyangzhao.sourcesense.ui.theme.SourceSenseTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EvaluationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun unansweredQuestion_disablesCheckAnswer() {
        composeTestRule.setContent {
            SourceSenseTheme(
                dynamicColor = false
            ) {
                EvaluationScreen(
                    uiState = createEvaluationState(),
                    onOptionSelected = {},
                    onSubmitAnswer = {},
                    onPrevious = {},
                    onContinue = {},
                    onRetry = {},
                    onRetrySave = {},
                    reduceAnimations = true
                )
            }
        }

        composeTestRule
            .onNodeWithText("Question 1 of 2")
            .assertExists()

        composeTestRule
            .onNodeWithText("Check Answer")
            .performScrollTo()
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithText("Explanation")
            .assertDoesNotExist()
    }

    @Test
    fun checkingCorrectAnswer_showsFeedbackAndNextButton() {
        composeTestRule.setContent {
            var uiState by remember {
                mutableStateOf(
                    createEvaluationState()
                )
            }

            SourceSenseTheme(
                dynamicColor = false
            ) {
                EvaluationScreen(
                    uiState = uiState,
                    onOptionSelected = { optionId ->
                        val questionId =
                            uiState.currentQuestion?.id

                        if (questionId != null) {
                            // Store the selected answer in the test state
                            uiState = uiState.copy(
                                selectedAnswers =
                                    uiState.selectedAnswers +
                                            (
                                                    questionId to
                                                            optionId
                                                    )
                            )
                        }
                    },
                    onSubmitAnswer = {
                        val questionId =
                            uiState.currentQuestion?.id

                        if (questionId != null) {
                            // Mark the answer as submitted to show feedback
                            uiState = uiState.copy(
                                submittedQuestionIds =
                                    uiState.submittedQuestionIds +
                                            questionId
                            )
                        }
                    },
                    onPrevious = {},
                    onContinue = {},
                    onRetry = {},
                    onRetrySave = {},
                    reduceAnimations = true
                )
            }
        }

        composeTestRule
            .onNodeWithText(
                "Directly relevant",
                substring = true
            )
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithText("Check Answer")
            .performScrollTo()
            .assertIsEnabled()
            .performClick()

        val scrollableColumn =
            composeTestRule.onNode(
                hasScrollAction()
            )

        // Scroll until the feedback content is composed
        scrollableColumn.performScrollToNode(
            hasText("Correct")
        )

        composeTestRule
            .onNodeWithText("Correct")
            .assertExists()

        scrollableColumn.performScrollToNode(
            hasText(
                "The population and variables match",
                substring = true
            )
        )

        composeTestRule
            .onNodeWithText(
                "The population and variables match",
                substring = true
            )
            .assertExists()

        scrollableColumn.performScrollToNode(
            hasText("Learning Tip")
        )

        composeTestRule
            .onNodeWithText("Learning Tip")
            .assertExists()

        scrollableColumn.performScrollToNode(
            hasText("Next Question")
        )

        composeTestRule
            .onNodeWithText("Next Question")
            .assertIsEnabled()
    }

    @Test
    fun submittedWrongFinalAnswer_showsCorrectAnswerAndFinishes() {
        var continueClickCount = 0

        composeTestRule.setContent {
            SourceSenseTheme(
                dynamicColor = false
            ) {
                EvaluationScreen(
                    uiState = createEvaluationState(
                        currentQuestionIndex = 1,
                        selectedOptionId = "causation",
                        isSubmitted = true
                    ),
                    onOptionSelected = {},
                    onSubmitAnswer = {},
                    onPrevious = {},
                    onContinue = {
                        continueClickCount += 1
                    },
                    onRetry = {},
                    onRetrySave = {},
                    reduceAnimations = true
                )
            }
        }

        val scrollableColumn =
            composeTestRule.onNode(
                hasScrollAction()
            )

        // Scroll the LazyColumn until the feedback is composed
        scrollableColumn.performScrollToNode(
            hasText("Not Quite")
        )

        composeTestRule
            .onNodeWithText("Not Quite")
            .assertExists()

        scrollableColumn.performScrollToNode(
            hasText("Correct Answer")
        )

        composeTestRule
            .onNodeWithText("Correct Answer")
            .assertExists()

        scrollableColumn.performScrollToNode(
            hasText("View Result")
        )

        composeTestRule
            .onNodeWithText("View Result")
            .assertIsEnabled()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                1,
                continueClickCount
            )
        }
    }

    private fun createEvaluationState(
        currentQuestionIndex: Int = 0,
        selectedOptionId: String? = null,
        isSubmitted: Boolean = false
    ): EvaluationUiState {
        val evidenceCase =
            createEvidenceCase()

        val currentQuestion =
            evidenceCase.questions[
                currentQuestionIndex
            ]

        val selectedAnswers =
            if (selectedOptionId == null) {
                emptyMap()
            } else {
                mapOf(
                    currentQuestion.id to
                            selectedOptionId
                )
            }

        val submittedQuestionIds =
            if (isSubmitted) {
                setOf(
                    currentQuestion.id
                )
            } else {
                emptySet()
            }

        return EvaluationUiState(
            isLoading = false,
            evidenceCase = evidenceCase,
            difficultyLevel =
                DifficultyLevel.BEGINNER,
            currentQuestionIndex =
                currentQuestionIndex,
            selectedAnswers =
                selectedAnswers,
            submittedQuestionIds =
                submittedQuestionIds
        )
    }

    private fun createEvidenceCase():
            EvidenceCase {
        return EvidenceCase(
            id = "evaluation_test_case",
            researchQuestion =
                "Does regular exercise improve sleep quality among university students?",
            title =
                "Exercise Habits and Sleep Among University Students",
            authors =
                "J. Lim and P. Wong",
            publication =
                "Student Health Review",
            publishedYear = 2025,
            excerpt =
                "Students who exercised more frequently generally reported better sleep.",
            methodSummary =
                "Cross-sectional student survey",
            sampleSummary =
                "250 university students",
            sourceType =
                SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote =
                "This is a fictional practice source created for educational use.",
            questions = listOf(
                EvaluationQuestion(
                    id =
                        "relevance_question",
                    dimension =
                        EvaluationDimension.RELEVANCE,
                    prompt =
                        "How relevant is this source to the research question?",
                    options = listOf(
                        AnswerOption(
                            id =
                                "directly_relevant",
                            text =
                                "Directly relevant because it examines exercise and sleep among university students"
                        ),
                        AnswerOption(
                            id = "unrelated",
                            text =
                                "Unrelated because it does not discuss university students"
                        )
                    ),
                    correctOptionId =
                        "directly_relevant",
                    explanation =
                        "The population and variables match the research question.",
                    learningTip =
                        "Compare the source population, variables and context with the research question."
                ),
                EvaluationQuestion(
                    id =
                        "causation_question",
                    dimension =
                        EvaluationDimension.CAUSATION,
                    prompt =
                        "What conclusion is supported by this study?",
                    options = listOf(
                        AnswerOption(
                            id = "association",
                            text =
                                "The study found an association between exercise and sleep quality"
                        ),
                        AnswerOption(
                            id = "causation",
                            text =
                                "The study proves that exercise causes better sleep"
                        )
                    ),
                    correctOptionId =
                        "association",
                    explanation =
                        "A cross-sectional survey can identify an association but cannot prove causation.",
                    learningTip =
                        "Match the strength of a conclusion to the study design."
                )
            )
        )
    }
}

