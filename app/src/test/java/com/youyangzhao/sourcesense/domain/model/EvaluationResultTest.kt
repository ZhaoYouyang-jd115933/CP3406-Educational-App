package com.youyangzhao.sourcesense.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EvaluationResultTest {

    @Test
    fun calculateEvaluationResult_allAnswersCorrect_returnsFullScore() {
        val evidenceCase = createTestEvidenceCase()
        val selectedAnswers = mapOf(
            "relevance" to "partly_relevant",
            "causation" to "association"
        )

        val result = calculateEvaluationResult(
            evidenceCase = evidenceCase,
            selectedAnswers = selectedAnswers
        )

        assertEquals(2, result.score)
        assertEquals(2, result.totalQuestions)
        assertEquals(100, result.percentage)
        assertTrue(result.questionResults.all { questionResult ->
            questionResult.isCorrect
        })
    }

    @Test
    fun calculateEvaluationResult_oneIncorrectAnswer_returnsPartialScore() {
        val evidenceCase = createTestEvidenceCase()
        val selectedAnswers = mapOf(
            "relevance" to "not_relevant",
            "causation" to "association"
        )

        val result = calculateEvaluationResult(
            evidenceCase = evidenceCase,
            selectedAnswers = selectedAnswers
        )

        assertEquals(1, result.score)
        assertEquals(2, result.totalQuestions)
        assertEquals(50, result.percentage)
        assertFalse(result.questionResults.first().isCorrect)
        assertTrue(result.questionResults.last().isCorrect)
    }

    @Test
    fun calculateEvaluationResult_missingAnswer_marksQuestionIncorrect() {
        val evidenceCase = createTestEvidenceCase()
        val selectedAnswers = mapOf(
            "relevance" to "partly_relevant"
        )

        val result = calculateEvaluationResult(
            evidenceCase = evidenceCase,
            selectedAnswers = selectedAnswers
        )

        val unansweredResult = result.questionResults.last()

        assertEquals(1, result.score)
        assertEquals(50, result.percentage)
        assertFalse(unansweredResult.isCorrect)
        assertEquals(null, unansweredResult.selectedOptionId)
        assertEquals(null, unansweredResult.selectedOptionText)
    }

    private fun createTestEvidenceCase(): EvidenceCase {
        return EvidenceCase(
            id = "test_case",
            researchQuestion = "Does social media use cause depression?",
            title = "Social Media Use and Student Wellbeing",
            authors = "Test Author",
            publication = "Test Journal",
            publishedYear = 2026,
            excerpt = "The study found an association between two variables.",
            methodSummary = "Cross-sectional survey",
            sampleSummary = "100 university students",
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = "Test source",
            questions = listOf(
                createRelevanceQuestion(),
                createCausationQuestion()
            )
        )
    }

    private fun createRelevanceQuestion(): EvaluationQuestion {
        return EvaluationQuestion(
            id = "relevance",
            dimension = EvaluationDimension.RELEVANCE,
            prompt = "How relevant is this source?",
            options = listOf(
                AnswerOption(
                    id = "partly_relevant",
                    text = "Partly relevant"
                ),
                AnswerOption(
                    id = "not_relevant",
                    text = "Not relevant"
                )
            ),
            correctOptionId = "partly_relevant",
            explanation = "The source examines the topic but does not prove causation.",
            learningTip = "Separate topic relevance from causal evidence."
        )
    }

    private fun createCausationQuestion(): EvaluationQuestion {
        return EvaluationQuestion(
            id = "causation",
            dimension = EvaluationDimension.CAUSATION,
            prompt = "What does the evidence support?",
            options = listOf(
                AnswerOption(
                    id = "causation",
                    text = "A causal relationship"
                ),
                AnswerOption(
                    id = "association",
                    text = "An association"
                )
            ),
            correctOptionId = "association",
            explanation = "A cross-sectional study cannot establish causation.",
            learningTip = "Correlation does not automatically establish causation."
        )
    }
}

