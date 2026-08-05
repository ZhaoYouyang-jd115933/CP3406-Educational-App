package com.youyangzhao.sourcesense.ui.review

import com.youyangzhao.sourcesense.domain.model.AcademicSource
import com.youyangzhao.sourcesense.domain.model.PublicationInformationAssessment
import com.youyangzhao.sourcesense.domain.model.SourceCitationDecision
import com.youyangzhao.sourcesense.domain.model.SourceCurrencyAssessment
import com.youyangzhao.sourcesense.domain.model.SourceRelevanceAssessment
import com.youyangzhao.sourcesense.domain.model.SourceReviewDepth
import com.youyangzhao.sourcesense.domain.model.SourceVerificationItem

data class RealSourceReviewUiState(
    val source: AcademicSource? = null,
    val searchTopic: String = "",
    val relevanceAssessment:
    SourceRelevanceAssessment? = null,
    val publicationInformationAssessment:
    PublicationInformationAssessment? = null,
    val currencyAssessment:
    SourceCurrencyAssessment? = null,
    val reviewDepth: SourceReviewDepth? = null,
    val citationDecision:
    SourceCitationDecision? = null,
    val verificationItems:
    Set<SourceVerificationItem> = emptySet(),
    val reflectionNote: String = "",
    val isSaving: Boolean = false,
    val savedReviewId: Long? = null,
    val errorMessage: String? = null
) {
    val hasSource: Boolean
        get() = source != null

    val isSaved: Boolean
        get() = savedReviewId != null

    val canEdit: Boolean
        get() = hasSource &&
                !isSaving &&
                !isSaved

    val canSave: Boolean
        get() = canEdit &&
                searchTopic.isNotBlank() &&
                relevanceAssessment != null &&
                publicationInformationAssessment != null &&
                currencyAssessment != null &&
                reviewDepth != null &&
                citationDecision != null &&
                verificationItems.isNotEmpty()
}

