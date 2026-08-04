package com.youyangzhao.sourcesense.ui.evaluation

import com.youyangzhao.sourcesense.domain.model.AnswerOption
import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvidenceCase
import com.youyangzhao.sourcesense.domain.model.SourceType
import com.youyangzhao.sourcesense.domain.repository.EvidenceRepository
import com.youyangzhao.sourcesense.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
            evidenceRepository = repository
        )

        // Complete the loading coroutine
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals("test_case", state.evidenceCase?.id)
        assertEquals(0, state.currentQuestionIndex)
        assertNull(state.errorMessage)
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
    fun finishLastQuestion_generatesEvaluationResult() = runTest {
        val viewModel = createLoadedViewModel()

        viewModel.selectAnswer("correct_1")
        viewModel.moveToNextQuestion()

        viewModel.selectAnswer("wrong_2")
        viewModel.moveToNextQuestion()

        val result = viewModel.uiState.value.result

        assertNotNull(result)
        assertEquals(1, result?.score)
        assertEquals(2, result?.totalQuestions)
        assertEquals(50, result?.percentage)
    }

    @Test
    fun restartEvaluation_clearsAnswersResultAndProgress() = runTest {
        val viewModel = createLoadedViewModel()

        viewModel.selectAnswer("correct_1")
        viewModel.moveToNextQuestion()
        viewModel.selectAnswer("correct_2")
        viewModel.moveToNextQuestion()

        assertNotNull(viewModel.uiState.value.result)

        viewModel.restartEvaluation()

        val state = viewModel.uiState.value

        assertEquals(0, state.currentQuestionIndex)
        assertTrue(state.selectedAnswers.isEmpty())
        assertNull(state.result)
        assertFalse(state.isLoading)
        assertNotNull(state.evidenceCase)
    }

    @Test
    fun initialization_repositoryFails_displaysErrorState() = runTest {
        val repository = FakeEvidenceRepository(
            failure = IllegalStateException("Test failure")
        )

        val viewModel = EvaluationViewModel(
            evidenceRepository = repository
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

    private suspend fun kotlinx.coroutines.test.TestScope
            .createLoadedViewModel(): EvaluationViewModel {
        val repository = FakeEvidenceRepository(
            evidenceCases = listOf(createTestEvidenceCase())
        )

        val viewModel = EvaluationViewModel(
            evidenceRepository = repository
        )

        // Finish repository loading before interacting with the state
        advanceUntilIdle()

        return viewModel
    }

    private fun createTestEvidenceCase(): EvidenceCase {
        return EvidenceCase(
            id = "test_case",
            researchQuestion = "Does social media use cause depression?",
            title = "Social Media Use and Student Wellbeing",
            authors = "Test Author",
            publication = "Test Journal",
            publishedYear = 2026,
            excerpt = "The study reported an association.",
            methodSummary = "Cross-sectional survey",
            sampleSummary = "100 university students",
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = "Test evidence case",
            questions = listOf(
                createQuestion(
                    id = "question_1",
                    dimension = EvaluationDimension.RELEVANCE,
                    correctOptionId = "correct_1",
                    incorrectOptionId = "wrong_1"
                ),
                createQuestion(
                    id = "question_2",
                    dimension = EvaluationDimension.CAUSATION,
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
        private val evidenceCases: List<EvidenceCase> = emptyList(),
        private val failure: Throwable? = null
    ) : EvidenceRepository {

        override suspend fun getEvidenceCases(): List<EvidenceCase> {
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
}