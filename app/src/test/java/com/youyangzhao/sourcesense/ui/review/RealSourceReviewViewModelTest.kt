package com.youyangzhao.sourcesense.ui.review

import com.youyangzhao.sourcesense.domain.model.AcademicSource
import com.youyangzhao.sourcesense.domain.model.PublicationInformationAssessment
import com.youyangzhao.sourcesense.domain.model.RealSourceReview
import com.youyangzhao.sourcesense.domain.model.SourceCitationDecision
import com.youyangzhao.sourcesense.domain.model.SourceCurrencyAssessment
import com.youyangzhao.sourcesense.domain.model.SourceRelevanceAssessment
import com.youyangzhao.sourcesense.domain.model.SourceReviewDepth
import com.youyangzhao.sourcesense.domain.model.SourceVerificationItem
import com.youyangzhao.sourcesense.domain.repository.SourceReviewRepository
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
class RealSourceReviewViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun startReview_loadsSourceAndUsesSearchTopic() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val viewModel = createViewModel()
            val source = createSource()

            viewModel.startReview(
                source = source,
                searchTopic = "AI in higher education"
            )

            val state = viewModel.uiState.value

            assertEquals(source, state.source)
            assertEquals(
                "AI in higher education",
                state.searchTopic
            )
            assertTrue(state.canEdit)
            assertFalse(state.canSave)
            assertNull(state.savedReviewId)
            assertNull(state.errorMessage)
        }

    @Test
    fun startReview_blankTopic_usesSourceTitle() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val viewModel = createViewModel()
            val source = createSource()

            viewModel.startReview(
                source = source,
                searchTopic = "   "
            )

            assertEquals(
                source.title,
                viewModel.uiState.value.searchTopic
            )
        }

    @Test
    fun saveReview_incompleteForm_showsValidationError() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val repository =
                FakeSourceReviewRepository()

            val viewModel = createViewModel(
                repository = repository
            )

            viewModel.startReview(
                source = createSource(),
                searchTopic = "student wellbeing"
            )

            viewModel.saveReview()
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(
                "Complete every required section and select at least one item to verify.",
                state.errorMessage
            )
            assertFalse(state.isSaving)
            assertNull(state.savedReviewId)
            assertEquals(
                0,
                repository.saveAttemptCount
            )
        }

    @Test
    fun saveReview_completeForm_savesTrimmedReviewAndLocksForm() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val repository =
                FakeSourceReviewRepository(
                    nextReviewId = 42L
                )

            val viewModel = createViewModel(
                repository = repository,
                currentTime =
                    1_786_000_000_000L
            )

            viewModel.startReview(
                source = createSource(),
                searchTopic =
                    "AI in higher education"
            )

            completeForm(viewModel)

            viewModel.updateReflectionNote(
                "  Verify the sample and limitations before citing.  "
            )

            viewModel.saveReview()
            advanceUntilIdle()

            val state =
                viewModel.uiState.value

            val savedReview =
                repository.savedReviews.single()

            assertFalse(state.isSaving)
            assertTrue(state.isSaved)
            assertFalse(state.canEdit)
            assertFalse(state.canSave)
            assertEquals(
                42L,
                state.savedReviewId
            )
            assertNull(state.errorMessage)

            assertEquals(
                "10.1000/source-sense-test",
                savedReview.doi
            )

            assertEquals(
                "A. Researcher, B. Scholar",
                savedReview.authors
            )

            assertEquals(
                "AI in higher education",
                savedReview.searchTopic
            )

            assertEquals(
                SourceRelevanceAssessment
                    .DIRECTLY_RELEVANT,
                savedReview.relevanceAssessment
            )

            assertEquals(
                PublicationInformationAssessment
                    .CLEAR_ENOUGH,
                savedReview
                    .publicationInformationAssessment
            )

            assertEquals(
                SourceCurrencyAssessment
                    .CURRENT_ENOUGH,
                savedReview.currencyAssessment
            )

            assertEquals(
                SourceReviewDepth
                    .ABSTRACT_REVIEWED,
                savedReview.reviewDepth
            )

            assertEquals(
                SourceCitationDecision
                    .NEEDS_FULL_TEXT_REVIEW,
                savedReview.citationDecision
            )

            assertEquals(
                setOf(
                    SourceVerificationItem.SAMPLE,
                    SourceVerificationItem.LIMITATIONS
                ),
                savedReview.verificationItems
            )

            assertEquals(
                "Verify the sample and limitations before citing.",
                savedReview.reflectionNote
            )

            assertEquals(
                1_786_000_000_000L,
                savedReview.reviewedAt
            )

            // Saved reviews must no longer accept edits
            viewModel.selectRelevance(
                SourceRelevanceAssessment.UNCLEAR
            )

            assertEquals(
                SourceRelevanceAssessment
                    .DIRECTLY_RELEVANT,
                viewModel.uiState.value
                    .relevanceAssessment
            )
        }

    @Test
    fun failedSave_canBeRetried() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val repository =
                FakeSourceReviewRepository(
                    shouldFailSaving = true
                )

            val viewModel = createViewModel(
                repository = repository
            )

            viewModel.startReview(
                source = createSource(),
                searchTopic =
                    "academic source evaluation"
            )

            completeForm(viewModel)

            viewModel.saveReview()
            advanceUntilIdle()

            assertEquals(
                "The source review could not be saved. Please try again.",
                viewModel.uiState.value
                    .errorMessage
            )

            assertNull(
                viewModel.uiState.value
                    .savedReviewId
            )

            assertEquals(
                1,
                repository.saveAttemptCount
            )

            repository.shouldFailSaving = false

            viewModel.retrySaving()
            advanceUntilIdle()

            assertEquals(
                2,
                repository.saveAttemptCount
            )

            assertEquals(
                1,
                repository.savedReviews.size
            )

            assertTrue(
                viewModel.uiState.value.isSaved
            )

            assertNull(
                viewModel.uiState.value
                    .errorMessage
            )
        }

    @Test
    fun startReview_newSource_clearsPreviousForm() =
        runTest(
            context = mainDispatcherRule.testDispatcher
        ) {
            val viewModel = createViewModel()

            viewModel.startReview(
                source = createSource(),
                searchTopic = "first topic"
            )

            completeForm(viewModel)

            viewModel.updateReflectionNote(
                "Previous reflection"
            )

            val newSource = createSource(
                doi = "10.1000/new-source",
                title =
                    "A Different Academic Source"
            )

            viewModel.startReview(
                source = newSource,
                searchTopic = "second topic"
            )

            val state =
                viewModel.uiState.value

            assertEquals(
                newSource,
                state.source
            )

            assertEquals(
                "second topic",
                state.searchTopic
            )

            assertNull(
                state.relevanceAssessment
            )

            assertNull(
                state
                    .publicationInformationAssessment
            )

            assertNull(
                state.currencyAssessment
            )

            assertNull(
                state.reviewDepth
            )

            assertNull(
                state.citationDecision
            )

            assertTrue(
                state.verificationItems.isEmpty()
            )

            assertTrue(
                state.reflectionNote.isEmpty()
            )

            assertNull(
                state.savedReviewId
            )

            assertNull(
                state.errorMessage
            )

            assertFalse(
                state.canSave
            )
        }

    private fun createViewModel(
        repository: SourceReviewRepository =
            FakeSourceReviewRepository(),
        currentTime: Long =
            1_786_000_000_000L
    ): RealSourceReviewViewModel {
        return RealSourceReviewViewModel(
            sourceReviewRepository =
                repository,
            currentTimeProvider = {
                currentTime
            }
        )
    }

    private fun completeForm(
        viewModel: RealSourceReviewViewModel
    ) {
        viewModel.selectRelevance(
            SourceRelevanceAssessment
                .DIRECTLY_RELEVANT
        )

        viewModel.selectPublicationInformation(
            PublicationInformationAssessment
                .CLEAR_ENOUGH
        )

        viewModel.selectCurrency(
            SourceCurrencyAssessment
                .CURRENT_ENOUGH
        )

        viewModel.selectReviewDepth(
            SourceReviewDepth
                .ABSTRACT_REVIEWED
        )

        viewModel.selectCitationDecision(
            SourceCitationDecision
                .NEEDS_FULL_TEXT_REVIEW
        )

        viewModel.toggleVerificationItem(
            SourceVerificationItem.SAMPLE
        )

        viewModel.toggleVerificationItem(
            SourceVerificationItem.LIMITATIONS
        )
    }

    private fun createSource(
        doi: String =
            "10.1000/source-sense-test",
        title: String =
            "Responsible AI in University Learning"
    ): AcademicSource {
        return AcademicSource(
            doi = doi,
            title = title,
            authors = listOf(
                "A. Researcher",
                "B. Scholar"
            ),
            publicationYear = 2026,
            publicationName =
                "Journal of Digital Education",
            publisher =
                "Example Academic Press",
            sourceType =
                "journal-article",
            url =
                "https://doi.org/$doi",
            abstractText =
                "This study examines responsible AI use in university learning."
        )
    }

    private class FakeSourceReviewRepository(
        var shouldFailSaving: Boolean = false,
        private val nextReviewId: Long = 1L
    ) : SourceReviewRepository {

        private val reviewsFlow =
            MutableStateFlow<
                    List<RealSourceReview>
                    >(
                emptyList()
            )

        val savedReviews =
            mutableListOf<RealSourceReview>()

        var saveAttemptCount: Int = 0
            private set

        override fun observeSourceReviews():
                Flow<List<RealSourceReview>> {
            return reviewsFlow
        }

        override suspend fun saveSourceReview(
            review: RealSourceReview
        ): Long {
            saveAttemptCount += 1

            if (shouldFailSaving) {
                throw IllegalStateException(
                    "Database failure"
                )
            }

            savedReviews += review

            reviewsFlow.value =
                savedReviews.toList()

            return nextReviewId
        }

        override suspend fun getSourceReview(
            reviewId: Long
        ): RealSourceReview? {
            return savedReviews.getOrNull(
                index =
                    reviewId.toInt() - 1
            )
        }
    }
}