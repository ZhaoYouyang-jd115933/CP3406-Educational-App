package com.youyangzhao.sourcesense.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evaluation_attempts")
data class EvaluationAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "evidence_case_id")
    val evidenceCaseId: String,
    val score: Int,
    @ColumnInfo(name = "total_questions")
    val totalQuestions: Int,
    val percentage: Int,
    @ColumnInfo(name = "completed_at")
    val completedAt: Long
)

