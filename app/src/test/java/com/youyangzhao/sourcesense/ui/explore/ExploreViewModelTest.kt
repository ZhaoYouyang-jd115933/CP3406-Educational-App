package com.youyangzhao.sourcesense.ui.explore

import com.youyangzhao.sourcesense.domain.model.AcademicSource
import com.youyangzhao.sourcesense.domain.model.PublicationInformationAssessment
import com.youyangzhao.sourcesense.domain.model.RealSourceReview
import com.youyangzhao.sourcesense.domain.model.SourceCitationDecision
import com.youyangzhao.sourcesense.domain.model.SourceCurrencyAssessment
import com.youyangzhao.sourcesense.domain.model.SourceRelevanceAssessment
import com.youyangzhao.sourcesense.domain.model.SourceReviewDepth
import com.youyangzhao.sourcesense.domain.model.SourceVerificationItem
import com.youyangzhao.sourcesense.domain.repository.AcademicSourceRepository
import com.youyangzhao.sourcesense.domain.repository.SourceReviewRepository
import com.youyangzhao.sourcesense.testing.MainDispatcherRule
import java.io.IOException
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
class ExploreViewModelTest {

    @get:Rule
    val mainDispatcherRule =
        MainDispatcherRule()

    @Test
    fun searchSources_blankQuery_showsValidationError() =
        runTest(
            context =
                mainDispatcherRule.testDispatcher
        ) {
            val academicRepository =
                FakeAcademicSourceRepository()

            val viewModel = createViewModel(
                academicRepository =
                    academicRepository
            )

            viewModel.updateQuery("   ")
            viewModel.searchSources()

            val state = viewModel.uiState.value

            assertEquals(
                "Enter a topic, title or DOI to search.",
                state.errorMessage
            )

            assertFalse(state.isLoading)
            assertFalse(state.hasSearched)
            assertTrue(state.sources.isEmpty())
            assertEquals(
                0,
                academicRepository.searchCalls
            )
        }

    @Test
    fun searchSources_success_loadsAcademicSources() =
        runTest(
            context =
                mainDispatcherRule.testDispatcher
        ) {
            val expectedSources =
                listOf(
                    createAcademicSource()
                )

            val academicRepository =
                FakeAcademicSourceRepository(
                    searchResults =
                        expectedSources
                )

            val viewModel = createViewModel(
                academicRepository =
                    academicRepository
            )

            viewModel.updateQuery(
                "  artificial intelligence  "
            )

            viewModel.searchSources()
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(
                "artificial intelligence",
                academicRepository.lastQuery
            )

            assertEquals(
                10,
                academicRepository.lastLimit
            )

            assertEquals(
                expectedSources,
                state.sources
            )

            assertFalse(state.isLoading)
            assertTrue(state.hasSearched)
            assertTrue(state.hasResults)
            assertNull(state.errorMessage)
        }

    @Test
    fun searchSources_ioFailure_showsConnectionError() =
        runTest(
            context =
                mainDispatcherRule.testDispatcher
        ) {
            val academicRepository =
                FakeAcademicSourceRepository(
                    searchException =
                        IOException(
                            "No connection"
                        )
                )

            val viewModel = createViewModel(
                academicRepository =
                    academicRepository
            )

            viewModel.updateQuery(
                "digital literacy"
            )

            viewModel.searchSources()
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertTrue(state.hasSearched)
            assertTrue(state.sources.isEmpty())

            assertEquals(
                "Unable to connect. Check your internet connection and try again.",
                state.errorMessage
            )
        }

    @Test
    fun retrySearch_reusesLastSubmittedQuery() =
        runTest(
            context =
                mainDispatcherRule.testDispatcher
        ) {
            val expectedSource =
                createAcademicSource()

            val academicRepository =
                FakeAcademicSourceRepository(
                    searchException =
                        IOException(
                            "Temporary failure"
                        )
                )

            val viewModel = createViewModel(
                academicRepository =
                    academicRepository
            )

            viewModel.updateQuery(
                "source evaluation"
            )

            viewModel.searchSources()
            advanceUntilIdle()

            assertEquals(
                1,
                academicRepository.searchCalls
            )

            assertTrue(
                viewModel.uiState.value
                    .errorMessage != null
            )

            academicRepository.searchException =
                null

            academicRepository.searchResults =
                listOf(expectedSource)

            viewModel.retrySearch()
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(
                2,
                academicRepository.searchCalls
            )

            assertEquals(
                "source evaluation",
                academicRepository.lastQuery
            )

            assertEquals(
                listOf(expectedSource),
                state.sources
            )

            assertNull(state.errorMessage)
            assertTrue(state.hasSearched)
        }

