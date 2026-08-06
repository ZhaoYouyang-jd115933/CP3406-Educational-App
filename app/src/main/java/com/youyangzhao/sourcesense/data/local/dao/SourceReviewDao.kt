package com.youyangzhao.sourcesense.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.youyangzhao.sourcesense.data.local.entity.SourceReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceReviewDao {

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertSourceReview(
        review: SourceReviewEntity
    ): Long

    @Query(
        """
        SELECT *
        FROM source_reviews
        ORDER BY reviewedAt DESC
        """
    )
    fun observeSourceReviews():
            Flow<List<SourceReviewEntity>>

    @Query(
        """
        SELECT *
        FROM source_reviews
        WHERE id = :reviewId
        LIMIT 1
        """
    )
    suspend fun getSourceReview(
        reviewId: Long
    ): SourceReviewEntity?

    @Query(
        """
        DELETE FROM source_reviews
        """
    )
    suspend fun clearAllSourceReviews()
}

