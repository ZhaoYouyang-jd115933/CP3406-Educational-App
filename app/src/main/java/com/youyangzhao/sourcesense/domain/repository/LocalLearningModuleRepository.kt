package com.youyangzhao.sourcesense.data.repository

import com.youyangzhao.sourcesense.domain.model.AnswerOption
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvidenceCase
import com.youyangzhao.sourcesense.domain.model.LearningModule
import com.youyangzhao.sourcesense.domain.model.SourceType
import com.youyangzhao.sourcesense.domain.repository.LearningModuleRepository

class LocalLearningModuleRepository : LearningModuleRepository {

    private val learningModules = listOf(
        createRelevanceModule(),
        createSourceTypeModule(),
        createCredibilityModule(),
        createCausationModule(),
        createClaimsAndCitationsModule()
    )

    override suspend fun getModulesForDifficulty(
        difficultyLevel: DifficultyLevel
    ): List<LearningModule> {
        return learningModules.filter { module ->
            module.difficultyLevel == difficultyLevel
        }
    }

    override suspend fun getLearningModule(
        moduleId: String
    ): LearningModule? {
        return learningModules.firstOrNull { module ->
            module.id == moduleId
        }
    }

    private fun createRelevanceModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "beginner_relevance_case",
            researchQuestion = """
                Does regular exercise improve sleep quality among university students?
            """.trimIndent(),
            title = """
                Exercise Habits and Sleep Among University Students
            """.trimIndent(),
            authors = "J. Lim and P. Wong",
            publication = "Student Health Review",
            publishedYear = 2025,
            excerpt = """
                A survey of 250 university students examined weekly exercise 
                frequency and self-reported sleep quality. Students who exercised 
                more frequently generally reported better sleep.
            """.trimIndent(),
            methodSummary = "Cross-sectional student survey",
            sampleSummary = "250 university students",
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "beginner_relevance_question_1",
                    dimension = EvaluationDimension.RELEVANCE,
                    prompt = """
                        How relevant is this source to the research question?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "directly_relevant",
                            text = """
                                Directly relevant because it examines exercise and sleep among university students
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "unrelated",
                            text = """
                                Unrelated because it does not discuss university students
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "proves_causation",
                            text = """
                                Fully answers the question because it proves exercise causes better sleep
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = "directly_relevant",
                    explanation = """
                        The population and main variables match the research question. 
                        However, relevance does not mean that the survey proves causation.
                    """.trimIndent(),
                    learningTip = """
                        Compare the source population, variables and context with the 
                        research question.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "beginner_relevance",
            difficultyLevel = DifficultyLevel.BEGINNER,
            title = "Relevance",
            description = """
                Decide whether a source directly addresses a research question.
            """.trimIndent(),
            learningFocus = """
                Match the source population, variables and context to the research question.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createSourceTypeModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "beginner_source_type_case",
            researchQuestion = """
                What advice is available about managing examination stress?
            """.trimIndent(),
            title = "Five Ways to Manage Examination Stress",
            authors = "Campus Life Reporter",
            publication = "University News Online",
            publishedYear = 2026,
            excerpt = """
                A university news website summarised advice from a campus counsellor 
                about sleep, exercise and study planning during examinations.
            """.trimIndent(),
            methodSummary = "News report containing professional advice",
            sampleSummary = "No research sample was reported",
            sourceType = SourceType.NEWS_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "beginner_source_type_question_1",
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
                            text = "Commercial webpage"
                        )
                    ),
                    correctOptionId = "news",
                    explanation = """
                        The information was published by a university news website. 
                        It is not presented as an original peer-reviewed research study.
                    """.trimIndent(),
                    learningTip = """
                        Check the publication venue and whether the source reports original research.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "beginner_source_type",
            difficultyLevel = DifficultyLevel.BEGINNER,
            title = "Source Type",
            description = """
                Identify academic articles, news reports, blogs and commercial webpages.
            """.trimIndent(),
            learningFocus = """
                Recognise where information was published and what kind of source it is.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createCredibilityModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "beginner_credibility_case",
            researchQuestion = """
                Can herbal drinks improve student concentration?
            """.trimIndent(),
            title = "My Secret Drink for Perfect Concentration",
            authors = "StudyMaster99",
            publication = "Personal Study Blog",
            publishedYear = 2026,
            excerpt = """
                The writer claims that a homemade herbal drink doubled their 
                concentration. No qualifications, research references, measurements 
                or information about ingredients are provided.
            """.trimIndent(),
            methodSummary = "Personal experience",
            sampleSummary = "One unidentified blog author",
            sourceType = SourceType.BLOG_POST,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "beginner_credibility_question_1",
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which feature creates the greatest credibility concern?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "missing_evidence",
                            text = """
                                The author provides no qualifications, research evidence or measurement details
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "short_title",
                            text = "The title is relatively short"
                        ),
                        AnswerOption(
                            id = "recent_source",
                            text = "The source was published recently"
                        )
                    ),
                    correctOptionId = "missing_evidence",
                    explanation = """
                        The claim relies on one personal experience and provides no 
                        transparent evidence that can be independently checked.
                    """.trimIndent(),
                    learningTip = """
                        Check authorship, expertise, supporting evidence and transparency.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "beginner_basic_credibility",
            difficultyLevel = DifficultyLevel.BEGINNER,
            title = "Basic Credibility",
            description = """
                Check authorship, publication quality and evidence transparency.
            """.trimIndent(),
            learningFocus = """
                Identify basic warning signs before relying on a source.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createCausationModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "beginner_causation_case",
            researchQuestion = """
                Does social media use cause examination anxiety?
            """.trimIndent(),
            title = "Social Media Use and Examination Anxiety",
            authors = "A. Chen and N. Kumar",
            publication = "Journal of Student Behaviour",
            publishedYear = 2025,
            excerpt = """
                Students reporting more daily social media use also reported higher 
                examination anxiety. Both variables were measured through a survey 
                completed at one point in time.
            """.trimIndent(),
            methodSummary = "Cross-sectional survey",
            sampleSummary = "380 university students",
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "beginner_causation_question_1",
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        What conclusion is supported by this study?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "causal",
                            text = """
                                Social media use definitely causes examination anxiety
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "association",
                            text = """
                                Social media use and examination anxiety were associated
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "no_relationship",
                            text = """
                                The study found no relationship between the variables
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = "association",
                    explanation = """
                        The survey found that the variables occurred together, but it 
                        cannot establish which variable came first or prove causation.
                    """.trimIndent(),
                    learningTip = """
                        Correlation describes a relationship. Causation requires stronger evidence.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "beginner_correlation_causation",
            difficultyLevel = DifficultyLevel.BEGINNER,
            title = "Correlation vs Causation",
            description = """
                Distinguish an observed relationship from a proven causal effect.
            """.trimIndent(),
            learningFocus = """
                Avoid interpreting association as proof that one variable caused another.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createClaimsAndCitationsModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "beginner_claims_citations_case",
            researchQuestion = """
                Is late-night smartphone use related to poorer student sleep?
            """.trimIndent(),
            title = "Late-Night Smartphone Use and Student Sleep",
            authors = "R. Tan and S. Lee",
            publication = "Student Wellbeing Quarterly",
            publishedYear = 2025,
            excerpt = """
                In a survey of 310 students, heavier late-night smartphone use 
                was associated with poorer self-reported sleep quality. The authors 
                stated that the design could not prove causation.
            """.trimIndent(),
            methodSummary = "Cross-sectional self-report survey",
            sampleSummary = "310 university students",
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "beginner_claims_citations_question_1",
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        Which statement is most accurately supported by the source?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "supported",
                            text = """
                                In this survey, heavier late-night smartphone use was associated with poorer reported sleep
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "causal",
                            text = """
                                Smartphones have been proven to cause sleep disorders in all students
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "treatment",
                            text = """
                                Avoiding smartphones is a proven medical treatment for insomnia
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = "supported",
                    explanation = """
                        The accurate statement preserves the sample context and reports 
                        an association rather than an unsupported causal or medical claim.
                    """.trimIndent(),
                    learningTip = """
                        The wording of a citation-based claim must match the source's actual evidence.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "beginner_claims_citations",
            difficultyLevel = DifficultyLevel.BEGINNER,
            title = "Claims and Citations",
            description = """
                Identify overclaiming and decide whether a source supports a specific statement.
            """.trimIndent(),
            learningFocus = """
                Keep academic claims within the wording and limits of the original evidence.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }
}

