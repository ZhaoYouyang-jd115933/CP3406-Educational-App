package com.youyangzhao.sourcesense.ui.evaluation

import com.youyangzhao.sourcesense.domain.model.AnswerOption
import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvaluationResult
import com.youyangzhao.sourcesense.domain.model.EvidenceCase
import com.youyangzhao.sourcesense.domain.model.SourceType
import com.youyangzhao.sourcesense.domain.repository.EvaluationHistoryRepository
import com.youyangzhao.sourcesense.domain.repository.EvidenceRepository
import com.youyangzhao.sourcesense.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    fun initialization_repositoryReturnsCase_loadsEvidenceCase() = runTest {
        val repository = FakeEvidenceRepository(
            evidenceCases = listOf(createTestEvidenceCase())
        )

        val viewModel = EvaluationViewModel(
            evidenceRepository = repository,
            evaluationHistoryRepository =
                FakeEvaluationHistoryRepository()
        )

        // Complete the evidence loading coroutine
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertFalse(state.isSaving)
        assertEquals("test_case", state.evidenceCase?.id)
        assertEquals(0, state.currentQuestionIndex)
        assertNull(state.errorMessage)
        assertNull(state.saveErrorMessage)
    }

    @Test
    fun moveToNextQuestion_withoutAnswer_staysOnCurrentQuestion() = runTest {
        val viewModel = createLoadedViewModel()

        viewModel.moveToNextQuestion()

        val state = viewModel.uiState.value

        assertEquals(0, state.currentQuestionIndex)
        assertTrue(state.selectedAnswers.isEmpty())
        assertNull(state.result)
    }

    @Test
    fun selectAnswer_thenMoveToNextQuestion_updatesProgress() = runTest {
        val viewModel = createLoadedViewModel()

        viewModel.selectAnswer("correct_1")
        viewModel.moveToNextQuestion()

        val state = viewModel.uiState.value

        assertEquals(1, state.currentQuestionIndex)
        assertEquals("question_2", state.currentQuestion?.id)
        assertEquals(
            "correct_1",
            state.selectedAnswers["question_1"]
        )
    }

    @Test
    fun moveToPreviousQuestion_afterMovingForward_returnsToFirstQuestion() =
        runTest {
            val viewModel = createLoadedViewModel()

            viewModel.selectAnswer("correct_1")
            viewModel.moveToNextQuestion()
            viewModel.moveToPreviousQuestion()

            val state = viewModel.uiState.value

            assertEquals(0, state.currentQuestionIndex)
            assertEquals("question_1", state.currentQuestion?.id)
            assertEquals("correct_1", state.selectedOptionId)
        }

    @Test
    fun finishLastQuestion_savesAndGeneratesEvaluationResult() = runTest {
        val historyRepository = FakeEvaluationHistoryRepository()
        val viewModel = createLoadedViewModel(
            evaluationHistoryRepository = historyRepository
        )

        viewModel.selectAnswer("correct_1")
        viewModel.moveToNextQuestion()

        viewModel.selectAnswer("wrong_2")
        viewModel.moveToNextQuestion()

        // Complete the result saving coroutine
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val result = state.result

        assertNotNull(result)
        assertFalse(state.isSaving)
        assertNull(state.saveErrorMessage)
        assertEquals(1, result?.score)
        assertEquals(2, result?.totalQuestions)
        assertEquals(50, result?.percentage)
        assertEquals(1, historyRepository.savedResults.size)
        assertEquals(
            result,
            historyRepository.savedResults.first()
        )
    }

    @Test
    fun restartEvaluation_clearsAnswersResultAndProgress() = runTest {
        val viewModel = createLoadedViewModel()

        viewModel.selectAnswer("correct_1")
        viewModel.moveToNextQuestion()

        viewModel.selectAnswer("correct_2")
        viewModel.moveToNextQuestion()

        // Complete the result saving coroutine
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.result)

        viewModel.restartEvaluation()

        val state = viewModel.uiState.value

        assertEquals(0, state.currentQuestionIndex)
        assertTrue(state.selectedAnswers.isEmpty())
        assertNull(state.result)
        assertNull(state.saveErrorMessage)
        assertFalse(state.isLoading)
        assertFalse(state.isSaving)
        assertNotNull(state.evidenceCase)
    }

    @Test
    fun initialization_repositoryFails_displaysErrorState() = runTest {
        val repository = FakeEvidenceRepository(
            failure = IllegalStateException("Test failure")
        )

        val viewModel = EvaluationViewModel(
            evidenceRepository = repository,
            evaluationHistoryRepository =
                FakeEvaluationHistoryRepository()
        )

        // Complete the failing loading coroutine
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNull(state.evidenceCase)
        assertEquals(
            "The evidence case could not be loaded.",
            state.errorMessage
        )
    }

    @Test
    fun finishLastQuestion_saveFails_displaysSaveError() = runTest {
        val historyRepository = FakeEvaluationHistoryRepository(
            failure = IllegalStateException("Database failure")
        )

        val viewModel = createLoadedViewModel(
            evaluationHistoryRepository = historyRepository
        )

        viewModel.selectAnswer("correct_1")
        viewModel.moveToNextQuestion()

        viewModel.selectAnswer("correct_2")
        viewModel.moveToNextQuestion()

        // Complete the failing result saving coroutine
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isSaving)
        assertNull(state.result)
        assertEquals(
            "The result could not be saved. Please try again.",
            state.saveErrorMessage
        )
        assertTrue(historyRepository.savedResults.isEmpty())
    }

    private fun TestScope.createLoadedViewModel(
        evaluationHistoryRepository:
        EvaluationHistoryRepository =
            FakeEvaluationHistoryRepository()
    ): EvaluationViewModel {
        val repository = FakeEvidenceRepository(
            evidenceCases = listOf(createTestEvidenceCase())
        )

        val viewModel = EvaluationViewModel(
            evidenceRepository = repository,
            evaluationHistoryRepository =
                evaluationHistoryRepository
        )

        // Finish repository loading before interacting with state
        advanceUntilIdle()

        return viewModel
    }

    private fun createTestEvidenceCase(): EvidenceCase {
        return EvidenceCase(
            id = "test_case",
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
                SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote =
                "Test evidence case",
            questions = listOf(
                createQuestion(
                    id = "question_1",
                    dimension =
                        EvaluationDimension.RELEVANCE,
                    correctOptionId = "correct_1",
                    incorrectOptionId = "wrong_1"
                ),
                createQuestion(
                    id = "question_2",
                    dimension =
                        EvaluationDimension.CAUSATION,
                    correctOptionId = "correct_2",
                    incorrectOptionId = "wrong_2"
                )
            )
        )
    }

    private fun createQuestion(
        id: String,
        dimension: EvaluationDimension,
        correctOptionId: String,
        incorrectOptionId: String
    ): EvaluationQuestion {
        return EvaluationQuestion(
            id = id,
            dimension = dimension,
            prompt = "Evaluate this evidence.",
            options = listOf(
                AnswerOption(
                    id = correctOptionId,
                    text = "Correct answer"
                ),
                AnswerOption(
                    id = incorrectOptionId,
                    text = "Incorrect answer"
                )
            ),
            correctOptionId = correctOptionId,
            explanation = "Test explanation",
            learningTip = "Test learning tip"
        )
    }

    private class FakeEvidenceRepository(
        private val evidenceCases:
        List<EvidenceCase> = emptyList(),
        private val failure: Throwable? = null
    ) : EvidenceRepository {

        override suspend fun getEvidenceCases():
                List<EvidenceCase> {
            failure?.let { throwable ->
                throw throwable
            }

            return evidenceCases
        }

        override suspend fun getEvidenceCase(
            caseId: String
        ): EvidenceCase? {
            failure?.let { throwable ->
                throw throwable
            }

            return evidenceCases.firstOrNull { evidenceCase ->
                evidenceCase.id == caseId
            }
        }
    }

    private class FakeEvaluationHistoryRepository(
        private val failure: Throwable? = null
    ) : EvaluationHistoryRepository {

        val savedResults = mutableListOf<EvaluationResult>()

        override suspend fun saveEvaluationResult(
            result: EvaluationResult
        ): Long {
            failure?.let { throwable ->
                throw throwable
            }

            savedResults += result

            return savedResults.size.toLong()
        }
    }
}