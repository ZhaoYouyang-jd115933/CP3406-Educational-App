package com.youyangzhao.sourcesense.data.mapper

import com.youyangzhao.sourcesense.data.local.entity.SourceReviewEntity
import com.youyangzhao.sourcesense.domain.model.PublicationInformationAssessment
import com.youyangzhao.sourcesense.domain.model.RealSourceReview
import com.youyangzhao.sourcesense.domain.model.SourceCitationDecision
import com.youyangzhao.sourcesense.domain.model.SourceCurrencyAssessment
import com.youyangzhao.sourcesense.domain.model.SourceRelevanceAssessment
import com.youyangzhao.sourcesense.domain.model.SourceReviewDepth
import com.youyangzhao.sourcesense.domain.model.SourceVerificationItem

private const val VERIFICATION_SEPARATOR = "|"

fun RealSourceReview.toSourceReviewEntity():
        SourceReviewEntity {
    return SourceReviewEntity(
        id = id,
        doi = doi,
        title = title,
        authors = authors,
        publicationYear = publicationYear,
        publicationName = publicationName,
        publisher = publisher,
        sourceType = sourceType,
        searchTopic = searchTopic,
        relevanceAssessment =
            relevanceAssessment.name,
        publicationInformationAssessment =
            publicationInformationAssessment.name,
        currencyAssessment =
            currencyAssessment.name,
        reviewDepth = reviewDepth.name,
        citationDecision = citationDecision.name,
        verificationItems = verificationItems
            .sortedBy { item ->
                item.ordinal
            }
            .joinToString(
                separator = VERIFICATION_SEPARATOR
            ) { item ->
                item.name
            },
        reflectionNote = reflectionNote,
        reviewedAt = reviewedAt
    )
}

fun SourceReviewEntity.toRealSourceReviewOrNull():
        RealSourceReview? {
    val relevance = runCatching {
        SourceRelevanceAssessment.valueOf(
            relevanceAssessment
        )
    }.getOrNull() ?: return null

    val publicationInformation = runCatching {
        PublicationInformationAssessment.valueOf(
            publicationInformationAssessment
        )
    }.getOrNull() ?: return null

    val currency = runCatching {
        SourceCurrencyAssessment.valueOf(
            currencyAssessment
        )
    }.getOrNull() ?: return null

    val depth = runCatching {
        SourceReviewDepth.valueOf(
            reviewDepth
        )
    }.getOrNull() ?: return null

    val decision = runCatching {
        SourceCitationDecision.valueOf(
            citationDecision
        )
    }.getOrNull() ?: return null

    val verification = verificationItems
        .split(VERIFICATION_SEPARATOR)
        .mapNotNull { storedValue ->
            storedValue
                .takeIf { value ->
                    value.isNotBlank()
                }
                ?.let { value ->
                    runCatching {
                        SourceVerificationItem.valueOf(
                            value
                        )
                    }.getOrNull()
                }
        }
        .toSet()

    if (verification.isEmpty()) {
        return null
    }

    return RealSourceReview(
        id = id,
        doi = doi,
        title = title,
        authors = authors,
        publicationYear = publicationYear,
        publicationName = publicationName,
        publisher = publisher,
        sourceType = sourceType,
        searchTopic = searchTopic,
        relevanceAssessment = relevance,
        publicationInformationAssessment =
            publicationInformation,
        currencyAssessment = currency,
        reviewDepth = depth,
        citationDecision = decision,
        verificationItems = verification,
        reflectionNote = reflectionNote,
        reviewedAt = reviewedAt
    )
}