    @Test
    fun savedReviews_areObservedWhenViewModelStarts() =
        runTest(
            context =
                mainDispatcherRule.testDispatcher
        ) {
            val savedReview =
                createSavedReview()

            val reviewRepository =
                FakeSourceReviewRepository(
                    initialReviews =
                        listOf(savedReview)
                )

            val viewModel = createViewModel(
                reviewRepository =
                    reviewRepository
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(
                listOf(savedReview),
                state.savedReviews
            )

            assertTrue(state.hasSavedReviews)
            assertNull(
                state.reviewHistoryErrorMessage
            )
        }

    @Test
    fun clearSearch_resetsSearchButPreservesSavedReviews() =
        runTest(
            context =
                mainDispatcherRule.testDispatcher
        ) {
            val savedReview =
                createSavedReview()

            val reviewRepository =
                FakeSourceReviewRepository(
                    initialReviews =
                        listOf(savedReview)
                )

            val academicRepository =
                FakeAcademicSourceRepository(
                    searchResults =
                        listOf(
                            createAcademicSource()
                        )
                )

            val viewModel = createViewModel(
                academicRepository =
                    academicRepository,
                reviewRepository =
                    reviewRepository
            )

            advanceUntilIdle()

            viewModel.updateQuery(
                "academic integrity"
            )

            viewModel.searchSources()
            advanceUntilIdle()

            viewModel.clearSearch()

            val state = viewModel.uiState.value

            assertEquals("", state.query)
            assertFalse(state.isLoading)
            assertFalse(state.hasSearched)
            assertTrue(state.sources.isEmpty())
            assertNull(state.errorMessage)

            assertEquals(
                listOf(savedReview),
                state.savedReviews
            )
        }

    private fun createViewModel(
        academicRepository:
        AcademicSourceRepository =
            FakeAcademicSourceRepository(),
        reviewRepository:
        SourceReviewRepository =
            FakeSourceReviewRepository()
    ): ExploreViewModel {
        return ExploreViewModel(
            academicSourceRepository =
                academicRepository,
            sourceReviewRepository =
                reviewRepository
        )
    }

    private fun createAcademicSource():
            AcademicSource {
        return AcademicSource(
            doi =
                "10.1000/source-sense-test",
            title =
                "Evaluating Academic Sources",
            authors =
                listOf(
                    "A. Researcher",
                    "B. Scholar"
                ),
            publicationYear = 2025,
            publicationName =
                "Journal of Information Literacy",
            publisher =
                "Academic Publisher",
            sourceType =
                "journal-article",
            url =
                "https://doi.org/10.1000/source-sense-test",
            abstractText =
                "This study examines academic source evaluation.",
            subjects =
                listOf(
                    "Information literacy"
                ),
            fullTextUrl =
                "https://example.com/full-text.pdf",
            licenseUrl =
                "https://creativecommons.org/licenses/by/4.0/"
        )
    }

    private fun createSavedReview():
            RealSourceReview {
        return RealSourceReview(
            id = 1L,
            doi =
                "10.1000/source-sense-test",
            title =
                "Evaluating Academic Sources",
            authors =
                "A. Researcher, B. Scholar",
            publicationYear = 2025,
            publicationName =
                "Journal of Information Literacy",
            publisher =
                "Academic Publisher",
            sourceType =
                "journal-article",
            searchTopic =
                "academic source evaluation",
            relevanceAssessment =
                SourceRelevanceAssessment
                    .DIRECTLY_RELEVANT,
            publicationInformationAssessment =
                PublicationInformationAssessment
                    .CLEAR_ENOUGH,
            currencyAssessment =
                SourceCurrencyAssessment
                    .CURRENT_ENOUGH,
            reviewDepth =
                SourceReviewDepth
                    .ABSTRACT_REVIEWED,
            citationDecision =
                SourceCitationDecision
                    .NEEDS_FULL_TEXT_REVIEW,
            verificationItems =
                setOf(
                    SourceVerificationItem
                        .RESEARCH_METHOD,
                    SourceVerificationItem
                        .LIMITATIONS
                ),
            reflectionNote =
                "Review the method before citing.",
            reviewedAt =
                1_786_000_000_000L
        )
    }
}

private class FakeAcademicSourceRepository(
    var searchResults:
    List<AcademicSource> = emptyList(),
    var searchException:
    Exception? = null
) : AcademicSourceRepository {

    var searchCalls: Int = 0
        private set

    var lastQuery: String? = null
        private set

    var lastLimit: Int? = null
        private set

    override suspend fun searchSources(
        query: String,
        limit: Int
    ): List<AcademicSource> {
        searchCalls += 1
        lastQuery = query
        lastLimit = limit

        searchException?.let { exception ->
            throw exception
        }

        return searchResults
    }
}

private class FakeSourceReviewRepository(
    initialReviews:
    List<RealSourceReview> = emptyList()
) : SourceReviewRepository {

    private val reviewsFlow =
        MutableStateFlow(
            initialReviews
        )

    override fun observeSourceReviews():
            Flow<List<RealSourceReview>> {
        return reviewsFlow
    }

    override suspend fun saveSourceReview(
        review: RealSourceReview
    ): Long {
        val reviewId =
            if (review.id == 0L) {
                reviewsFlow.value.size + 1L
            } else {
                review.id
            }

        reviewsFlow.value =
            reviewsFlow.value +
                    review.copy(
                        id = reviewId
                    )

        return reviewId
    }

    override suspend fun getSourceReview(
        reviewId: Long
    ): RealSourceReview? {
        return reviewsFlow.value
            .firstOrNull { review ->
                review.id == reviewId
            }
    }
}

