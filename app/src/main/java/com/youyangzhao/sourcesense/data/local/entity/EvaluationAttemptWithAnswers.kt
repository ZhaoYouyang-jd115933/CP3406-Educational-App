package com.youyangzhao.sourcesense.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class EvaluationAttemptWithAnswers(
    @Embedded
    val attempt: EvaluationAttemptEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "attempt_id"
    )
    val answers: List<EvaluationAnswerEntity>
)

