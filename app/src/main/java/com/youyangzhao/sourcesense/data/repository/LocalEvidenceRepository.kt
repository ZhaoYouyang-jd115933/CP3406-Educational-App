package com.youyangzhao.sourcesense.data.repository

import com.youyangzhao.sourcesense.domain.model.AnswerOption
import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvidenceCase
import com.youyangzhao.sourcesense.domain.model.SourceType
import com.youyangzhao.sourcesense.domain.repository.EvidenceRepository

class LocalEvidenceRepository : EvidenceRepository {

    private val evidenceCases = listOf(
        createSocialMediaCase()
    )

    override suspend fun getEvidenceCases(): List<EvidenceCase> {
        return evidenceCases
    }

    override suspend fun getEvidenceCase(
        caseId: String
    ): EvidenceCase? {
        return evidenceCases.firstOrNull { evidenceCase ->
            evidenceCase.id == caseId
        }
    }

    private fun createSocialMediaCase(): EvidenceCase {
        return EvidenceCase(
            id = "social_media_depression",
            researchQuestion = """
                Does frequent social media use directly cause depressive symptoms among university students?
            """.trimIndent(),
            title = """
                Daily Social Media Use and Depressive Symptoms in Undergraduate Students
            """.trimIndent(),
            authors = "M. Lee and R. Tan",
            publication = "Journal of Student Wellbeing",
            publishedYear = 2025,
            excerpt = """
                A cross-sectional survey of 420 undergraduate students found that 
                participants who reported more daily social media use also reported 
                higher depressive symptom scores. The authors stated that the study 
                could not establish temporal order or causation.
            """.trimIndent(),
            methodSummary = "Cross-sectional self-report survey",
            sampleSummary = "420 undergraduate volunteers from one university",
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = createSocialMediaQuestions()
        )
    }

    private fun createSocialMediaQuestions(): List<EvaluationQuestion> {
        return listOf(
            createRelevanceQuestion(),
            createSourceTypeQuestion(),
            createEvidenceStrengthQuestion(),
            createCausationQuestion(),
            createOverclaimingQuestion(),
            createCitationSupportQuestion()
        )
    }

    private fun createRelevanceQuestion(): EvaluationQuestion {
        return EvaluationQuestion(
            id = "relevance",
            dimension = EvaluationDimension.RELEVANCE,
            prompt = """
                How relevant is this source to the research question?
            """.trimIndent(),
            options = listOf(
                AnswerOption(
                    id = "highly_relevant",
                    text = "Highly relevant because it directly proves causation"
                ),
                AnswerOption(
                    id = "partly_relevant",
                    text = "Partly relevant because it examines the relationship but not causation"
                ),
                AnswerOption(
                    id = "not_relevant",
                    text = "Not relevant because it does not discuss university students"
                )
            ),
            correctOptionId = "partly_relevant",
            explanation = """
                The study examines social media use and depressive symptoms in university 
                students, so it is relevant to the topic. However, its cross-sectional 
                design cannot directly answer whether social media causes depression.
            """.trimIndent(),
            learningTip = """
                A relevant topic does not automatically mean that a source can answer 
                every part of a research question.
            """.trimIndent()
        )
    }

    private fun createSourceTypeQuestion(): EvaluationQuestion {
        return EvaluationQuestion(
            id = "source_type",
            dimension = EvaluationDimension.SOURCE_TYPE,
            prompt = "What type of source is presented?",
            options = listOf(
                AnswerOption(
                    id = "peer_reviewed",
                    text = "Peer-reviewed journal article"
                ),
                AnswerOption(
                    id = "news",
                    text = "News article"
                ),
                AnswerOption(
                    id = "blog",
                    text = "Personal blog post"
                ),
                AnswerOption(
                    id = "commercial",
                    text = "Commercial webpage"
                )
            ),
            correctOptionId = "peer_reviewed",
            explanation = """
                The source is presented as an article published in an academic journal 
                with named authors, a research method and a defined study sample.
            """.trimIndent(),
            learningTip = """
                Check the publication venue, authorship, research method and review 
                process before classifying a source.
            """.trimIndent()
        )
    }

