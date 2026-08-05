package com.youyangzhao.sourcesense.ui.evaluation

import com.youyangzhao.sourcesense.domain.model.AnswerOption
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.EvaluationAttemptSummary
import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvaluationResult
import com.youyangzhao.sourcesense.domain.model.EvidenceCase
import com.youyangzhao.sourcesense.domain.model.LearningModule
import com.youyangzhao.sourcesense.domain.model.SourceType
import com.youyangzhao.sourcesense.domain.repository.EvaluationHistoryRepository
import com.youyangzhao.sourcesense.domain.repository.LearningModuleRepository
import com.youyangzhao.sourcesense.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EvaluationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun startModule_loadsSelectedModule() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val viewModel = createViewModel()

            viewModel.startModule(TEST_MODULE_ID)
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertEquals(
                TEST_CASE_ID,
                state.evidenceCase?.id
            )
            assertEquals(
                DifficultyLevel.BEGINNER,
                state.difficultyLevel
            )
            assertEquals(
                0,
                state.currentQuestionIndex
            )
            assertEquals(
                2,
                state.totalQuestions
            )
            assertNull(state.errorMessage)
        }

    @Test
    fun nextQuestion_beforeSubmittingAnswer_doesNothing() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val viewModel =
                createLoadedViewModel()

            viewModel.selectAnswer(
                optionId = "correct_1"
            )

            viewModel.moveToNextQuestion()

            val state = viewModel.uiState.value

            assertEquals(
                0,
                state.currentQuestionIndex
            )
            assertFalse(
                state.isCurrentAnswerSubmitted
            )
            assertEquals(
                "correct_1",
                state.selectedOptionId
            )
        }

    @Test
    fun submitAnswer_showsFeedbackAndLocksSelection() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val viewModel =
                createLoadedViewModel()

            viewModel.selectAnswer(
                optionId = "correct_1"
            )

            viewModel.submitCurrentAnswer()

            // The submitted answer must remain locked
            viewModel.selectAnswer(
                optionId = "wrong_1"
            )

            val state = viewModel.uiState.value

            assertTrue(
                state.isCurrentAnswerSubmitted
            )
            assertEquals(
                true,
                state.isCurrentAnswerCorrect
            )
            assertEquals(
                "correct_1",
                state.selectedOptionId
            )
            assertTrue(state.canContinue)
        }

    @Test
    fun nextQuestion_afterSubmittingAnswer_updatesProgress() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val viewModel =
                createLoadedViewModel()

            answerAndSubmit(
                viewModel = viewModel,
                optionId = "correct_1"
            )

            viewModel.moveToNextQuestion()

            val state = viewModel.uiState.value

            assertEquals(
                1,
                state.currentQuestionIndex
            )
            assertEquals(
                "question_2",
                state.currentQuestion?.id
            )
            assertFalse(
                state.isCurrentAnswerSubmitted
            )
            assertNull(
                state.selectedOptionId
            )
        }

    @Test
    fun finishLastQuestion_savesCompleteResultOnce() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val historyRepository =
                FakeEvaluationHistoryRepository()

            val viewModel =
                createLoadedViewModel(
                    historyRepository =
                        historyRepository
                )

            answerAndSubmit(
                viewModel = viewModel,
                optionId = "correct_1"
            )

            viewModel.moveToNextQuestion()

            answerAndSubmit(
                viewModel = viewModel,
                optionId = "wrong_2"
            )

            viewModel.moveToNextQuestion()

            // Complete the saving coroutine
            advanceUntilIdle()

            val result =
                viewModel.uiState.value.result

            assertNotNull(result)
            assertEquals(
                1,
                result?.score
            )
            assertEquals(
                2,
                result?.totalQuestions
            )
            assertEquals(
                50,
                result?.percentage
            )
            assertEquals(
                1,
                historyRepository.savedResults.size
            )
        }

    @Test
    fun failedSave_canBeRetried() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val historyRepository =
                FakeEvaluationHistoryRepository(
                    shouldFailSaving = true
                )

            val viewModel =
                createLoadedViewModel(
                    historyRepository =
                        historyRepository
                )

            answerAndSubmit(
                viewModel = viewModel,
                optionId = "correct_1"
            )

            viewModel.moveToNextQuestion()

            answerAndSubmit(
                viewModel = viewModel,
                optionId = "correct_2"
            )

            viewModel.moveToNextQuestion()
            advanceUntilIdle()

            assertNull(
                viewModel.uiState.value.result
            )

            assertEquals(
                "The result could not be saved. Please try again.",
                viewModel.uiState.value
                    .saveErrorMessage
            )

            historyRepository
                .shouldFailSaving = false

            viewModel.retrySavingResult()
            advanceUntilIdle()

            assertNotNull(
                viewModel.uiState.value.result
            )
            assertNull(
                viewModel.uiState.value
                    .saveErrorMessage
            )
            assertEquals(
                1,
                historyRepository.savedResults.size
            )
        }

    @Test
    fun restartEvaluation_clearsCurrentAttempt() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val viewModel =
                createLoadedViewModel()

            answerAndSubmit(
                viewModel = viewModel,
                optionId = "correct_1"
            )

            viewModel.moveToNextQuestion()

            answerAndSubmit(
                viewModel = viewModel,
                optionId = "correct_2"
            )

            viewModel.moveToNextQuestion()
            advanceUntilIdle()

            viewModel.restartEvaluation()
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(
                0,
                state.currentQuestionIndex
            )
            assertTrue(
                state.selectedAnswers.isEmpty()
            )
            assertTrue(
                state.submittedQuestionIds.isEmpty()
            )
            assertNull(state.result)
            assertFalse(state.isLoading)
            assertNotNull(state.evidenceCase)
        }

    @Test
    fun missingModule_displaysError() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val viewModel =
                EvaluationViewModel(
                    learningModuleRepository =
                        FakeLearningModuleRepository(),
                    evaluationHistoryRepository =
                        FakeEvaluationHistoryRepository()
                )

            viewModel.startModule(
                moduleId = "missing_module"
            )

            advanceUntilIdle()

            assertEquals(
                "The selected learning module is unavailable.",
                viewModel.uiState.value
                    .errorMessage
            )
        }

    private fun TestScope
            .createLoadedViewModel(
        historyRepository:
        EvaluationHistoryRepository =
            FakeEvaluationHistoryRepository()
    ): EvaluationViewModel {
        val viewModel = createViewModel(
            historyRepository =
                historyRepository
        )

        viewModel.startModule(
            moduleId = TEST_MODULE_ID
        )

        advanceUntilIdle()

        return viewModel
    }

    private fun createViewModel(
        historyRepository:
        EvaluationHistoryRepository =
            FakeEvaluationHistoryRepository()
    ): EvaluationViewModel {
        return EvaluationViewModel(
            learningModuleRepository =
                FakeLearningModuleRepository(
                    modules =
                        listOf(
                            createTestModule()
                        )
                ),
            evaluationHistoryRepository =
                historyRepository
        )
    }

    private fun answerAndSubmit(
        viewModel: EvaluationViewModel,
        optionId: String
    ) {
        viewModel.selectAnswer(
            optionId = optionId
        )

        viewModel.submitCurrentAnswer()
    }

    private fun createTestModule():
            LearningModule {
        return LearningModule(
            id = TEST_MODULE_ID,
            difficultyLevel =
                DifficultyLevel.BEGINNER,
            title = "Test Module",
            description = "Test description",
            learningFocus = "Test focus",
            evidenceCase =
                EvidenceCase(
                    id = TEST_CASE_ID,
                    researchQuestion =
                        "Does social media use cause depression?",
                    title =
                        "Social Media Use and Student Wellbeing",
                    authors = "Test Author",
                    publication = "Test Journal",
                    publishedYear = 2026,
                    excerpt =
                        "The study reported an association.",
                    methodSummary =
                        "Cross-sectional survey",
                    sampleSummary =
                        "100 university students",
                    sourceType =
                        SourceType
                            .PEER_REVIEWED_ARTICLE,
                    sourceNote =
                        "Test source",
                    questions = listOf(
                        createQuestion(
                            id = "question_1",
                            dimension =
                                EvaluationDimension
                                    .RELEVANCE,
                            correctOptionId =
                                "correct_1",
                            wrongOptionId =
                                "wrong_1"
                        ),
                        createQuestion(
                            id = "question_2",
                            dimension =
                                EvaluationDimension
                                    .CAUSATION,
                            correctOptionId =
                                "correct_2",
                            wrongOptionId =
                                "wrong_2"
                        )
                    )
                )
        )
    }

    private fun createQuestion(
        id: String,
        dimension: EvaluationDimension,
        correctOptionId: String,
        wrongOptionId: String
    ): EvaluationQuestion {
        return EvaluationQuestion(
            id = id,
            dimension = dimension,
            prompt =
                "Evaluate this evidence.",
            options = listOf(
                AnswerOption(
                    id = correctOptionId,
                    text = "Correct answer"
                ),
                AnswerOption(
                    id = wrongOptionId,
                    text = "Incorrect answer"
                )
            ),
            correctOptionId =
                correctOptionId,
            explanation =
                "Test explanation",
            learningTip =
                "Test learning tip"
        )
    }

    private class FakeLearningModuleRepository(
        private val modules:
        List<LearningModule> = emptyList()
    ) : LearningModuleRepository {

        override suspend fun
                getModulesForDifficulty(
            difficultyLevel:
            DifficultyLevel
        ): List<LearningModule> {
            return modules.filter { module ->
                module.difficultyLevel ==
                        difficultyLevel
            }
        }

        override suspend fun
                getLearningModule(
            moduleId: String
        ): LearningModule? {
            return modules.firstOrNull { module ->
                module.id == moduleId
            }
        }
    }

    private class FakeEvaluationHistoryRepository(
        var shouldFailSaving:
        Boolean = false
    ) : EvaluationHistoryRepository {

        private val attemptsFlow =
            MutableStateFlow<
                    List<EvaluationAttemptSummary>
                    >(
                emptyList()
            )

        val savedResults =
            mutableListOf<EvaluationResult>()

        override fun
                observeEvaluationAttempts():
                Flow<List<EvaluationAttemptSummary>> {
            return attemptsFlow
        }

        override suspend fun
                saveEvaluationResult(
            result: EvaluationResult
        ): Long {
            if (shouldFailSaving) {
                throw IllegalStateException(
                    "Database failure"
                )
            }

            savedResults += result

            return savedResults.size.toLong()
        }
    }

    private companion object {
        const val TEST_MODULE_ID =
            "test_module"

        const val TEST_CASE_ID =
            "test_case"
    }
}