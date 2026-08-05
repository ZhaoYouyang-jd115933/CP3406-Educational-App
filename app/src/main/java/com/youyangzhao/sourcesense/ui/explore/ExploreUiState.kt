package com.youyangzhao.sourcesense.ui.explore

import com.youyangzhao.sourcesense.domain.model.AcademicSource
import com.youyangzhao.sourcesense.domain.model.RealSourceReview

data class ExploreUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val sources: List<AcademicSource> = emptyList(),
    val savedReviews:
    List<RealSourceReview> = emptyList(),
    val hasSearched: Boolean = false,
    val errorMessage: String? = null,
    val reviewHistoryErrorMessage: String? = null
) {
    val canSearch: Boolean
        get() = query.isNotBlank() && !isLoading

    val hasResults: Boolean
        get() = sources.isNotEmpty()

    val hasSavedReviews: Boolean
        get() = savedReviews.isNotEmpty()

    val showEmptyResults: Boolean
        get() {
            return hasSearched &&
                    !isLoading &&
                    errorMessage == null &&
                    sources.isEmpty()
        }
}

