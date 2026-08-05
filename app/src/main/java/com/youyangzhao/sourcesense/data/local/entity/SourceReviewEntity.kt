package com.youyangzhao.sourcesense.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Store one structured review of a real academic source
@Entity(
    tableName = "source_reviews",
    indices = [
        Index(value = ["doi"])
    ]
)
data class SourceReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val doi: String,
    val title: String,
    val authors: String,
    val publicationYear: Int?,
    val publicationName: String?,
    val publisher: String?,
    val sourceType: String?,
    val searchTopic: String,
    val relevanceAssessment: String,
    val publicationInformationAssessment: String,
    val currencyAssessment: String,
    val reviewDepth: String,
    val citationDecision: String,
    val verificationItems: String,
    val reflectionNote: String,
    val reviewedAt: Long
)
