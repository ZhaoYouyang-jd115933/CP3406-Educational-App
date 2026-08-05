package com.youyangzhao.sourcesense.ui.review

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.youyangzhao.sourcesense.domain.model.AcademicSource
import com.youyangzhao.sourcesense.domain.model.PublicationInformationAssessment
import com.youyangzhao.sourcesense.domain.model.SourceCitationDecision
import com.youyangzhao.sourcesense.domain.model.SourceCurrencyAssessment
import com.youyangzhao.sourcesense.domain.model.SourceRelevanceAssessment
import com.youyangzhao.sourcesense.domain.model.SourceReviewDepth
import com.youyangzhao.sourcesense.domain.model.SourceVerificationItem
import com.youyangzhao.sourcesense.ui.theme.SourceSenseTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RealSourceReviewScreenTest {

    @get:Rule
    val composeTestRule =
        createComposeRule()

    @Test
    fun missingSource_showsReturnAction() {
        var backClickCount = 0

        composeTestRule.setContent {
            SourceSenseTheme(
                dynamicColor = false
            ) {
                RealSourceReviewScreen(
                    uiState =
                        RealSourceReviewUiState(),
                    onBackToExplore = {
                        backClickCount += 1
                    },
                    onOpenPaperPage = {},
                    onRelevanceSelected = {},
                    onPublicationInformationSelected = {},
                    onCurrencySelected = {},
                    onReviewDepthSelected = {},
                    onCitationDecisionSelected = {},
                    onVerificationItemToggled = {},
                    onReflectionNoteChange = {},
                    onSaveReview = {},
                    onRetrySaving = {}
                )
            }
        }

        composeTestRule
            .onNode(
                hasText(
                    "No Source Selected"
                )
            )
            .assertExists()

        composeTestRule
            .onNode(
                hasText(
                    "Back to Explore"
                )
            )
            .assertIsEnabled()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                1,
                backClickCount
            )
        }
    }

    @Test
    fun incompleteReview_showsSourceAndDisablesSave() {
        var openedSource:
                AcademicSource? = null

        val source =
            createSource()

        composeTestRule.setContent {
            SourceSenseTheme(
                dynamicColor = false
            ) {
                RealSourceReviewScreen(
                    uiState =
                        RealSourceReviewUiState(
                            source = source,
                            searchTopic =
                                "responsible AI education"
                        ),
                    onBackToExplore = {},
                    onOpenPaperPage = {
                            selectedSource ->

                        openedSource =
                            selectedSource
                    },
                    onRelevanceSelected = {},
                    onPublicationInformationSelected = {},
                    onCurrencySelected = {},
                    onReviewDepthSelected = {},
                    onCitationDecisionSelected = {},
                    onVerificationItemToggled = {},
                    onReflectionNoteChange = {},
                    onSaveReview = {},
                    onRetrySaving = {}
                )
            }
        }

        composeTestRule
            .onNode(
                hasText(
                    "Review a Real Source"
                )
            )
            .assertExists()

        composeTestRule
            .onNode(
                hasText(
                    source.title
                )
            )
            .assertExists()

        val scrollableColumn =
            composeTestRule.onNode(
                hasScrollAction()
            )

        // Scroll until the reading action is composed
        scrollableColumn
            .performScrollToNode(
                hasText(
                    "Read Source"
                )
            )

        composeTestRule
            .onNode(
                hasText(
                    "Read Source"
                )
            )
            .assertIsEnabled()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                source,
                openedSource
            )
        }

        // Confirm that the metadata limitation is visible
        scrollableColumn
            .performScrollToNode(
                hasText(
                    "Important Evidence Limit"
                )
            )

        composeTestRule
            .onNode(
                hasText(
                    "Important Evidence Limit"
                )
            )
            .assertExists()

        scrollableColumn
            .performScrollToNode(
                hasText(
                    text =
                        "Crossref metadata cannot confirm",
                    substring = true
                )
            )

        composeTestRule
            .onNode(
                hasText(
                    text =
                        "Crossref metadata cannot confirm",
                    substring = true
                )
            )
            .assertExists()

        // An incomplete review must not be saved
        scrollableColumn
            .performScrollToNode(
                hasText(
                    "Save Source Review"
                )
            )

        composeTestRule
            .onNode(
                hasText(
                    "Save Source Review"
                )
            )
            .assertIsNotEnabled()
    }

    @Test
    fun completeReview_enablesSaveAndInvokesCallback() {
        var saveClickCount = 0

        composeTestRule.setContent {
            SourceSenseTheme(
                dynamicColor = false
            ) {
                RealSourceReviewScreen(
                    uiState =
                        createCompleteState(),
                    onBackToExplore = {},
                    onOpenPaperPage = {},
                    onRelevanceSelected = {},
                    onPublicationInformationSelected = {},
                    onCurrencySelected = {},
                    onReviewDepthSelected = {},
                    onCitationDecisionSelected = {},
                    onVerificationItemToggled = {},
                    onReflectionNoteChange = {},
                    onSaveReview = {
                        saveClickCount += 1
                    },
                    onRetrySaving = {}
                )
            }
        }

        val scrollableColumn =
            composeTestRule.onNode(
                hasScrollAction()
            )

        // Scroll until the save action is composed
        scrollableColumn
            .performScrollToNode(
                hasText(
                    "Save Source Review"
                )
            )

        composeTestRule
            .onNode(
                hasText(
                    "Save Source Review"
                )
            )
            .assertIsEnabled()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                1,
                saveClickCount
            )
        }
    }

    @Test
    fun savedReview_showsConfirmationAndRemovesActiveSaveAction() {
        composeTestRule.setContent {
            SourceSenseTheme(
                dynamicColor = false
            ) {
                RealSourceReviewScreen(
                    uiState =
                        createCompleteState(
                            savedReviewId = 7L
                        ),
                    onBackToExplore = {},
                    onOpenPaperPage = {},
                    onRelevanceSelected = {},
                    onPublicationInformationSelected = {},
                    onCurrencySelected = {},
                    onReviewDepthSelected = {},
                    onCitationDecisionSelected = {},
                    onVerificationItemToggled = {},
                    onReflectionNoteChange = {},
                    onSaveReview = {},
                    onRetrySaving = {}
                )
            }
        }

        val scrollableColumn =
            composeTestRule.onNode(
                hasScrollAction()
            )

        // Scroll until the saved confirmation is composed
        scrollableColumn
            .performScrollToNode(
                hasText(
                    text =
                        "Your structured review has been stored locally",
                    substring = true
                )
            )

        composeTestRule
            .onNode(
                hasText(
                    text =
                        "Your structured review has been stored locally",
                    substring = true
                )
            )
            .assertExists()

        composeTestRule
            .onNode(
                hasText(
                    "Save Source Review"
                )
            )
            .assertDoesNotExist()
    }

    private fun createCompleteState(
        savedReviewId: Long? = null
    ): RealSourceReviewUiState {
        return RealSourceReviewUiState(
            source = createSource(),
            searchTopic =
                "responsible AI education",
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
            verificationItems = setOf(
                SourceVerificationItem.SAMPLE,
                SourceVerificationItem.LIMITATIONS
            ),
            reflectionNote =
                "Verify the sample before citing.",
            savedReviewId =
                savedReviewId
        )
    }

    private fun createSource():
            AcademicSource {
        return AcademicSource(
            doi =
                "10.1000/source-sense-test",
            title =
                "Responsible AI in University Learning",
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
                "https://doi.org/10.1000/source-sense-test",
            abstractText =
                "This study examines responsible AI use in university learning."
        )
    }
}