    private fun createEvidenceStrengthQuestion(): EvaluationQuestion {
        return EvaluationQuestion(
            id = "evidence_strength",
            dimension = EvaluationDimension.EVIDENCE_STRENGTH,
            prompt = """
                How strong is this evidence for answering the causal research question?
            """.trimIndent(),
            options = listOf(
                AnswerOption(
                    id = "strong_causal",
                    text = "Strong enough to prove a causal relationship"
                ),
                AnswerOption(
                    id = "useful_limited",
                    text = "Useful evidence, but limited for causal conclusions"
                ),
                AnswerOption(
                    id = "no_evidence",
                    text = "It provides no useful evidence"
                )
            ),
            correctOptionId = "useful_limited",
            explanation = """
                The study provides useful evidence of an association, but self-reported 
                data, one university and a cross-sectional design limit its ability to 
                support a causal conclusion.
            """.trimIndent(),
            learningTip = """
                Evidence strength depends on study design, sample quality, measurement 
                methods and the claim being evaluated.
            """.trimIndent()
        )
    }

    private fun createCausationQuestion(): EvaluationQuestion {
        return EvaluationQuestion(
            id = "causation",
            dimension = EvaluationDimension.CAUSATION,
            prompt = """
                What conclusion can be drawn from the reported relationship?
            """.trimIndent(),
            options = listOf(
                AnswerOption(
                    id = "proves_causation",
                    text = "Social media use directly causes depressive symptoms"
                ),
                AnswerOption(
                    id = "supports_association",
                    text = "The variables were associated in this sample"
                ),
                AnswerOption(
                    id = "no_relationship",
                    text = "There was no relationship between the variables"
                )
            ),
            correctOptionId = "supports_association",
            explanation = """
                The study measured both variables at one point in time. It found an 
                association, but it cannot show which variable came first or exclude 
                other explanations.
            """.trimIndent(),
            learningTip = """
                Correlation describes variables changing together. Causation requires 
                stronger evidence that one variable produced the other.
            """.trimIndent()
        )
    }

    private fun createOverclaimingQuestion(): EvaluationQuestion {
        return EvaluationQuestion(
            id = "overclaiming",
            dimension = EvaluationDimension.OVERCLAIMING,
            prompt = """
                A student writes: "This study proves that social media causes depression 
                in all university students." How should this statement be evaluated?
            """.trimIndent(),
            options = listOf(
                AnswerOption(
                    id = "accurate",
                    text = "It accurately reports the study"
                ),
                AnswerOption(
                    id = "overclaiming",
                    text = "It overstates what the evidence can support"
                ),
                AnswerOption(
                    id = "unrelated",
                    text = "It is unrelated to the study"
                )
            ),
            correctOptionId = "overclaiming",
            explanation = """
                The statement changes an association into causation and generalises from 
                one volunteer sample to all university students.
            """.trimIndent(),
            learningTip = """
                Watch for words such as proves, causes, always and all when the evidence 
                is limited or observational.
            """.trimIndent()
        )
    }

    private fun createCitationSupportQuestion(): EvaluationQuestion {
        return EvaluationQuestion(
            id = "citation_support",
            dimension = EvaluationDimension.CITATION_SUPPORT,
            prompt = """
                Which statement is best supported by this source?
            """.trimIndent(),
            options = listOf(
                AnswerOption(
                    id = "causal_claim",
                    text = "Social media use causes depression in university students"
                ),
                AnswerOption(
                    id = "supported_claim",
                    text = """
                        In this sample, heavier social media use was associated with 
                        higher depressive symptom scores
                    """.trimIndent()
                ),
                AnswerOption(
                    id = "universal_claim",
                    text = """
                        All students with depression use social media for more than four 
                        hours each day
                    """.trimIndent()
                )
            ),
            correctOptionId = "supported_claim",
            explanation = """
                This statement preserves the study's sample boundary and describes an 
                association rather than making an unsupported causal claim.
            """.trimIndent(),
            learningTip = """
                A citation should support the exact wording of the claim, not merely 
                discuss a similar topic.
            """.trimIndent()
        )
    }
}

