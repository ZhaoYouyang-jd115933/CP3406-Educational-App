package com.youyangzhao.sourcesense.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.youyangzhao.sourcesense.data.local.entity.EvaluationAnswerEntity
import com.youyangzhao.sourcesense.data.local.entity.EvaluationAttemptEntity
import com.youyangzhao.sourcesense.data.local.entity.EvaluationAttemptWithAnswers
import kotlinx.coroutines.flow.Flow

data class DimensionAccuracy(
    val dimension: String,
    val correctAnswers: Long,
    val totalAnswers: Long
)

@Dao
interface EvaluationAttemptDao {

    @Insert
    suspend fun insertAttempt(
        attempt: EvaluationAttemptEntity
    ): Long

    @Insert
    suspend fun insertAnswers(
        answers: List<EvaluationAnswerEntity>
    )

    @Transaction
    suspend fun insertAttemptWithAnswers(
        attempt: EvaluationAttemptEntity,
        answers: List<EvaluationAnswerEntity>
    ): Long {
        val attemptId = insertAttempt(attempt)

        // Connect each answer to the generated attempt ID
        val linkedAnswers = answers.map { answer ->
            answer.copy(attemptId = attemptId)
        }

        insertAnswers(linkedAnswers)

        return attemptId
    }

    @Query(
        """
        SELECT *
        FROM evaluation_attempts
        ORDER BY completed_at DESC
        """
    )
    fun observeAttempts(): Flow<List<EvaluationAttemptEntity>>

    @Transaction
    @Query(
        """
        SELECT *
        FROM evaluation_attempts
        ORDER BY completed_at DESC
        """
    )
    fun observeAttemptsWithAnswers():
            Flow<List<EvaluationAttemptWithAnswers>>

    @Query(
        """
        SELECT COUNT(*)
        FROM evaluation_attempts
        """
    )
    fun observeAttemptCount(): Flow<Int>

    @Query(
        """
        SELECT AVG(percentage)
        FROM evaluation_attempts
        """
    )
    fun observeAveragePercentage(): Flow<Double?>

    @Query(
        """
        SELECT MAX(percentage)
        FROM evaluation_attempts
        """
    )
    fun observeBestPercentage(): Flow<Int?>

    @Query(
        """
        SELECT
            dimension AS dimension,
            SUM(
                CASE
                    WHEN is_correct = 1 THEN 1
                    ELSE 0
                END
            ) AS correctAnswers,
            COUNT(*) AS totalAnswers
        FROM evaluation_answers
        GROUP BY dimension
        ORDER BY dimension
        """
    )
    fun observeDimensionAccuracy(): Flow<List<DimensionAccuracy>>

    @Query(
        """
        DELETE FROM evaluation_attempts
        """
    )
    suspend fun clearAllAttempts()
}

