package com.youyangzhao.sourcesense.domain.model

enum class SourceType(
    val displayName: String
) {
    PEER_REVIEWED_ARTICLE("Peer-reviewed journal article"),
    NEWS_ARTICLE("News article"),
    BLOG_POST("Blog post"),
    COMMERCIAL_WEBPAGE("Commercial webpage")
}

data class EvidenceCase(
    val id: String,
    val researchQuestion: String,
    val title: String,
    val authors: String,
    val publication: String,
    val publishedYear: Int,
    val excerpt: String,
    val methodSummary: String,
    val sampleSummary: String,
    val sourceType: SourceType,
    val sourceNote: String,
    val questions: List<EvaluationQuestion>
) {
    init {
        // Prevent incomplete learning cases
        require(questions.isNotEmpty()) {
            "An evidence case must contain evaluation questions."
        }

        // Prevent duplicate question identifiers
        require(questions.map { question ->
            question.id
        }.distinct().size == questions.size) {
            "Question identifiers must be unique."
        }
    }
}

