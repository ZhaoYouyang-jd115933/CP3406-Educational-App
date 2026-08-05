package com.youyangzhao.sourcesense.data.repository

import com.youyangzhao.sourcesense.data.local.dao.SourceReviewDao
import com.youyangzhao.sourcesense.data.mapper.toRealSourceReviewOrNull
import com.youyangzhao.sourcesense.data.mapper.toSourceReviewEntity
import com.youyangzhao.sourcesense.domain.model.RealSourceReview
import com.youyangzhao.sourcesense.domain.repository.SourceReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomSourceReviewRepository(
    private val sourceReviewDao: SourceReviewDao
) : SourceReviewRepository {

    override fun observeSourceReviews():
            Flow<List<RealSourceReview>> {
        return sourceReviewDao
            .observeSourceReviews()
            .map { storedReviews ->
                storedReviews.mapNotNull { storedReview ->
                    // Ignore invalid legacy values safely
                    storedReview.toRealSourceReviewOrNull()
                }
            }
    }

    override suspend fun saveSourceReview(
        review: RealSourceReview
    ): Long {
        return sourceReviewDao.insertSourceReview(
            review = review.toSourceReviewEntity()
        )
    }

    override suspend fun getSourceReview(
        reviewId: Long
    ): RealSourceReview? {
        return sourceReviewDao
            .getSourceReview(
                reviewId = reviewId
            )
            ?.toRealSourceReviewOrNull()
    }
}

