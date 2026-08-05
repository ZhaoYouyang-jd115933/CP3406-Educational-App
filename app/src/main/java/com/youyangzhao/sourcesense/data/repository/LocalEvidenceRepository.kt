package com.youyangzhao.sourcesense.data.repository

import com.youyangzhao.sourcesense.domain.model.AnswerOption
import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvidenceCase
import com.youyangzhao.sourcesense.domain.model.SourceType
import com.youyangzhao.sourcesense.domain.repository.EvidenceRepository

class LocalEvidenceRepository : EvidenceRepository {

    private val evidenceCases = listOf(
        createBeginnerCase(),
        createIntermediateCase(),
        createAdvancedCase()
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

    private fun createBeginnerCase(): EvidenceCase {
        return EvidenceCase(
            id = "beginner_phone_sleep_news",
            researchQuestion = """
                Does using a smartphone before bedtime reduce sleep quality among university students?
            """.trimIndent(),
            title = """
                Late-Night Phone Use Linked to Poorer Student Sleep
            """.trimIndent(),
            authors = "Campus Health Desk",
            publication = "University News Online",
            publishedYear = 2025,
            excerpt = """
                A university news website reported that an online survey of 
                300 students found that students who used smartphones for more 
                than two hours after 10 p.m. also reported poorer sleep quality. 
                The report did not identify the original researchers, provide 
                the survey questions or explain whether other factors were controlled.
            """.trimIndent(),
            methodSummary = """
                News report summarising an online self-report survey
            """.trimIndent(),
            sampleSummary = """
                300 self-selected students from one university
            """.trimIndent(),
            sourceType = SourceType.NEWS_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = createBeginnerQuestions()
        )
    }

    private fun createBeginnerQuestions(): List<EvaluationQuestion> {
        return listOf(
            EvaluationQuestion(
                id = "beginner_source_type",
                dimension = EvaluationDimension.SOURCE_TYPE,
                prompt = "What type of source is presented?",
                options = listOf(
                    AnswerOption(
                        id = "journal",
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
                        text = "Commercial product webpage"
                    )
                ),
                correctOptionId = "news",
                explanation = """
                    The source was published by a university news website. It reports 
                    information about a survey but is not the original academic study.
                """.trimIndent(),
                learningTip = """
                    Identify where the information was published before evaluating how 
                    it should be used.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "beginner_relevance",
                dimension = EvaluationDimension.RELEVANCE,
                prompt = """
                    How relevant is the source to the research question?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "fully_answers",
                        text = """
                            It fully answers the question because it proves that phone use reduces sleep quality
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "partly_relevant",
                        text = """
                            It is relevant because it examines phone use and sleep, but it does not prove causation
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "not_relevant",
                        text = """
                            It is not relevant because the participants were university students
                        """.trimIndent()
                    )
                ),
                correctOptionId = "partly_relevant",
                explanation = """
                    The source directly discusses smartphone use and student sleep. 
                    However, an association reported in a survey cannot establish that 
                    phone use caused poorer sleep.
                """.trimIndent(),
                learningTip = """
                    A source may be relevant to a topic without fully answering every 
                    part of the research question.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "beginner_credibility_check",
                dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                prompt = """
                    What should a student check before relying heavily on this report?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "original_study",
                        text = """
                            The original study, its researchers and how the survey was conducted
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "website_colour",
                        text = "The colours used on the news website"
                    ),
                    AnswerOption(
                        id = "headline_length",
                        text = "The number of words in the headline"
                    )
                ),
                correctOptionId = "original_study",
                explanation = """
                    The report does not provide enough information about the researchers, 
                    survey questions or data analysis. Checking the original study would 
                    allow a stronger evaluation.
                """.trimIndent(),
                learningTip = """
                    A secondary report should be traced back to the original evidence 
                    whenever possible.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "beginner_causation",
                dimension = EvaluationDimension.CAUSATION,
                prompt = """
                    What does the reported result show?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "proves_cause",
                        text = """
                            Smartphone use definitely caused poor sleep
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "association",
                        text = """
                            Smartphone use and poorer sleep were associated in the survey
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "no_relationship",
                        text = """
                            Smartphone use and sleep had no relationship
                        """.trimIndent()
                    )
                ),
                correctOptionId = "association",
                explanation = """
                    The survey found that heavier phone use and poorer sleep appeared 
                    together. It did not establish which factor came first or whether 
                    another factor affected both.
                """.trimIndent(),
                learningTip = """
                    An association shows that two variables are related, not that one 
                    necessarily caused the other.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "beginner_overclaiming",
                dimension = EvaluationDimension.OVERCLAIMING,
                prompt = """
                    Which statement most clearly overclaims the evidence?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "accurate_summary",
                        text = """
                            Students reporting more late-night phone use also reported poorer sleep
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "overclaim",
                        text = """
                            This survey proves that smartphones cause sleep problems in every university student
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "limited_summary",
                        text = """
                            The survey suggests a relationship that should be investigated further
                        """.trimIndent()
                    )
                ),
                correctOptionId = "overclaim",
                explanation = """
                    The statement incorrectly changes an association into causation and 
                    generalises one self-selected sample to every university student.
                """.trimIndent(),
                learningTip = """
                    Be cautious with words such as proves, causes, always and every.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "beginner_citation_support",
                dimension = EvaluationDimension.CITATION_SUPPORT,
                prompt = """
                    Which claim is directly supported by the source?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "supported",
                        text = """
                            In this survey, heavier late-night smartphone use was associated with poorer reported sleep
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "universal",
                        text = """
                            Every student who uses a smartphone at night will develop a sleep disorder
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "treatment",
                        text = """
                            Removing smartphones is a proven treatment for insomnia
                        """.trimIndent()
                    )
                ),
                correctOptionId = "supported",
                explanation = """
                    The supported statement preserves the survey context and describes 
                    an association instead of making a universal or causal claim.
                """.trimIndent(),
                learningTip = """
                    A citation must support the exact wording and strength of the claim.
                """.trimIndent()
            )
        )
    }

    private fun createIntermediateCase(): EvidenceCase {
        return EvidenceCase(
            id = "intermediate_social_media_article",
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
            sampleSummary = """
                420 undergraduate volunteers from one university
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = createIntermediateQuestions()
        )
    }

