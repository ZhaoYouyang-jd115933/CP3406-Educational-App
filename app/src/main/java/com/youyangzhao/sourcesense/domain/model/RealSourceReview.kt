package com.youyangzhao.sourcesense.domain.model

enum class SourceRelevanceAssessment(
    val displayName: String,
    val description: String
) {
    DIRECTLY_RELEVANT(
        displayName = "Directly relevant",
        description =
            "The source closely matches the search topic or research need."
    ),
    PARTLY_RELEVANT(
        displayName = "Partly relevant",
        description =
            "The source addresses only part of the topic, population or context."
    ),
    UNCLEAR(
        displayName = "Unclear",
        description =
            "The available information is not enough to judge relevance confidently."
    )
}

enum class PublicationInformationAssessment(
    val displayName: String,
    val description: String
) {
    CLEAR_ENOUGH(
        displayName = "Clear enough",
        description =
            "The authorship, publication, year and DOI are sufficiently clear."
    ),
    PARTLY_CLEAR(
        displayName = "Partly clear",
        description =
            "Some important publication information is missing or uncertain."
    ),
    INSUFFICIENT(
        displayName = "Insufficient",
        description =
            "The publication record lacks enough information for confident use."
    )
}

enum class SourceCurrencyAssessment(
    val displayName: String,
    val description: String
) {
    CURRENT_ENOUGH(
        displayName = "Current enough",
        description =
            "The publication date appears suitable for the topic."
    ),
    OLDER_BUT_RELEVANT(
        displayName = "Older but still relevant",
        description =
            "The source is older but may remain useful for theory, history or foundational evidence."
    ),
    MAY_BE_OUTDATED(
        displayName = "May be outdated",
        description =
            "Newer evidence may be needed before relying on this source."
    )
}

enum class SourceReviewDepth(
    val displayName: String,
    val description: String
) {
    METADATA_ONLY(
        displayName = "Metadata only",
        description =
            "Only the title, authors, year, publication and DOI were reviewed."
    ),
    ABSTRACT_REVIEWED(
        displayName = "Abstract reviewed",
        description =
            "The abstract was reviewed, but the full paper was not checked."
    ),
    FULL_TEXT_REVIEWED(
        displayName = "Full text reviewed",
        description =
            "The full paper was reviewed, including its method, findings and limitations."
    )
}

enum class SourceCitationDecision(
    val displayName: String,
    val description: String
) {
    READY_TO_CONSIDER(
        displayName = "Ready to consider",
        description =
            "The source appears promising enough for further academic consideration."
    ),
    NEEDS_FULL_TEXT_REVIEW(
        displayName = "Needs more review",
        description =
            "More information is required before deciding whether to use the source."
    ),
    NOT_SUITABLE(
        displayName = "Not suitable",
        description =
            "The source does not currently fit the topic or evidence need."
    )
}

enum class SourceVerificationItem(
    val displayName: String
) {
    RESEARCH_METHOD(
        displayName = "Research method"
    ),
    SAMPLE(
        displayName = "Sample and participants"
    ),
    FULL_FINDINGS(
        displayName = "Full findings"
    ),
    LIMITATIONS(
        displayName = "Study limitations"
    ),
    FUNDING_AND_CONFLICTS(
        displayName = "Funding and conflicts"
    ),
    CITATION_CONTEXT(
        displayName = "Citation context"
    )
}

data class RealSourceReview(
    val id: Long = 0,
    val doi: String,
    val title: String,
    val authors: String,
    val publicationYear: Int?,
    val publicationName: String?,
    val publisher: String?,
    val sourceType: String?,
    val searchTopic: String,
    val relevanceAssessment: SourceRelevanceAssessment,
    val publicationInformationAssessment:
    PublicationInformationAssessment,
    val currencyAssessment: SourceCurrencyAssessment,
    val reviewDepth: SourceReviewDepth,
    val citationDecision: SourceCitationDecision,
    val verificationItems: Set<SourceVerificationItem>,
    val reflectionNote: String,
    val reviewedAt: Long
) {
    init {
        require(doi.isNotBlank()) {
            "A source review must include a DOI."
        }

        require(title.isNotBlank()) {
            "A source review must include a title."
        }

        require(searchTopic.isNotBlank()) {
            "A source review must include the search topic."
        }

        require(verificationItems.isNotEmpty()) {
            "A source review must include at least one verification item."
        }
    }
}

