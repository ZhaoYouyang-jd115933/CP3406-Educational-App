package com.youyangzhao.sourcesense.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youyangzhao.sourcesense.domain.repository.AcademicSourceRepository
import com.youyangzhao.sourcesense.domain.repository.SourceReviewRepository
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ExploreViewModel(
    private val academicSourceRepository:
    AcademicSourceRepository,
    private val sourceReviewRepository:
    SourceReviewRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(ExploreUiState())

    val uiState: StateFlow<ExploreUiState> =
        _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var lastSubmittedQuery: String? = null

    init {
        observeSavedReviews()
    }

    private fun observeSavedReviews() {
        viewModelScope.launch {
            sourceReviewRepository
                .observeSourceReviews()
                .catch {
                    _uiState.update { currentState ->
                        currentState.copy(
                            reviewHistoryErrorMessage =
                                "Saved source reviews could not be loaded."
                        )
                    }
                }
                .collect { reviews ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            savedReviews = reviews,
                            reviewHistoryErrorMessage = null
                        )
                    }
                }
        }
    }

    fun updateQuery(
        query: String
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                query = query,
                errorMessage = null
            )
        }
    }

    fun searchSources() {
        val cleanQuery =
            _uiState.value.query.trim()

        if (cleanQuery.isBlank()) {
            _uiState.update { currentState ->
                currentState.copy(
                    errorMessage =
                        "Enter a topic, title or DOI to search."
                )
            }

            return
        }

        performSearch(
            query = cleanQuery
        )
    }

    private fun performSearch(
        query: String
    ) {
        searchJob?.cancel()
        lastSubmittedQuery = query

        searchJob = viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    query = query,
                    isLoading = true,
                    sources = emptyList(),
                    hasSearched = false,
                    errorMessage = null
                )
            }

            try {
                val sources =
                    academicSourceRepository
                        .searchSources(
                            query = query,
                            limit = 10
                        )

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        sources = sources,
                        hasSearched = true,
                        errorMessage = null
                    )
                }
            } catch (
                exception: CancellationException
            ) {
                throw exception
            } catch (
                exception: IOException
            ) {
                showSearchError(
                    message =
                        "Unable to connect. Check your internet connection and try again."
                )
            } catch (
                exception: HttpException
            ) {
                showSearchError(
                    message =
                        "Crossref could not complete the search. Please try again later."
                )
            } catch (
                exception: Exception
            ) {
                showSearchError(
                    message =
                        "Academic sources could not be loaded. Please try again."
                )
            }
        }
    }

    private fun showSearchError(
        message: String
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = false,
                sources = emptyList(),
                hasSearched = true,
                errorMessage = message
            )
        }
    }

    fun retrySearch() {
        val query = lastSubmittedQuery
            ?: _uiState.value.query.trim()

        if (query.isBlank()) {
            _uiState.update { currentState ->
                currentState.copy(
                    errorMessage =
                        "Enter a topic, title or DOI to search."
                )
            }

            return
        }

        performSearch(
            query = query
        )
    }

    fun clearSearch() {
        searchJob?.cancel()
        lastSubmittedQuery = null

        _uiState.update { currentState ->
            ExploreUiState(
                savedReviews =
                    currentState.savedReviews,
                reviewHistoryErrorMessage =
                    currentState.reviewHistoryErrorMessage
            )
        }
    }

    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(
                errorMessage = null
            )
        }
    }
}

class ExploreViewModelFactory(
    private val academicSourceRepository:
    AcademicSourceRepository,
    private val sourceReviewRepository:
    SourceReviewRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                ExploreViewModel::class.java
            )
        ) {
            return ExploreViewModel(
                academicSourceRepository =
                    academicSourceRepository,
                sourceReviewRepository =
                    sourceReviewRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}

