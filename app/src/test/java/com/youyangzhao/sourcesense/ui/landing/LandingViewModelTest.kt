package com.youyangzhao.sourcesense.ui.landing

import com.youyangzhao.sourcesense.domain.model.AnswerOption
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.EvaluationAttemptSummary
import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvaluationResult
import com.youyangzhao.sourcesense.domain.model.EvidenceCase
import com.youyangzhao.sourcesense.domain.model.LearningModule
import com.youyangzhao.sourcesense.domain.model.SourceType
import com.youyangzhao.sourcesense.domain.model.UserSettings
import com.youyangzhao.sourcesense.domain.repository.EvaluationHistoryRepository
import com.youyangzhao.sourcesense.domain.repository.LearningModuleRepository
import com.youyangzhao.sourcesense.domain.repository.UserSettingsRepository
import com.youyangzhao.sourcesense.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LandingViewModelTest {

    @get:Rule
    val mainDispatcherRule =
        MainDispatcherRule()

    @Test
    fun noHistory_loadsModulesAsNotStarted() =
        runTest(
            context =
                mainDispatcherRule
                    .testDispatcher
        ) {
            val viewModel =
                createViewModel()

            advanceUntilIdle()

            val state =
                viewModel.uiState.value

            assertFalse(state.isLoading)

            assertEquals(
                DifficultyLevel.BEGINNER,
                state.difficultyLevel
            )

            assertEquals(
                2,
                state.totalModuleCount
            )

            assertEquals(
                0,
                state.completedModuleCount
            )

            assertTrue(
                state.modules.all { module ->
                    !module.isCompleted
                }
            )
        }

    @Test
    fun repeatedAttempts_useBestScoreAndOneCompletedModule() =
        runTest(
            context =
                mainDispatcherRule
                    .testDispatcher
        ) {
            val historyRepository =
                FakeEvaluationHistoryRepository(
                    initialAttempts = listOf(
                        createAttempt(
                            id = 1,
                            evidenceCaseId =
                                BEGINNER_CASE_1,
                            score = 2,
                            percentage = 50,
                            completedAt = 100
                        ),
                        createAttempt(
                            id = 2,
                            evidenceCaseId =
                                BEGINNER_CASE_1,
                            score = 4,
                            percentage = 100,
                            completedAt = 200
                        )
                    )
                )

            val viewModel =
                createViewModel(
                    historyRepository =
                        historyRepository
                )

            advanceUntilIdle()

            val state =
                viewModel.uiState.value

            val module =
                state.modules.first {
                        moduleUiModel ->
                    moduleUiModel.module.id ==
                            BEGINNER_MODULE_1
                }

            assertEquals(
                1,
                state.completedModuleCount
            )

            assertEquals(
                2,
                module.attemptCount
            )

            assertEquals(
                4,
                module.bestScore
            )

            assertEquals(
                4,
                module.bestTotalQuestions
            )

            assertEquals(
                100,
                module.bestPercentage
            )

            assertEquals(
                0.5f,
                state.overallProgress
            )
        }

    @Test
    fun oldAttemptWithDifferentQuestionCount_isIgnored() =
        runTest(
            context =
                mainDispatcherRule
                    .testDispatcher
        ) {
            val historyRepository =
                FakeEvaluationHistoryRepository(
                    initialAttempts = listOf(
                        EvaluationAttemptSummary(
                            attemptId = 1,
                            evidenceCaseId =
                                BEGINNER_CASE_1,
                            score = 1,
                            totalQuestions = 1,
                            percentage = 100,
                            completedAt = 100
                        )
                    )
                )

            val viewModel =
                createViewModel(
                    historyRepository =
                        historyRepository
                )

            advanceUntilIdle()

            val module =
                viewModel.uiState.value
                    .modules
                    .first()

            assertFalse(
                module.isCompleted
            )

            assertEquals(
                0,
                module.attemptCount
            )

            assertNull(
                module.bestScore
            )
        }

    @Test
    fun difficultyChange_loadsMatchingModules() =
        runTest(
            context =
                mainDispatcherRule
                    .testDispatcher
        ) {
            val settingsRepository =
                FakeUserSettingsRepository()

            val viewModel =
                createViewModel(
                    settingsRepository =
                        settingsRepository
                )

            advanceUntilIdle()

            settingsRepository
                .updateDifficultyLevel(
                    difficultyLevel =
                        DifficultyLevel
                            .INTERMEDIATE
                )

            advanceUntilIdle()

            val state =
                viewModel.uiState.value

            assertEquals(
                DifficultyLevel.INTERMEDIATE,
                state.difficultyLevel
            )

            assertEquals(
                1,
                state.totalModuleCount
            )

            assertEquals(
                INTERMEDIATE_MODULE,
                state.modules
                    .single()
                    .module
                    .id
            )
        }

    @Test
    fun clearingHistory_resetsProgress() =
        runTest(
            context =
                mainDispatcherRule
                    .testDispatcher
        ) {
            val historyRepository =
                FakeEvaluationHistoryRepository(
                    initialAttempts = listOf(
                        createAttempt(
                            id = 1,
                            evidenceCaseId =
                                BEGINNER_CASE_1,
                            score = 4,
                            percentage = 100,
                            completedAt = 100
                        )
                    )
                )

            val viewModel =
                createViewModel(
                    historyRepository =
                        historyRepository
                )

            advanceUntilIdle()

            assertEquals(
                1,
                viewModel.uiState.value
                    .completedModuleCount
            )

            historyRepository.setAttempts(
                attempts = emptyList()
            )

            advanceUntilIdle()

            val state =
                viewModel.uiState.value

            assertEquals(
                0,
                state.completedModuleCount
            )

            assertEquals(
                0f,
                state.overallProgress
            )

            assertTrue(
                state.modules.all { module ->
                    !module.isCompleted
                }
            )
        }

    private fun createViewModel(
        settingsRepository:
        FakeUserSettingsRepository =
            FakeUserSettingsRepository(),
        historyRepository:
        FakeEvaluationHistoryRepository =
            FakeEvaluationHistoryRepository()
    ): LandingViewModel {
        return LandingViewModel(
            learningModuleRepository =
                FakeLearningModuleRepository(
                    modules =
                        createModules()
                ),
            userSettingsRepository =
                settingsRepository,
            evaluationHistoryRepository =
                historyRepository
        )
    }

    private fun createModules():
            List<LearningModule> {
        return listOf(
            createModule(
                moduleId =
                    BEGINNER_MODULE_1,
                evidenceCaseId =
                    BEGINNER_CASE_1,
                difficultyLevel =
                    DifficultyLevel.BEGINNER
            ),
            createModule(
                moduleId =
                    BEGINNER_MODULE_2,
                evidenceCaseId =
                    BEGINNER_CASE_2,
                difficultyLevel =
                    DifficultyLevel.BEGINNER
            ),
            createModule(
                moduleId =
                    INTERMEDIATE_MODULE,
                evidenceCaseId =
                    INTERMEDIATE_CASE,
                difficultyLevel =
                    DifficultyLevel.INTERMEDIATE
            )
        )
    }

    private fun createModule(
        moduleId: String,
        evidenceCaseId: String,
        difficultyLevel: DifficultyLevel
    ): LearningModule {
        val questions =
            (1..4).map { index ->
                EvaluationQuestion(
                    id =
                        "${moduleId}_question_$index",
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
                                "Correct"
                        ),
                        AnswerOption(
                            id =
                                "wrong_$index",
                            text =
                                "Wrong"
                        )
                    ),
                    correctOptionId =
                        "correct_$index",
                    explanation =
                        "Explanation $index",
                    learningTip =
                        "Tip $index"
                )
            }

        return LearningModule(
            id = moduleId,
            difficultyLevel =
                difficultyLevel,
            title =
                "Module $moduleId",
            description =
                "Test description",
            learningFocus =
                "Test focus",
            evidenceCase =
                EvidenceCase(
                    id = evidenceCaseId,
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
                        questions
                )
        )
    }

    private fun createAttempt(
        id: Long,
        evidenceCaseId: String,
        score: Int,
        percentage: Int,
        completedAt: Long
    ): EvaluationAttemptSummary {
        return EvaluationAttemptSummary(
            attemptId = id,
            evidenceCaseId =
                evidenceCaseId,
            score = score,
            totalQuestions = 4,
            percentage = percentage,
            completedAt = completedAt
        )
    }

    private class FakeLearningModuleRepository(
        private val modules:
        List<LearningModule>
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

    private class FakeUserSettingsRepository :
        UserSettingsRepository {

        private val settingsFlow =
            MutableStateFlow(
                UserSettings(
                    difficultyLevel =
                        DifficultyLevel
                            .BEGINNER
                )
            )

        override fun observeUserSettings():
                Flow<UserSettings> {
            return settingsFlow
        }

        override suspend fun
                updateDifficultyLevel(
            difficultyLevel:
            DifficultyLevel
        ) {
            settingsFlow.value =
                settingsFlow.value.copy(
                    difficultyLevel =
                        difficultyLevel
                )
        }

        override suspend fun
                updateUseLargerText(
            enabled: Boolean
        ) {
            settingsFlow.value =
                settingsFlow.value.copy(
                    useLargerText = enabled
                )
        }

        override suspend fun
                updateReduceAnimations(
            enabled: Boolean
        ) {
            settingsFlow.value =
                settingsFlow.value.copy(
                    reduceAnimations =
                        enabled
                )
        }

        override suspend fun
                updateSoundFeedback(
            enabled: Boolean
        ) {
            settingsFlow.value =
                settingsFlow.value.copy(
                    soundFeedbackEnabled =
                        enabled
                )
        }

        override suspend fun
                resetUserSettings() {
            settingsFlow.value =
                UserSettings()
        }
    }

    private class FakeEvaluationHistoryRepository(
        initialAttempts:
        List<EvaluationAttemptSummary> =
            emptyList()
    ) : EvaluationHistoryRepository {

        private val attemptsFlow =
            MutableStateFlow(
                initialAttempts
            )

        override fun
                observeEvaluationAttempts():
                Flow<List<EvaluationAttemptSummary>> {
            return attemptsFlow
        }

        override suspend fun
                saveEvaluationResult(
            result: EvaluationResult
        ): Long {
            return 1L
        }

        fun setAttempts(
            attempts:
            List<EvaluationAttemptSummary>
        ) {
            attemptsFlow.value =
                attempts
        }
    }

    private companion object {
        const val BEGINNER_MODULE_1 =
            "beginner_module_1"

        const val BEGINNER_MODULE_2 =
            "beginner_module_2"

        const val INTERMEDIATE_MODULE =
            "intermediate_module"

        const val BEGINNER_CASE_1 =
            "beginner_case_1"

        const val BEGINNER_CASE_2 =
            "beginner_case_2"

        const val INTERMEDIATE_CASE =
            "intermediate_case"
    }
}