    private fun createIntermediateQuestions(): List<EvaluationQuestion> {
        return listOf(
            EvaluationQuestion(
                id = "intermediate_method",
                dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                prompt = """
                    How appropriate is the study design for answering the causal research question?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "fully_appropriate",
                        text = """
                            Fully appropriate because cross-sectional surveys can establish causation
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "limited",
                        text = """
                            Useful for identifying an association, but limited for establishing causation
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "completely_useless",
                        text = """
                            Completely useless because surveys cannot provide any evidence
                        """.trimIndent()
                    )
                ),
                correctOptionId = "limited",
                explanation = """
                    A cross-sectional survey measures variables at one point in time. 
                    It can identify an association but cannot establish temporal order 
                    or confidently rule out alternative explanations.
                """.trimIndent(),
                learningTip = """
                    Evaluate whether the research method matches the kind of claim being made.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "intermediate_sample",
                dimension = EvaluationDimension.GENERALISATION,
                prompt = """
                    What is the most important limitation of the sample?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "single_volunteer_sample",
                        text = """
                            It contains volunteers from one university, so it may not represent all university students
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "too_many",
                        text = """
                            It contains too many participants to produce useful findings
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "student_problem",
                        text = """
                            University students can never participate in research
                        """.trimIndent()
                    )
                ),
                correctOptionId = "single_volunteer_sample",
                explanation = """
                    Volunteers from one university may differ from students at other 
                    institutions, locations or educational systems.
                """.trimIndent(),
                learningTip = """
                    Consider how participants were recruited and which population they represent.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "intermediate_strength",
                dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                prompt = """
                    How strong is this evidence for the claim that social media causes depression?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "strong",
                        text = "Strong enough to prove the causal claim"
                    ),
                    AnswerOption(
                        id = "useful_limited",
                        text = """
                            Useful evidence of an association, but weak evidence of causation
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "none",
                        text = "It provides no relevant information"
                    )
                ),
                correctOptionId = "useful_limited",
                explanation = """
                    The evidence is relevant and useful, but the observational design, 
                    self-reported measures and restricted sample limit causal interpretation.
                """.trimIndent(),
                learningTip = """
                    Evidence can be useful without being strong enough to support every claim.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "intermediate_self_report",
                dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                prompt = """
                    Why might self-reported social media use weaken the evidence?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "reporting_error",
                        text = """
                            Participants may remember or report their behaviour inaccurately
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "automatic_cause",
                        text = """
                            Self-reported data automatically proves causation
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "larger_sample",
                        text = """
                            Self-reporting always creates a larger and more representative sample
                        """.trimIndent()
                    )
                ),
                correctOptionId = "reporting_error",
                explanation = """
                    Participants may underestimate, overestimate or inaccurately remember 
                    their social media use and depressive symptoms.
                """.trimIndent(),
                learningTip = """
                    Ask how each variable was measured and whether the measurement may contain bias.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "intermediate_generalisation",
                dimension = EvaluationDimension.GENERALISATION,
                prompt = """
                    Which conclusion stays within the sample and evidence boundaries?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "all_students",
                        text = """
                            Social media causes depression in all university students
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "sample_boundary",
                        text = """
                            Among the surveyed students, heavier social media use was associated with higher depressive symptom scores
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "no_value",
                        text = """
                            The study tells us nothing about social media or depressive symptoms
                        """.trimIndent()
                    )
                ),
                correctOptionId = "sample_boundary",
                explanation = """
                    This conclusion describes the observed association and clearly limits 
                    the statement to the surveyed participants.
                """.trimIndent(),
                learningTip = """
                    Match the scope of a claim to the population actually studied.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "intermediate_revision",
                dimension = EvaluationDimension.CITATION_SUPPORT,
                prompt = """
                    Which sentence is the most appropriate way to use this source in an academic paper?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "causal_sentence",
                        text = """
                            Lee and Tan prove that social media directly causes depression
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "qualified_sentence",
                        text = """
                            Lee and Tan found an association between reported social media use and depressive symptoms, although their cross-sectional design could not establish causation
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "unrelated_sentence",
                        text = """
                            Lee and Tan showed that all technology harms student achievement
                        """.trimIndent()
                    )
                ),
                correctOptionId = "qualified_sentence",
                explanation = """
                    The sentence accurately reports the association and includes the 
                    study's main causal limitation.
                """.trimIndent(),
                learningTip = """
                    Good academic writing reports both the finding and the relevant limitation.
                """.trimIndent()
            )
        )
    }

    private fun createAdvancedCase(): EvidenceCase {
        return EvidenceCase(
            id = "advanced_focus_supplement_webpage",
            researchQuestion = """
                Does the FocusFuel supplement improve university students' academic performance?
            """.trimIndent(),
            title = """
                FocusFuel Improves Student Performance by 40%
            """.trimIndent(),
            authors = "FocusFuel Research Team",
            publication = "FocusFuel Product Website",
            publishedYear = 2026,
            excerpt = """
                FocusFuel states that students using its supplement reported a 
                40 percent improvement in concentration. The claim is based on 
                60 volunteers who chose whether to use the product. The company 
                funded the study and published the results on its own website. 
                Concentration was measured through participant self-ratings rather 
                than examination grades. The report did not control for previous 
                achievement, sleep, caffeine use or study time.
            """.trimIndent(),
            methodSummary = """
                Company-funded, non-randomised comparison using self-reported concentration
            """.trimIndent(),
            sampleSummary = """
                60 self-selected university student volunteers
            """.trimIndent(),
            sourceType = SourceType.COMMERCIAL_WEBPAGE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = createAdvancedQuestions()
        )
    }

    private fun createAdvancedQuestions(): List<EvaluationQuestion> {
        return listOf(
            EvaluationQuestion(
                id = "advanced_conflict",
                dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                prompt = """
                    Which feature creates the clearest potential conflict of interest?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "company_control",
                        text = """
                            The company funded the study and published the results on its product website
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "student_sample",
                        text = "The participants were university students"
                    ),
                    AnswerOption(
                        id = "recent_year",
                        text = "The webpage was published recently"
                    )
                ),
                correctOptionId = "company_control",
                explanation = """
                    The organisation selling the product also funded the study and 
                    controlled how the findings were presented. This does not automatically 
                    invalidate the evidence, but it increases the need for independent verification.
                """.trimIndent(),
                learningTip = """
                    Identify who funded, conducted and published the research.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "advanced_design",
                dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                prompt = """
                    What is the strongest methodological reason the study cannot establish that FocusFuel caused the reported improvement?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "self_selection",
                        text = """
                            Participants chose whether to use the product, so the groups may have differed before the study began
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "commercial_name",
                        text = """
                            The product has a commercial name
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "percentage",
                        text = """
                            The result was reported as a percentage
                        """.trimIndent()
                    )
                ),
                correctOptionId = "self_selection",
                explanation = """
                    Self-selection creates a serious risk that product users differed 
                    from non-users in motivation, health, study habits or other characteristics.
                """.trimIndent(),
                learningTip = """
                    Strong causal evidence requires comparable groups and control over important differences.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "advanced_alternatives",
                dimension = EvaluationDimension.ALTERNATIVE_EXPLANATIONS,
                prompt = """
                    Which alternative explanation most directly challenges the product's causal claim?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "other_factors",
                        text = """
                            Students who used the product may also have slept more, studied longer or consumed different amounts of caffeine
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "website_exists",
                        text = """
                            The company has a website
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "students_study",
                        text = """
                            University students normally complete assessments
                        """.trimIndent()
                    )
                ),
                correctOptionId = "other_factors",
                explanation = """
                    Sleep, study time, previous achievement and caffeine use could affect 
                    both product use and reported concentration.
                """.trimIndent(),
                learningTip = """
                    Look for uncontrolled variables that could explain the observed difference.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "advanced_measurement",
                dimension = EvaluationDimension.CITATION_SUPPORT,
                prompt = """
                    Why does the measurement fail to directly support the claim about academic performance?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "different_outcome",
                        text = """
                            The study measured self-rated concentration rather than examination results or academic performance
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "students_answered",
                        text = """
                            Students were asked questions
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "number_small",
                        text = """
                            The webpage includes a number
                        """.trimIndent()
                    )
                ),
                correctOptionId = "different_outcome",
                explanation = """
                    Improved self-rated concentration is not the same outcome as improved 
                    grades, examination performance or academic achievement.
                """.trimIndent(),
                learningTip = """
                    Check whether the evidence measured the exact outcome used in the claim.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "advanced_corroboration",
                dimension = EvaluationDimension.ALTERNATIVE_EXPLANATIONS,
                prompt = """
                    Which additional evidence would most strengthen the claim?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "independent_trial",
                        text = """
                            A preregistered, independently funded randomised trial measuring actual academic outcomes
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "more_advertising",
                        text = """
                            More positive statements on the company's website
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "influencer_review",
                        text = """
                            A paid influencer describing personal experience with the product
                        """.trimIndent()
                    )
                ),
                correctOptionId = "independent_trial",
                explanation = """
                    Independent funding, random assignment, preregistration and direct 
                    academic outcome measures would address several major weaknesses.
                """.trimIndent(),
                learningTip = """
                    Strong corroboration should be independent and methodologically stronger 
                    than the original evidence.
                """.trimIndent()
            ),
            EvaluationQuestion(
                id = "advanced_conclusion",
                dimension = EvaluationDimension.OVERCLAIMING,
                prompt = """
                    Which conclusion is most defensible after considering all the evidence limitations?
                """.trimIndent(),
                options = listOf(
                    AnswerOption(
                        id = "proven",
                        text = """
                            FocusFuel has been proven to improve university examination results by 40 percent
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "qualified",
                        text = """
                            A small self-selected group reported higher concentration, but the evidence does not establish that FocusFuel improves academic performance
                        """.trimIndent()
                    ),
                    AnswerOption(
                        id = "definitely_harmful",
                        text = """
                            FocusFuel definitely reduces academic performance
                        """.trimIndent()
                    )
                ),
                correctOptionId = "qualified",
                explanation = """
                    The qualified conclusion reports what was actually measured while 
                    acknowledging the design, measurement and conflict-of-interest limitations.
                """.trimIndent(),
                learningTip = """
                    The strongest academic conclusion is often the one that clearly states 
                    both the finding and its limits.
                """.trimIndent()
            )
        )
    }
}

