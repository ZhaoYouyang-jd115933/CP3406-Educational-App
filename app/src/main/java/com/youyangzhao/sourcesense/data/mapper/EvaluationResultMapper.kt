package com.youyangzhao.sourcesense.data.mapper

import com.youyangzhao.sourcesense.data.local.entity.EvaluationAnswerEntity
import com.youyangzhao.sourcesense.data.local.entity.EvaluationAttemptEntity
import com.youyangzhao.sourcesense.domain.model.EvaluationResult

fun EvaluationResult.toAttemptEntity(
    completedAt: Long
): EvaluationAttemptEntity {
    return EvaluationAttemptEntity(
        evidenceCaseId = evidenceCaseId,
        score = score,
        totalQuestions = totalQuestions,
        percentage = percentage,
        completedAt = completedAt
    )
}

fun EvaluationResult.toAnswerEntities():
        List<EvaluationAnswerEntity> {
    return questionResults.map { questionResult ->
        EvaluationAnswerEntity(
            attemptId = 0,
            questionId = questionResult.questionId,
            dimension = questionResult.dimension.name,
            selectedOptionId = questionResult.selectedOptionId,
            selectedOptionText = questionResult.selectedOptionText,
            correctOptionId = questionResult.correctOptionId,
            correctOptionText = questionResult.correctOptionText,
            isCorrect = questionResult.isCorrect
        )
    }
}

