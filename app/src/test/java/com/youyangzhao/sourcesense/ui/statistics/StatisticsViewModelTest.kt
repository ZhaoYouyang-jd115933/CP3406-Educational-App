package com.youyangzhao.sourcesense.ui.statistics

import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.LearningStatistics
import com.youyangzhao.sourcesense.domain.model.RecentAttempt
import com.youyangzhao.sourcesense.domain.model.SkillAccuracy
import com.youyangzhao.sourcesense.domain.repository.StatisticsRepository
import com.youyangzhao.sourcesense.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun initialization_repositoryReturnsStatistics_updatesUiState() = runTest {
        val expectedStatistics = createTestStatistics()
        val repository = FakeStatisticsRepository(
            initialStatistics = expectedStatistics
        )

        val viewModel = StatisticsViewModel(
            statisticsRepository = repository
        )

        // Complete the statistics collection coroutine
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(expectedStatistics, state.statistics)
        assertTrue(state.hasData)
        assertEquals(2, state.statistics.completedEvaluations)
        assertEquals(42, state.statistics.averagePercentage)
        assertEquals(50, state.statistics.bestPercentage)
    }

    @Test
    fun requestClearHistory_whenDataExists_showsConfirmation() = runTest {
        val viewModel = createLoadedViewModel()

        viewModel.requestClearHistory()

        val state = viewModel.uiState.value

        assertTrue(state.showClearConfirmation)
        assertFalse(state.isClearingHistory)
    }

    @Test
    fun dismissClearConfirmation_hidesConfirmation() = runTest {
        val viewModel = createLoadedViewModel()

        viewModel.requestClearHistory()
        viewModel.dismissClearConfirmation()

        assertFalse(
            viewModel.uiState.value.showClearConfirmation
        )
    }

    @Test
    fun confirmClearHistory_callsRepositoryAndClearsStatistics() = runTest {
        val repository = FakeStatisticsRepository(
            initialStatistics = createTestStatistics()
        )

        val viewModel = StatisticsViewModel(
            statisticsRepository = repository
        )

        advanceUntilIdle()

        viewModel.requestClearHistory()
        viewModel.confirmClearHistory()

        // Complete the clear operation and Room-style update
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(1, repository.clearCallCount)
        assertFalse(state.isClearingHistory)
        assertFalse(state.showClearConfirmation)
        assertFalse(state.hasData)
        assertEquals(0, state.statistics.completedEvaluations)
    }

    @Test
    fun confirmClearHistory_whenRepositoryFails_displaysError() = runTest {
        val repository = FakeStatisticsRepository(
            initialStatistics = createTestStatistics(),
            clearFailure = IllegalStateException(
                "Database clear failure"
            )
        )

        val viewModel = StatisticsViewModel(
            statisticsRepository = repository
        )

        advanceUntilIdle()

        viewModel.requestClearHistory()
        viewModel.confirmClearHistory()

        // Complete the failing clear operation
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(1, repository.clearCallCount)
        assertFalse(state.isClearingHistory)
        assertTrue(state.hasData)
        assertEquals(
            "Learning history could not be cleared.",
            state.errorMessage
        )
    }

    @Test
    fun initialization_repositoryObservationFails_displaysError() = runTest {
        val repository = FakeStatisticsRepository(
            observeFailure = IllegalStateException(
                "Database observation failure"
            )
        )

        val viewModel = StatisticsViewModel(
            statisticsRepository = repository
        )

        // Complete the failing collection coroutine
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertFalse(state.hasData)
        assertEquals(
            "Learning statistics could not be loaded.",
            state.errorMessage
        )
    }

    @Test
    fun clearError_removesCurrentErrorMessage() = runTest {
        val repository = FakeStatisticsRepository(
            initialStatistics = createTestStatistics(),
            clearFailure = IllegalStateException(
                "Database clear failure"
            )
        )

        val viewModel = StatisticsViewModel(
            statisticsRepository = repository
        )

        advanceUntilIdle()

        viewModel.requestClearHistory()
        viewModel.confirmClearHistory()
        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value.errorMessage != null
        )

        viewModel.clearError()

        assertEquals(
            null,
            viewModel.uiState.value.errorMessage
        )
    }

    private suspend fun kotlinx.coroutines.test.TestScope
            .createLoadedViewModel(): StatisticsViewModel {
        val repository = FakeStatisticsRepository(
            initialStatistics = createTestStatistics()
        )

        val viewModel = StatisticsViewModel(
            statisticsRepository = repository
        )

        // Complete the initial statistics collection
        advanceUntilIdle()

        return viewModel
    }

    private fun createTestStatistics(): LearningStatistics {
        return LearningStatistics(
            completedEvaluations = 2,
            averagePercentage = 42,
            bestPercentage = 50,
            recentAttempts = listOf(
                RecentAttempt(
                    id = 2,
                    evidenceCaseId = "social_media_depression",
                    score = 2,
                    totalQuestions = 6,
                    percentage = 33,
                    completedAt = 1_786_000_000_000
                ),
                RecentAttempt(
                    id = 1,
                    evidenceCaseId = "social_media_depression",
                    score = 3,
                    totalQuestions = 6,
                    percentage = 50,
                    completedAt = 1_785_000_000_000
                )
            ),
            skillAccuracies = listOf(
                SkillAccuracy(
                    dimension = EvaluationDimension.RELEVANCE,
                    correctAnswers = 1,
                    totalAnswers = 2
                ),
                SkillAccuracy(
                    dimension = EvaluationDimension.SOURCE_TYPE,
                    correctAnswers = 2,
                    totalAnswers = 2
                )
            )
        )
    }

    private class FakeStatisticsRepository(
        initialStatistics: LearningStatistics =
            LearningStatistics(),
        private val observeFailure: Throwable? = null,
        private val clearFailure: Throwable? = null
    ) : StatisticsRepository {

        private val statisticsFlow = MutableStateFlow(
            initialStatistics
        )

        var clearCallCount: Int = 0
            private set

        override fun observeLearningStatistics():
                Flow<LearningStatistics> {
            return if (observeFailure == null) {
                statisticsFlow
            } else {
                flow {
                    throw observeFailure
                }
            }
        }

        override suspend fun clearLearningHistory() {
            clearCallCount += 1

            clearFailure?.let { throwable ->
                throw throwable
            }

            // Simulate Room emitting an empty database state
            statisticsFlow.value = LearningStatistics()
        }
    }
}

