package com.youyangzhao.sourcesense.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youyangzhao.sourcesense.domain.model.AcademicSource
import com.youyangzhao.sourcesense.domain.model.PublicationInformationAssessment
import com.youyangzhao.sourcesense.domain.model.RealSourceReview
import com.youyangzhao.sourcesense.domain.model.SourceCitationDecision
import com.youyangzhao.sourcesense.domain.model.SourceCurrencyAssessment
import com.youyangzhao.sourcesense.domain.model.SourceRelevanceAssessment
import com.youyangzhao.sourcesense.domain.model.SourceReviewDepth
import com.youyangzhao.sourcesense.domain.model.SourceVerificationItem
import com.youyangzhao.sourcesense.domain.repository.SourceReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RealSourceReviewViewModel(
    private val sourceReviewRepository:
    SourceReviewRepository,
    private val currentTimeProvider: () -> Long = {
        System.currentTimeMillis()
    }
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        RealSourceReviewUiState()
    )

    val uiState: StateFlow<RealSourceReviewUiState> =
        _uiState.asStateFlow()

    fun startReview(
        source: AcademicSource,
        searchTopic: String
    ) {
        // Start every source review with a clean form
        _uiState.value = RealSourceReviewUiState(
            source = source,
            searchTopic = searchTopic
                .trim()
                .ifBlank {
                    source.title
                }
        )
    }

    fun selectRelevance(
        assessment: SourceRelevanceAssessment
    ) {
        updateEditableState { currentState ->
            currentState.copy(
                relevanceAssessment = assessment,
                errorMessage = null
            )
        }
    }

    fun selectPublicationInformation(
        assessment:
        PublicationInformationAssessment
    ) {
        updateEditableState { currentState ->
            currentState.copy(
                publicationInformationAssessment =
                    assessment,
                errorMessage = null
            )
        }
    }

    fun selectCurrency(
        assessment: SourceCurrencyAssessment
    ) {
        updateEditableState { currentState ->
            currentState.copy(
                currencyAssessment = assessment,
                errorMessage = null
            )
        }
    }

    fun selectReviewDepth(
        depth: SourceReviewDepth
    ) {
        updateEditableState { currentState ->
            currentState.copy(
                reviewDepth = depth,
                errorMessage = null
            )
        }
    }

    fun selectCitationDecision(
        decision: SourceCitationDecision
    ) {
        updateEditableState { currentState ->
            currentState.copy(
                citationDecision = decision,
                errorMessage = null
            )
        }
    }

    fun toggleVerificationItem(
        item: SourceVerificationItem
    ) {
        updateEditableState { currentState ->
            val updatedItems =
                if (item in currentState.verificationItems) {
                    currentState.verificationItems - item
                } else {
                    currentState.verificationItems + item
                }

            currentState.copy(
                verificationItems = updatedItems,
                errorMessage = null
            )
        }
    }

    fun updateReflectionNote(
        note: String
    ) {
        updateEditableState { currentState ->
            currentState.copy(
                reflectionNote = note,
                errorMessage = null
            )
        }
    }

    fun saveReview() {
        val currentState = _uiState.value
        val source = currentState.source

        if (source == null) {
            _uiState.update { state ->
                state.copy(
                    errorMessage =
                        "No academic source has been selected."
                )
            }

            return
        }

        if (!currentState.canSave) {
            _uiState.update { state ->
                state.copy(
                    errorMessage =
                        "Complete every required section and select at least one item to verify."
                )
            }

            return
        }

        val review = RealSourceReview(
            doi = source.doi,
            title = source.title,
            authors = source.authorsDisplay,
            publicationYear =
                source.publicationYear,
            publicationName =
                source.publicationName,
            publisher = source.publisher,
            sourceType = source.sourceType,
            searchTopic = currentState.searchTopic,
            relevanceAssessment =
                requireNotNull(
                    currentState.relevanceAssessment
                ),
            publicationInformationAssessment =
                requireNotNull(
                    currentState
                        .publicationInformationAssessment
                ),
            currencyAssessment =
                requireNotNull(
                    currentState.currencyAssessment
                ),
            reviewDepth =
                requireNotNull(
                    currentState.reviewDepth
                ),
            citationDecision =
                requireNotNull(
                    currentState.citationDecision
                ),
            verificationItems =
                currentState.verificationItems,
            reflectionNote =
                currentState.reflectionNote.trim(),
            reviewedAt = currentTimeProvider()
        )

        _uiState.update { state ->
            state.copy(
                isSaving = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                sourceReviewRepository.saveSourceReview(
                    review = review
                )
            }.onSuccess { reviewId ->
                _uiState.update { state ->
                    state.copy(
                        isSaving = false,
                        savedReviewId = reviewId,
                        errorMessage = null
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        isSaving = false,
                        errorMessage =
                            "The source review could not be saved. Please try again."
                    )
                }
            }
        }
    }

    fun retrySaving() {
        if (!_uiState.value.isSaving) {
            saveReview()
        }
    }

    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(
                errorMessage = null
            )
        }
    }

    private fun updateEditableState(
        update:
            (RealSourceReviewUiState) ->
        RealSourceReviewUiState
    ) {
        _uiState.update { currentState ->
            if (!currentState.canEdit) {
                currentState
            } else {
                update(currentState)
            }
        }
    }
}

class RealSourceReviewViewModelFactory(
    private val sourceReviewRepository:
    SourceReviewRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                RealSourceReviewViewModel::class.java
            )
        ) {
            return RealSourceReviewViewModel(
                sourceReviewRepository =
                    sourceReviewRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}

