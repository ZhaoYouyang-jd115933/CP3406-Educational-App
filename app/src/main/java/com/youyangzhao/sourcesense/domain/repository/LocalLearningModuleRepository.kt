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
        createClaimsAndCitationsModule(),
        createResearchMethodModule(),
        createSampleQualityModule(),
        createGeneralisabilityModule(),
        createIntermediateEvidenceStrengthModule(),
        createBiasAndConflictsModule(),
        createConfoundingVariablesModule(),
        createStatisticalInterpretationModule(),
        createConflictingSourcesModule(),
        createSystematicReviewQualityModule(),
        createResearchEthicsModule()
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

    private fun createResearchMethodModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "intermediate_research_method_case",
            researchQuestion = """
                Does a study-planning app improve university students' examination performance?
            """.trimIndent(),
            title = "Testing a Digital Study-Planning App",
            authors = "L. Ng and T. Harris",
            publication = "Journal of Educational Technology",
            publishedYear = 2026,
            excerpt = """
                Researchers randomly assigned 120 university students to either use a
                study-planning app for six weeks or continue with their normal study
                practices. Both groups completed the same examination at the end.
            """.trimIndent(),
            methodSummary = "Randomised controlled experiment",
            sampleSummary = """
                120 university students randomly assigned to two groups
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "intermediate_research_method_question_1",
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        Which feature most strengthens this study's ability to examine causation?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "random_assignment",
                            text = """
                                Students were randomly assigned to an app group and a comparison group
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "recent_publication",
                            text = "The study was published recently"
                        ),
                        AnswerOption(
                            id = "academic_title",
                            text = "The article has an academic-sounding title"
                        )
                    ),
                    correctOptionId = "random_assignment",
                    explanation = """
                        Random assignment and a comparison group help reduce systematic
                        differences between the groups. This makes a causal interpretation
                        more reasonable than it would be in an observational survey.
                    """.trimIndent(),
                    learningTip = """
                        Match the research method to the claim. Causal questions usually
                        require stronger designs than descriptive or correlational questions.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "intermediate_research_method",
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            title = "Research Method",
            description = """
                Judge whether a study design is suitable for the research question.
            """.trimIndent(),
            learningFocus = """
                Connect experiments, surveys and observational designs with the claims they can support.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createSampleQualityModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "intermediate_sample_quality_case",
            researchQuestion = """
                How satisfied are university students with online learning?
            """.trimIndent(),
            title = "Student Satisfaction with Online Learning",
            authors = "M. Tan and R. Singh",
            publication = "Higher Education Research Notes",
            publishedYear = 2025,
            excerpt = """
                An online survey was shared with one business class. Eighty-five students
                voluntarily completed it. Most respondents reported being satisfied with
                online learning, and the authors discussed university students generally.
            """.trimIndent(),
            methodSummary = "Voluntary online survey",
            sampleSummary = "85 volunteers from one business class",
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "intermediate_sample_quality_question_1",
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        What is the most important limitation of this sample?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "narrow_volunteer_sample",
                            text = """
                                It is a voluntary sample from only one class and may not represent other students
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "too_many_students",
                            text = """
                                The study included too many participants to analyse properly
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "recent_data",
                            text = """
                                The data is unreliable because it was collected recently
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = "narrow_volunteer_sample",
                    explanation = """
                        Students from one business class may differ from students in other
                        programmes or universities. Voluntary participation may also attract
                        students with particularly strong opinions.
                    """.trimIndent(),
                    learningTip = """
                        Evaluate how participants were selected, who was excluded and whether
                        the sample represents the population in the research question.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "intermediate_sample_quality",
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            title = "Sample Quality",
            description = """
                Evaluate sample selection, representativeness and possible selection bias.
            """.trimIndent(),
            learningFocus = """
                Decide whether the study participants adequately represent the target population.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createGeneralisabilityModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "intermediate_generalisability_case",
            researchQuestion = """
                Does background music improve concentration among adults?
            """.trimIndent(),
            title = """
                Background Music and Concentration in First-Year Students
            """.trimIndent(),
            authors = "E. Wong and K. Patel",
            publication = "Cognitive Learning Journal",
            publishedYear = 2025,
            excerpt = """
                Sixty first-year psychology students from one university completed a
                concentration task while listening to instrumental music. Their scores were
                slightly higher than scores recorded during a silent task.
            """.trimIndent(),
            methodSummary = "Within-participant laboratory task",
            sampleSummary = """
                60 first-year psychology students from one university
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "intermediate_generalisability_question_1",
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        Which conclusion best respects the limits of this study?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "limited_conclusion",
                            text = """
                                Instrumental music was associated with slightly better task performance in this group of students
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "all_adults",
                            text = """
                                Background music improves concentration for all adults
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "all_tasks",
                            text = """
                                Music improves performance on every type of academic task
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = "limited_conclusion",
                    explanation = """
                        The participants came from one year level, one programme and one
                        university. The findings should not automatically be generalised to
                        all adults, institutions or concentration tasks.
                    """.trimIndent(),
                    learningTip = """
                        Keep conclusions within the population, setting and task that were
                        actually examined.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "intermediate_generalisability",
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            title = "Generalisability",
            description = """
                Decide how far a study's findings can reasonably be applied.
            """.trimIndent(),
            learningFocus = """
                Check whether conclusions extend beyond the study population, setting or conditions.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createIntermediateEvidenceStrengthModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "intermediate_evidence_strength_case",
            researchQuestion = """
                Do structured study-skills programmes improve academic performance?
            """.trimIndent(),
            title = """
                Study-Skills Programmes and Academic Performance: A Systematic Review
            """.trimIndent(),
            authors = "D. Roberts and S. Ibrahim",
            publication = "Review of Educational Research",
            publishedYear = 2026,
            excerpt = """
                The authors systematically searched four academic databases and reviewed
                18 controlled studies. They assessed study quality and found a small but
                generally consistent improvement in academic performance.
            """.trimIndent(),
            methodSummary = "Systematic review of controlled studies",
            sampleSummary = """
                18 controlled studies from multiple institutions
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "intermediate_evidence_strength_question_1",
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Why may this source provide stronger evidence than one small survey?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "multiple_studies",
                            text = """
                                It combines multiple controlled studies and evaluates their quality
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "long_title",
                            text = """
                                It has a longer title than a survey article
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "always_correct",
                            text = """
                                Systematic reviews are always correct and have no limitations
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = "multiple_studies",
                    explanation = """
                        A systematic review can compare results across several studies and
                        assess their quality. It may therefore provide broader evidence,
                        although its conclusions still depend on the included studies.
                    """.trimIndent(),
                    learningTip = """
                        Evidence strength depends on design quality, consistency, sample
                        coverage and the reliability of the underlying studies.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "intermediate_evidence_strength",
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            title = "Evidence Strength",
            description = """
                Compare evidence using study design, consistency and methodological quality.
            """.trimIndent(),
            learningFocus = """
                Judge evidence by how it was produced, not simply by the confidence of its wording.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createBiasAndConflictsModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "intermediate_bias_conflicts_case",
            researchQuestion = """
                Do energy drinks improve university students' concentration?
            """.trimIndent(),
            title = "Energy Drinks and Short-Term Concentration",
            authors = "P. Adams and C. Liu",
            publication = "Journal of Performance Nutrition",
            publishedYear = 2026,
            excerpt = """
                A controlled study reported improved concentration scores after participants
                consumed an energy drink. The study was funded by the drink manufacturer,
                and one author worked as a paid consultant for the company.
            """.trimIndent(),
            methodSummary = "Controlled short-term performance study",
            sampleSummary = "100 university student volunteers",
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "intermediate_bias_conflicts_question_1",
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        How should the funding and consultancy relationship affect the evaluation?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "scrutinise_conflict",
                            text = """
                                Treat them as potential conflicts and examine the methods, reporting and disclosure carefully
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "automatic_rejection",
                            text = """
                                Automatically reject the study without examining its methods
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "no_relevance",
                            text = """
                                Ignore them because funding can never influence research
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = "scrutinise_conflict",
                    explanation = """
                        Industry funding does not automatically make a study false, but it
                        creates a possible conflict of interest. Readers should examine the
                        study design, analysis, reporting and transparency more carefully.
                    """.trimIndent(),
                    learningTip = """
                        Look for funding sources, author relationships, selective reporting
                        and whether conflicts are clearly disclosed.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "intermediate_bias_conflicts",
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            title = "Bias and Conflicts",
            description = """
                Identify possible bias, funding influence and conflicts of interest.
            """.trimIndent(),
            learningFocus = """
                Consider how incentives and research decisions may influence reported findings.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createConfoundingVariablesModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "advanced_confounding_variables_case",
            researchQuestion = """
                Does attending optional tutorials cause higher examination scores?
            """.trimIndent(),
            title = """
                Tutorial Attendance and Examination Performance
            """.trimIndent(),
            authors = "K. Morgan and H. Zhang",
            publication = "Journal of University Learning",
            publishedYear = 2026,
            excerpt = """
                Researchers compared 420 students who attended optional tutorials with
                students who did not attend. Tutorial attendees achieved higher average
                examination scores. However, they also reported more weekly study hours
                and higher previous-semester grades.
            """.trimIndent(),
            methodSummary = "Observational comparison using student records and survey data",
            sampleSummary = "420 university students who chose whether to attend tutorials",
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "advanced_confounding_variables_question_1",
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        Why can this study not confidently conclude that tutorial attendance caused the higher scores?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "confounding_factors",
                            text = """
                                Tutorial attendees may already have stronger study habits and previous academic performance
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "large_sample",
                            text = """
                                The sample included too many students to support a conclusion
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "exam_measurement",
                            text = """
                                Examination scores cannot be used as an academic outcome
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = "confounding_factors",
                    explanation = """
                        Study hours and previous grades are possible confounding variables.
                        They may influence both tutorial attendance and examination results,
                        creating an apparent effect even if tutorials are not the sole cause.
                    """.trimIndent(),
                    learningTip = """
                        Look for variables that may influence both the proposed cause and
                        the measured outcome.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "advanced_confounding_variables",
            difficultyLevel = DifficultyLevel.ADVANCED,
            title = "Confounding Variables",
            description = """
                Identify third variables that may create a misleading causal relationship.
            """.trimIndent(),
            learningFocus = """
                Separate the effect of an intervention from pre-existing differences between groups.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createStatisticalInterpretationModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "advanced_statistical_interpretation_case",
            researchQuestion = """
                Does a study reminder system meaningfully improve student grades?
            """.trimIndent(),
            title = """
                Automated Study Reminders and Academic Results
            """.trimIndent(),
            authors = "S. Ali and M. Turner",
            publication = "Educational Data Science Review",
            publishedYear = 2026,
            excerpt = """
                A study involving 4,800 students found that users of an automated
                reminder system achieved examination scores that were 0.4 percentage
                points higher on average. The difference was statistically significant,
                with p less than 0.01.
            """.trimIndent(),
            methodSummary = "Large controlled comparison",
            sampleSummary = "4,800 university students",
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "advanced_statistical_interpretation_question_1",
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        What is the most accurate interpretation of this result?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "small_effect",
                            text = """
                                The difference is unlikely to be due to random sampling alone, but its practical effect is very small
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "major_improvement",
                            text = """
                                Statistical significance proves that the reminder system produced a major educational improvement
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "no_relationship",
                            text = """
                                A small percentage difference means that no relationship exists
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = "small_effect",
                    explanation = """
                        Statistical significance concerns whether an observed difference is
                        unlikely under a specified statistical model. It does not show that
                        the effect is large, important or educationally meaningful.
                    """.trimIndent(),
                    learningTip = """
                        Interpret statistical significance together with effect size,
                        confidence, study quality and practical importance.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "advanced_statistical_interpretation",
            difficultyLevel = DifficultyLevel.ADVANCED,
            title = "Statistical Interpretation",
            description = """
                Distinguish statistical significance from effect size and practical importance.
            """.trimIndent(),
            learningFocus = """
                Avoid treating a small statistically significant result as automatically meaningful.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createConflictingSourcesModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "advanced_conflicting_sources_case",
            researchQuestion = """
                Does laptop note-taking reduce university students' learning outcomes?
            """.trimIndent(),
            title = """
                Comparing Conflicting Studies of Digital Note-Taking
            """.trimIndent(),
            authors = "F. Wilson and Y. Park",
            publication = "Evidence in Higher Education",
            publishedYear = 2026,
            excerpt = """
                Study A surveyed 90 volunteers after one lecture and reported that
                students preferred laptops. Study B randomly assigned 360 students
                to laptop or handwritten note-taking across six lectures and found
                slightly better delayed test performance in the handwritten group.
            """.trimIndent(),
            methodSummary = """
                Comparison of a small voluntary survey and a larger randomised study
            """.trimIndent(),
            sampleSummary = """
                Study A: 90 volunteers; Study B: 360 randomly assigned students
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "advanced_conflicting_sources_question_1",
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which approach is most appropriate when evaluating these apparently conflicting findings?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "compare_methods",
                            text = """
                                Compare the questions, outcomes, samples and study designs before deciding whether the findings truly conflict
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "choose_preference",
                            text = """
                                Accept Study A because student preference is the most important evidence of learning
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "newest_source",
                            text = """
                                Accept whichever study was published more recently without examining its methods
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = "compare_methods",
                    explanation = """
                        The studies measured different outcomes. Study A examined preference,
                        while Study B measured delayed performance. Their methods and samples
                        also differed, so the findings are not directly interchangeable.
                    """.trimIndent(),
                    learningTip = """
                        When sources disagree, compare what they measured, who they studied,
                        how the evidence was produced and how precisely the conclusions were stated.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "advanced_conflicting_sources",
            difficultyLevel = DifficultyLevel.ADVANCED,
            title = "Comparing Conflicting Sources",
            description = """
                Evaluate why credible-looking studies may reach different conclusions.
            """.trimIndent(),
            learningFocus = """
                Compare research questions, outcomes, samples and methods instead of choosing a source by appearance.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createSystematicReviewQualityModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "advanced_systematic_review_quality_case",
            researchQuestion = """
                Do mindfulness programmes reduce stress among university students?
            """.trimIndent(),
            title = """
                Mindfulness and Student Stress: A Review of Published Evidence
            """.trimIndent(),
            authors = "N. Evans and J. Rahman",
            publication = "Student Mental Health Research",
            publishedYear = 2026,
            excerpt = """
                The review included 12 studies found through one academic database.
                The authors did not publish their search terms, inclusion criteria or
                assessment of study quality. Studies reporting no benefit were discussed
                briefly, while positive studies received more detailed attention.
            """.trimIndent(),
            methodSummary = "Narrative review described as a systematic review",
            sampleSummary = "12 published studies identified through one database",
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "advanced_systematic_review_quality_question_1",
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which issue most seriously weakens confidence in this review?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "untransparent_process",
                            text = """
                                The search, selection and quality-assessment process is not transparent or reproducible
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "study_count",
                            text = """
                                A review must always contain more than 100 studies to be valid
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "topic_popularity",
                            text = """
                                Mindfulness is a popular topic, so research about it cannot be evaluated
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = "untransparent_process",
                    explanation = """
                        Without clear search terms, inclusion rules and quality assessment,
                        readers cannot determine whether important studies were missed or
                        whether weak and strong evidence were treated appropriately.
                    """.trimIndent(),
                    learningTip = """
                        A strong systematic review should report a reproducible search,
                        explicit eligibility criteria and a structured assessment of study quality.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "advanced_systematic_review_quality",
            difficultyLevel = DifficultyLevel.ADVANCED,
            title = "Systematic Review Quality",
            description = """
                Evaluate the transparency and reliability of evidence-review methods.
            """.trimIndent(),
            learningFocus = """
                Check database coverage, search reporting, inclusion criteria and study-quality assessment.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }

    private fun createResearchEthicsModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = "advanced_research_ethics_case",
            researchQuestion = """
                How do financial difficulties affect university students' mental wellbeing?
            """.trimIndent(),
            title = """
                Financial Stress and Student Wellbeing
            """.trimIndent(),
            authors = "B. Carter and L. Hassan",
            publication = "Journal of Student Support",
            publishedYear = 2026,
            excerpt = """
                Researchers collected identifiable financial and mental-health information
                through an online survey. Participants were not clearly told how long their
                data would be stored. The research team shared the raw dataset with another
                organisation without describing a separate consent process.
            """.trimIndent(),
            methodSummary = "Online survey collecting sensitive identifiable information",
            sampleSummary = "650 university students",
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = """
                This is a fictional practice source created for educational use.
            """.trimIndent(),
            questions = listOf(
                EvaluationQuestion(
                    id = "advanced_research_ethics_question_1",
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        What is the most important ethical concern in this case?
                    """.trimIndent(),
                    options = listOf(
                        AnswerOption(
                            id = "consent_privacy",
                            text = """
                                Participants may not have given sufficiently informed consent for storing and sharing sensitive identifiable data
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "large_sample",
                            text = """
                                The study included too many participants for ethical approval
                            """.trimIndent()
                        ),
                        AnswerOption(
                            id = "online_method",
                            text = """
                                Online surveys are always unethical regardless of their safeguards
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = "consent_privacy",
                    explanation = """
                        Sensitive information requires clear consent, strong privacy
                        protections and transparent limits on storage and sharing.
                        Data collection methods are not automatically unethical, but their
                        safeguards must match the risks faced by participants.
                    """.trimIndent(),
                    learningTip = """
                        Examine informed consent, data minimisation, confidentiality,
                        participant risk and transparency about secondary data use.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = "advanced_research_ethics",
            difficultyLevel = DifficultyLevel.ADVANCED,
            title = "Research Ethics and Transparency",
            description = """
                Evaluate consent, privacy, participant risk and transparent data practices.
            """.trimIndent(),
            learningFocus = """
                Consider whether research protects participants and clearly explains how sensitive data will be used.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }
}

