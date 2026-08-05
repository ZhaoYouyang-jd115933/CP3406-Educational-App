package com.youyangzhao.sourcesense.domain.repository

import com.youyangzhao.sourcesense.domain.model.RealSourceReview
import kotlinx.coroutines.flow.Flow

interface SourceReviewRepository {

    fun observeSourceReviews():
            Flow<List<RealSourceReview>>

    suspend fun saveSourceReview(
        review: RealSourceReview
    ): Long

    suspend fun getSourceReview(
        reviewId: Long
    ): RealSourceReview?
}

