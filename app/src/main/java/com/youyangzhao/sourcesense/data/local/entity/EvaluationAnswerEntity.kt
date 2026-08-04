package com.youyangzhao.sourcesense.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "evaluation_answers",
    foreignKeys = [
        ForeignKey(
            entity = EvaluationAttemptEntity::class,
            parentColumns = ["id"],
            childColumns = ["attempt_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["attempt_id"]),
        Index(value = ["dimension"]),
        Index(
            value = ["attempt_id", "question_id"],
            unique = true
        )
    ]
)
data class EvaluationAnswerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "attempt_id")
    val attemptId: Long,
    @ColumnInfo(name = "question_id")
    val questionId: String,
    val dimension: String,
    @ColumnInfo(name = "selected_option_id")
    val selectedOptionId: String?,
    @ColumnInfo(name = "selected_option_text")
    val selectedOptionText: String?,
    @ColumnInfo(name = "correct_option_id")
    val correctOptionId: String,
    @ColumnInfo(name = "correct_option_text")
    val correctOptionText: String,
    @ColumnInfo(name = "is_correct")
    val isCorrect: Boolean
)

