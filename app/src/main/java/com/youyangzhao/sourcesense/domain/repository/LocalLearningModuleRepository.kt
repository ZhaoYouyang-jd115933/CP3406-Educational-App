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

    // Reuse the same disclosure for every fictional learning case
    private val practiceSourceNote = """
        This is a fictional practice source created for educational use.
    """.trimIndent()

    // Keep modules grouped by difficulty for easier maintenance
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
        // Return only modules matching the selected difficulty
        return learningModules.filter { module ->
            module.difficultyLevel == difficultyLevel
        }
    }

    override suspend fun getLearningModule(
        moduleId: String
    ): LearningModule? {
        // Find the module selected from the learning screen
        return learningModules.firstOrNull { module ->
            module.id == moduleId
        }
    }

    // Beginner modules

    private fun createRelevanceModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = """
                beginner_relevance_case
            """.trimIndent(),
            researchQuestion = """
                Does regular exercise improve sleep quality among university students?
            """.trimIndent(),
            title = """
                Exercise Habits and Sleep Among University Students
            """.trimIndent(),
            authors = """
                J. Lim and P. Wong
            """.trimIndent(),
            publication = """
                Student Health Review
            """.trimIndent(),
            publishedYear = 2025,
            excerpt = """
                A survey of 250 university students examined weekly exercise frequency and self-reported
                sleep quality. Students who exercised more frequently generally reported better sleep.
            """.trimIndent(),
            methodSummary = """
                Cross-sectional student survey
            """.trimIndent(),
            sampleSummary = """
                250 university students
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        beginner_relevance_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.RELEVANCE,
                    prompt = """
                        How relevant is this source to the research question?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                directly_relevant
                            """.trimIndent(),
                            text = """
                                Directly relevant because it examines exercise and sleep among university students
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                unrelated
                            """.trimIndent(),
                            text = """
                                Unrelated because it does not discuss university students
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                proves_causation
                            """.trimIndent(),
                            text = """
                                Fully answers the question because it proves exercise causes better sleep
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        directly_relevant
                    """.trimIndent(),
                    explanation = """
                        The population and main variables match the research question. However, relevance does
                        not mean that the survey proves causation.
                    """.trimIndent(),
                    learningTip = """
                        Compare the source population, variables and context with the research question.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_relevance_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.RELEVANCE,
                    prompt = """
                        Which feature creates the strongest match between the source and the research question?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                matching_population_variables
                            """.trimIndent(),
                            text = """
                                It studies university students, exercise frequency and sleep quality
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                recent_year
                            """.trimIndent(),
                            text = """
                                It was published in 2025
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                two_authors
                            """.trimIndent(),
                            text = """
                                It has two listed authors
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        matching_population_variables
                    """.trimIndent(),
                    explanation = """
                        Relevance depends mainly on whether the source examines the same population and concepts
                        as the research question.
                    """.trimIndent(),
                    learningTip = """
                        Do not judge relevance from the publication year or number of authors alone.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_relevance_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.RELEVANCE,
                    prompt = """
                        What important part of the research question is not fully answered by this source?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                causal_effect
                            """.trimIndent(),
                            text = """
                                Whether regular exercise actually causes improved sleep quality
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                student_population
                            """.trimIndent(),
                            text = """
                                Whether university students were included
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                sleep_measure
                            """.trimIndent(),
                            text = """
                                Whether sleep was discussed at all
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        causal_effect
                    """.trimIndent(),
                    explanation = """
                        The source is relevant, but its cross-sectional survey only shows an association. It
                        does not establish that exercise caused the reported sleep difference.
                    """.trimIndent(),
                    learningTip = """
                        A source can be relevant without being sufficient to answer every part of a question.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_relevance_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.RELEVANCE,
                    prompt = """
                        Which additional source would best complement this evidence?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                exercise_trial
                            """.trimIndent(),
                            text = """
                                A controlled study testing an exercise programme and measuring student sleep over time
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                general_sleep_blog
                            """.trimIndent(),
                            text = """
                                A personal blog about feeling tired after examinations
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                sports_marketing_report
                            """.trimIndent(),
                            text = """
                                A market report about university sportswear sales
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        exercise_trial
                    """.trimIndent(),
                    explanation = """
                        A controlled study with repeated sleep measurements would remain closely relevant while
                        providing stronger evidence about change over time.
                    """.trimIndent(),
                    learningTip = """
                        Choose supporting sources that match the population, variables and purpose of the
                        research question.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                beginner_relevance
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.BEGINNER,
            title = """
                Relevance
            """.trimIndent(),
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
            id = """
                beginner_source_type_case
            """.trimIndent(),
            researchQuestion = """
                What advice is available about managing examination stress?
            """.trimIndent(),
            title = """
                Five Ways to Manage Examination Stress
            """.trimIndent(),
            authors = """
                Campus Life Reporter
            """.trimIndent(),
            publication = """
                University News Online
            """.trimIndent(),
            publishedYear = 2026,
            excerpt = """
                A university news website summarised advice from a campus counsellor about sleep,
                exercise and study planning during examinations.
            """.trimIndent(),
            methodSummary = """
                News report containing professional advice
            """.trimIndent(),
            sampleSummary = """
                No research sample was reported
            """.trimIndent(),
            sourceType = SourceType.NEWS_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        beginner_source_type_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.SOURCE_TYPE,
                    prompt = """
                        What type of source is presented?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                journal
                            """.trimIndent(),
                            text = """
                                Peer-reviewed journal article
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                news
                            """.trimIndent(),
                            text = """
                                News article
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                blog
                            """.trimIndent(),
                            text = """
                                Personal blog post
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                commercial
                            """.trimIndent(),
                            text = """
                                Commercial webpage
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        news
                    """.trimIndent(),
                    explanation = """
                        The information was published by a university news website. It is not presented as an
                        original peer-reviewed research study.
                    """.trimIndent(),
                    learningTip = """
                        Check the publication venue and whether the source reports original research.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_source_type_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.SOURCE_TYPE,
                    prompt = """
                        Which detail most clearly shows that this is not an original research article?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                summarised_advice
                            """.trimIndent(),
                            text = """
                                It summarises a counsellor's advice and reports no research method or sample
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                university_topic
                            """.trimIndent(),
                            text = """
                                It discusses university students
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                recent_publication
                            """.trimIndent(),
                            text = """
                                It was published in 2026
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        summarised_advice
                    """.trimIndent(),
                    explanation = """
                        Original research normally explains how data was collected and analysed. This source
                        presents professional advice through a news report instead.
                    """.trimIndent(),
                    learningTip = """
                        Look for a method, participants, data and analysis when identifying original research.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_source_type_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.SOURCE_TYPE,
                    prompt = """
                        What is the most appropriate use of this source in an assignment?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                practical_context
                            """.trimIndent(),
                            text = """
                                Use it for practical context or to describe publicly available advice
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                prove_effectiveness
                            """.trimIndent(),
                            text = """
                                Use it as proof that the suggested strategies improve stress outcomes
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                replace_research
                            """.trimIndent(),
                            text = """
                                Use it instead of academic research because it appears on a university website
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        practical_context
                    """.trimIndent(),
                    explanation = """
                        A news report can provide useful context and communicate expert advice, but it does not
                        by itself test whether the advice is effective.
                    """.trimIndent(),
                    learningTip = """
                        Match the source type to the claim you want it to support.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_source_type_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.SOURCE_TYPE,
                    prompt = """
                        Which source would provide stronger evidence about whether a stress-management strategy
                        works?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                controlled_study
                            """.trimIndent(),
                            text = """
                                A peer-reviewed controlled study measuring stress before and after the strategy
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                student_comment
                            """.trimIndent(),
                            text = """
                                An anonymous student comment below the news article
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                advertisement
                            """.trimIndent(),
                            text = """
                                An advertisement for a commercial study-planning service
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        controlled_study
                    """.trimIndent(),
                    explanation = """
                        A controlled study directly measures outcomes and reports a research method, making it
                        more suitable for evaluating effectiveness.
                    """.trimIndent(),
                    learningTip = """
                        Different source types answer different questions. Advice and evidence of effectiveness
                        are not the same.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                beginner_source_type
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.BEGINNER,
            title = """
                Source Type
            """.trimIndent(),
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
            id = """
                beginner_credibility_case
            """.trimIndent(),
            researchQuestion = """
                Can herbal drinks improve student concentration?
            """.trimIndent(),
            title = """
                My Secret Drink for Perfect Concentration
            """.trimIndent(),
            authors = """
                StudyMaster99
            """.trimIndent(),
            publication = """
                Personal Study Blog
            """.trimIndent(),
            publishedYear = 2026,
            excerpt = """
                The writer claims that a homemade herbal drink doubled their concentration. No
                qualifications, research references, measurements or information about ingredients are
                provided.
            """.trimIndent(),
            methodSummary = """
                Personal experience
            """.trimIndent(),
            sampleSummary = """
                One unidentified blog author
            """.trimIndent(),
            sourceType = SourceType.BLOG_POST,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        beginner_credibility_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which feature creates the greatest credibility concern?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                missing_evidence
                            """.trimIndent(),
                            text = """
                                The author provides no qualifications, research evidence or measurement details
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                short_title
                            """.trimIndent(),
                            text = """
                                The title is relatively short
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                recent_source
                            """.trimIndent(),
                            text = """
                                The source was published recently
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        missing_evidence
                    """.trimIndent(),
                    explanation = """
                        The claim relies on one personal experience and provides no transparent evidence that
                        can be independently checked.
                    """.trimIndent(),
                    learningTip = """
                        Check authorship, expertise, supporting evidence and transparency.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_credibility_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which addition would most improve the transparency of the claim?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                method_details
                            """.trimIndent(),
                            text = """
                                Clear ingredient details, concentration measurements and links to relevant research
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                more_confident_language
                            """.trimIndent(),
                            text = """
                                A stronger statement that the drink definitely works
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                more_images
                            """.trimIndent(),
                            text = """
                                Several attractive photographs of the drink
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        method_details
                    """.trimIndent(),
                    explanation = """
                        Transparent methods and supporting research allow readers to examine how the claim was
                        produced and whether it is credible.
                    """.trimIndent(),
                    learningTip = """
                        Evidence becomes easier to evaluate when methods, measures and sources are clearly
                        reported.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_credibility_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Why is one person's experience weak evidence for a general claim about students?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                single_anecdote
                            """.trimIndent(),
                            text = """
                                It may reflect coincidence, expectation or personal factors and may not apply to others
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                blog_format
                            """.trimIndent(),
                            text = """
                                All information published in a blog is automatically false
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                herbal_topic
                            """.trimIndent(),
                            text = """
                                Herbal products can never be studied scientifically
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        single_anecdote
                    """.trimIndent(),
                    explanation = """
                        A single anecdote cannot separate the drink's effect from other explanations and cannot
                        show whether the experience is typical.
                    """.trimIndent(),
                    learningTip = """
                        Avoid both blind trust and automatic rejection. Evaluate how the evidence was produced.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_credibility_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which conclusion is most responsible?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                needs_research
                            """.trimIndent(),
                            text = """
                                The blog presents an unverified personal claim that requires stronger research before it
                                can guide students
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                proven_effect
                            """.trimIndent(),
                            text = """
                                The drink has been proven to double concentration
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                definitely_fraud
                            """.trimIndent(),
                            text = """
                                The author is definitely dishonest because no qualifications are listed
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        needs_research
                    """.trimIndent(),
                    explanation = """
                        The source is not strong enough to establish effectiveness, but lack of evidence does
                        not prove deliberate dishonesty.
                    """.trimIndent(),
                    learningTip = """
                        Use cautious conclusions that reflect what the available evidence can and cannot
                        establish.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                beginner_basic_credibility
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.BEGINNER,
            title = """
                Basic Credibility
            """.trimIndent(),
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
            id = """
                beginner_causation_case
            """.trimIndent(),
            researchQuestion = """
                Does social media use cause examination anxiety?
            """.trimIndent(),
            title = """
                Social Media Use and Examination Anxiety
            """.trimIndent(),
            authors = """
                A. Chen and N. Kumar
            """.trimIndent(),
            publication = """
                Journal of Student Behaviour
            """.trimIndent(),
            publishedYear = 2025,
            excerpt = """
                Students reporting more daily social media use also reported higher examination anxiety.
                Both variables were measured through a survey completed at one point in time.
            """.trimIndent(),
            methodSummary = """
                Cross-sectional survey
            """.trimIndent(),
            sampleSummary = """
                380 university students
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        beginner_causation_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        What conclusion is supported by this study?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                causal
                            """.trimIndent(),
                            text = """
                                Social media use definitely causes examination anxiety
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                association
                            """.trimIndent(),
                            text = """
                                Social media use and examination anxiety were associated
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                no_relationship
                            """.trimIndent(),
                            text = """
                                The study found no relationship between the variables
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        association
                    """.trimIndent(),
                    explanation = """
                        The survey found that the variables occurred together, but it cannot establish which
                        variable came first or prove causation.
                    """.trimIndent(),
                    learningTip = """
                        Correlation describes a relationship. Causation requires stronger evidence.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_causation_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        Why can the study not establish which variable came first?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                single_time_point
                            """.trimIndent(),
                            text = """
                                Social media use and anxiety were measured at the same point in time
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                large_sample
                            """.trimIndent(),
                            text = """
                                The study included more than 300 students
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                journal_source
                            """.trimIndent(),
                            text = """
                                The findings were published in a journal
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        single_time_point
                    """.trimIndent(),
                    explanation = """
                        A cross-sectional design measures variables simultaneously, so it cannot show whether
                        social media use preceded anxiety or anxiety preceded heavier use.
                    """.trimIndent(),
                    learningTip = """
                        To support causation, evidence usually needs a clear time order.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_causation_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        Which alternative explanation is plausible?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                reverse_or_third_variable
                            """.trimIndent(),
                            text = """
                                Anxious students may use social media more, or another factor may affect both variables
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                no_measurement
                            """.trimIndent(),
                            text = """
                                The study did not measure either social media use or anxiety
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                automatic_cause
                            """.trimIndent(),
                            text = """
                                Any statistically observed relationship must be causal
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        reverse_or_third_variable
                    """.trimIndent(),
                    explanation = """
                        Reverse causation and third variables are both possible explanations for an observed
                        association.
                    """.trimIndent(),
                    learningTip = """
                        Ask whether the proposed outcome could influence the proposed cause or whether another
                        variable could influence both.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_causation_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        Which design would better test a causal effect?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                randomised_usage_limit
                            """.trimIndent(),
                            text = """
                                Randomly assign students to a social-media reduction programme or a comparison group and
                                track anxiety
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                larger_cross_sectional_survey
                            """.trimIndent(),
                            text = """
                                Survey more students once using the same questions
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                opinion_poll
                            """.trimIndent(),
                            text = """
                                Ask students whether they believe social media causes anxiety
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        randomised_usage_limit
                    """.trimIndent(),
                    explanation = """
                        Random assignment and repeated outcome measurement would provide stronger evidence about
                        whether changing social media use changes anxiety.
                    """.trimIndent(),
                    learningTip = """
                        Stronger causal designs actively compare conditions and measure outcomes over time.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                beginner_correlation_causation
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.BEGINNER,
            title = """
                Correlation vs Causation
            """.trimIndent(),
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
            id = """
                beginner_claims_citations_case
            """.trimIndent(),
            researchQuestion = """
                Is late-night smartphone use related to poorer student sleep?
            """.trimIndent(),
            title = """
                Late-Night Smartphone Use and Student Sleep
            """.trimIndent(),
            authors = """
                R. Tan and S. Lee
            """.trimIndent(),
            publication = """
                Student Wellbeing Quarterly
            """.trimIndent(),
            publishedYear = 2025,
            excerpt = """
                In a survey of 310 students, heavier late-night smartphone use was associated with
                poorer self-reported sleep quality. The authors stated that the design could not prove
                causation.
            """.trimIndent(),
            methodSummary = """
                Cross-sectional self-report survey
            """.trimIndent(),
            sampleSummary = """
                310 university students
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        beginner_claims_citations_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        Which statement is most accurately supported by the source?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                supported
                            """.trimIndent(),
                            text = """
                                In this survey, heavier late-night smartphone use was associated with poorer reported
                                sleep
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                causal
                            """.trimIndent(),
                            text = """
                                Smartphones have been proven to cause sleep disorders in all students
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                treatment
                            """.trimIndent(),
                            text = """
                                Avoiding smartphones is a proven medical treatment for insomnia
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        supported
                    """.trimIndent(),
                    explanation = """
                        The accurate statement preserves the sample context and reports an association rather
                        than an unsupported causal or medical claim.
                    """.trimIndent(),
                    learningTip = """
                        The wording of a citation-based claim must match the source's actual evidence.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_claims_citations_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        Which part of the source should be preserved when paraphrasing it?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                association_and_context
                            """.trimIndent(),
                            text = """
                                That the finding was an association based on self-reports from a student survey
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                universal_effect
                            """.trimIndent(),
                            text = """
                                That the result applies to every student
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                medical_diagnosis
                            """.trimIndent(),
                            text = """
                                That participants were clinically diagnosed with sleep disorders
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        association_and_context
                    """.trimIndent(),
                    explanation = """
                        A faithful paraphrase keeps the study population, measurement method and non-causal
                        wording.
                    """.trimIndent(),
                    learningTip = """
                        Do not remove limitations that materially change the meaning of a source.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_claims_citations_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        Which sentence overstates the evidence?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                overstated_claim
                            """.trimIndent(),
                            text = """
                                Late-night smartphone use directly causes insomnia in university students
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                careful_claim
                            """.trimIndent(),
                            text = """
                                The survey found a relationship between heavier late-night use and poorer reported sleep
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                limited_claim
                            """.trimIndent(),
                            text = """
                                The study did not establish a causal effect
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        overstated_claim
                    """.trimIndent(),
                    explanation = """
                        The source measured an association and did not diagnose insomnia or prove a direct
                        causal effect.
                    """.trimIndent(),
                    learningTip = """
                        Watch for words such as causes, proves, always and all when the source uses more limited
                        language.
                    """.trimIndent()
                ),
                question(
                    id = """
                        beginner_claims_citations_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        Which citation use is most academically responsible?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                qualified_use
                            """.trimIndent(),
                            text = """
                                Use the study as evidence of an observed association and note its self-report and cross-
                                sectional limits
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                headline_only
                            """.trimIndent(),
                            text = """
                                Cite only the article title and ignore the method
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                unsupported_advice
                            """.trimIndent(),
                            text = """
                                Use the study to prescribe treatment for sleep disorders
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        qualified_use
                    """.trimIndent(),
                    explanation = """
                        Responsible citation use accurately represents the finding while acknowledging the
                        design limits that affect interpretation.
                    """.trimIndent(),
                    learningTip = """
                        A strong citation supports a precise claim rather than a broader claim that the source
                        never tested.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                beginner_claims_citations
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.BEGINNER,
            title = """
                Claims and Citations
            """.trimIndent(),
            description = """
                Identify overclaiming and decide whether a source supports a specific statement.
            """.trimIndent(),
            learningFocus = """
                Keep academic claims within the wording and limits of the original evidence.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }



    // Intermediate modules

    private fun createResearchMethodModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = """
                intermediate_research_method_case
            """.trimIndent(),
            researchQuestion = """
                Does a study-planning app improve university students' examination performance?
            """.trimIndent(),
            title = """
                Testing a Digital Study-Planning App
            """.trimIndent(),
            authors = """
                L. Ng and T. Harris
            """.trimIndent(),
            publication = """
                Journal of Educational Technology
            """.trimIndent(),
            publishedYear = 2026,
            excerpt = """
                Researchers randomly assigned 120 university students to either use a study-planning app
                for six weeks or continue with their normal study practices. Both groups completed the
                same examination at the end.
            """.trimIndent(),
            methodSummary = """
                Randomised controlled experiment
            """.trimIndent(),
            sampleSummary = """
                120 university students randomly assigned to two groups
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        intermediate_research_method_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        Which feature most strengthens this study's ability to examine causation?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                random_assignment
                            """.trimIndent(),
                            text = """
                                Students were randomly assigned to an app group and a comparison group
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                recent_publication
                            """.trimIndent(),
                            text = """
                                The study was published recently
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                academic_title
                            """.trimIndent(),
                            text = """
                                The article has an academic-sounding title
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        random_assignment
                    """.trimIndent(),
                    explanation = """
                        Random assignment and a comparison group help reduce systematic differences between the
                        groups. This makes a causal interpretation more reasonable than it would be in an
                        observational survey.
                    """.trimIndent(),
                    learningTip = """
                        Match the research method to the claim. Causal questions usually require stronger
                        designs than descriptive or correlational questions.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_research_method_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        Why is the comparison group important?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                estimate_counterfactual
                            """.trimIndent(),
                            text = """
                                It helps estimate what may have happened without using the app
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                increase_title_quality
                            """.trimIndent(),
                            text = """
                                It makes the article title more convincing
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                guarantee_no_bias
                            """.trimIndent(),
                            text = """
                                It guarantees that every possible source of bias has been removed
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        estimate_counterfactual
                    """.trimIndent(),
                    explanation = """
                        The comparison group provides a benchmark for changes that might occur through normal
                        study, time or other influences.
                    """.trimIndent(),
                    learningTip = """
                        A causal study needs a credible comparison for the outcome that would occur without the
                        intervention.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_research_method_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which limitation could still affect the interpretation of this experiment?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                adherence_and_scope
                            """.trimIndent(),
                            text = """
                                Students may not use the app as intended, and the sample may not represent other
                                universities
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                randomisation_invalid
                            """.trimIndent(),
                            text = """
                                Random assignment makes all research findings invalid
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                same_exam_problem
                            """.trimIndent(),
                            text = """
                                Using the same examination for both groups prevents comparison
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        adherence_and_scope
                    """.trimIndent(),
                    explanation = """
                        Randomisation strengthens internal validity, but non-adherence and a limited sample can
                        still affect the estimated effect and its generalisability.
                    """.trimIndent(),
                    learningTip = """
                        A strong method reduces important threats; it does not eliminate every limitation.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_research_method_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        If the app group scored higher, which conclusion would be most appropriate?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                bounded_causal_claim
                            """.trimIndent(),
                            text = """
                                Under the study conditions, assignment to the app group improved average examination
                                performance
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                universal_success
                            """.trimIndent(),
                            text = """
                                The app will improve every student's grades in every course
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                no_conclusion
                            """.trimIndent(),
                            text = """
                                Randomised experiments can never support causal conclusions
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        bounded_causal_claim
                    """.trimIndent(),
                    explanation = """
                        The experiment can support a causal conclusion within its design and sample, but it does
                        not justify a universal claim.
                    """.trimIndent(),
                    learningTip = """
                        Even strong designs require conclusions that stay within the tested population and
                        conditions.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                intermediate_research_method
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            title = """
                Research Method
            """.trimIndent(),
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
            id = """
                intermediate_sample_quality_case
            """.trimIndent(),
            researchQuestion = """
                How satisfied are university students with online learning?
            """.trimIndent(),
            title = """
                Student Satisfaction with Online Learning
            """.trimIndent(),
            authors = """
                M. Tan and R. Singh
            """.trimIndent(),
            publication = """
                Higher Education Research Notes
            """.trimIndent(),
            publishedYear = 2025,
            excerpt = """
                An online survey was shared with one business class. Eighty-five students voluntarily
                completed it. Most respondents reported being satisfied with online learning, and the
                authors discussed university students generally.
            """.trimIndent(),
            methodSummary = """
                Voluntary online survey
            """.trimIndent(),
            sampleSummary = """
                85 volunteers from one business class
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        intermediate_sample_quality_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        What is the most important limitation of this sample?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                narrow_volunteer_sample
                            """.trimIndent(),
                            text = """
                                It is a voluntary sample from only one class and may not represent other students
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                too_many_students
                            """.trimIndent(),
                            text = """
                                The study included too many participants to analyse properly
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                recent_data
                            """.trimIndent(),
                            text = """
                                The data is unreliable because it was collected recently
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        narrow_volunteer_sample
                    """.trimIndent(),
                    explanation = """
                        Students from one business class may differ from students in other programmes or
                        universities. Voluntary participation may also attract students with particularly strong
                        opinions.
                    """.trimIndent(),
                    learningTip = """
                        Evaluate how participants were selected, who was excluded and whether the sample
                        represents the population in the research question.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_sample_quality_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        What type of selection problem is most plausible?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                volunteer_bias
                            """.trimIndent(),
                            text = """
                                Students with especially positive or negative experiences may be more likely to respond
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                measurement_perfection
                            """.trimIndent(),
                            text = """
                                Voluntary participation guarantees perfectly accurate answers
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                random_selection
                            """.trimIndent(),
                            text = """
                                Every university student had an equal chance of selection
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        volunteer_bias
                    """.trimIndent(),
                    explanation = """
                        Self-selection can produce a respondent group that differs systematically from students
                        who choose not to participate.
                    """.trimIndent(),
                    learningTip = """
                        Ask whether the decision to participate could be related to the outcome being measured.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_sample_quality_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which group is least represented by this sample?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                other_programmes_and_nonrespondents
                            """.trimIndent(),
                            text = """
                                Students from other programmes, other universities and students who did not volunteer
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                business_volunteers
                            """.trimIndent(),
                            text = """
                                Students in the selected business class who completed the survey
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                survey_authors
                            """.trimIndent(),
                            text = """
                                The two researchers who wrote the article
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        other_programmes_and_nonrespondents
                    """.trimIndent(),
                    explanation = """
                        The sample directly represents only volunteers from one class, leaving major parts of
                        the target population unobserved.
                    """.trimIndent(),
                    learningTip = """
                        Representativeness depends on coverage as well as sample size.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_sample_quality_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which sampling plan would best improve representativeness?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                stratified_random_sample
                            """.trimIndent(),
                            text = """
                                Randomly sample students across programmes, year levels and universities
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                same_class_more_reminders
                            """.trimIndent(),
                            text = """
                                Send more reminders only to the same business class
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                social_media_poll
                            """.trimIndent(),
                            text = """
                                Post an open poll and accept responses from anyone who sees it
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        stratified_random_sample
                    """.trimIndent(),
                    explanation = """
                        Sampling across relevant subgroups gives more of the target population a planned chance
                        of inclusion and reduces dependence on one class.
                    """.trimIndent(),
                    learningTip = """
                        Improve a sample by matching the selection strategy to the diversity of the target
                        population.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                intermediate_sample_quality
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            title = """
                Sample Quality
            """.trimIndent(),
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
            id = """
                intermediate_generalisability_case
            """.trimIndent(),
            researchQuestion = """
                Does background music improve concentration among adults?
            """.trimIndent(),
            title = """
                Background Music and Concentration in First-Year Students
            """.trimIndent(),
            authors = """
                E. Wong and K. Patel
            """.trimIndent(),
            publication = """
                Cognitive Learning Journal
            """.trimIndent(),
            publishedYear = 2025,
            excerpt = """
                Sixty first-year psychology students from one university completed a concentration task
                while listening to instrumental music. Their scores were slightly higher than scores
                recorded during a silent task.
            """.trimIndent(),
            methodSummary = """
                Within-participant laboratory task
            """.trimIndent(),
            sampleSummary = """
                60 first-year psychology students from one university
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        intermediate_generalisability_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        Which conclusion best respects the limits of this study?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                limited_conclusion
                            """.trimIndent(),
                            text = """
                                Instrumental music was associated with slightly better task performance in this group of
                                students
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                all_adults
                            """.trimIndent(),
                            text = """
                                Background music improves concentration for all adults
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                all_tasks
                            """.trimIndent(),
                            text = """
                                Music improves performance on every type of academic task
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        limited_conclusion
                    """.trimIndent(),
                    explanation = """
                        The participants came from one year level, one programme and one university. The
                        findings should not automatically be generalised to all adults, institutions or
                        concentration tasks.
                    """.trimIndent(),
                    learningTip = """
                        Keep conclusions within the population, setting and task that were actually examined.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_generalisability_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        Why is generalising the result to all adults risky?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                narrow_population
                            """.trimIndent(),
                            text = """
                                The sample contains only first-year psychology students from one university
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                instrumental_music
                            """.trimIndent(),
                            text = """
                                Instrumental music can never be used in research
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                within_participant
                            """.trimIndent(),
                            text = """
                                Within-participant studies cannot produce any useful evidence
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        narrow_population
                    """.trimIndent(),
                    explanation = """
                        Age, education, occupation, culture and prior music preferences may differ substantially
                        between this student sample and adults generally.
                    """.trimIndent(),
                    learningTip = """
                        External validity weakens when the study sample is much narrower than the population in
                        the research question.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_generalisability_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        Which feature limits generalisation across different kinds of concentration?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                single_task
                            """.trimIndent(),
                            text = """
                                The study used one laboratory concentration task
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                score_difference
                            """.trimIndent(),
                            text = """
                                The music condition produced slightly higher scores
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                publication_year
                            """.trimIndent(),
                            text = """
                                The source was published in 2025
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        single_task
                    """.trimIndent(),
                    explanation = """
                        Performance on one laboratory task may not represent reading, problem-solving, workplace
                        attention or other forms of concentration.
                    """.trimIndent(),
                    learningTip = """
                        Generalisation concerns settings and tasks as well as people.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_generalisability_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which follow-up study would best test broader generalisability?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                diverse_multitask_sample
                            """.trimIndent(),
                            text = """
                                Recruit adults from varied backgrounds and test several realistic concentration tasks
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                repeat_same_group
                            """.trimIndent(),
                            text = """
                                Repeat the same task with the same psychology students
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                ask_preferences_only
                            """.trimIndent(),
                            text = """
                                Ask whether people like instrumental music without measuring concentration
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        diverse_multitask_sample
                    """.trimIndent(),
                    explanation = """
                        A more diverse sample and multiple realistic tasks would test whether the finding holds
                        beyond the original population and setting.
                    """.trimIndent(),
                    learningTip = """
                        To support broader claims, vary the people, contexts and outcomes being studied.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                intermediate_generalisability
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            title = """
                Generalisability
            """.trimIndent(),
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
            id = """
                intermediate_evidence_strength_case
            """.trimIndent(),
            researchQuestion = """
                Do structured study-skills programmes improve academic performance?
            """.trimIndent(),
            title = """
                Study-Skills Programmes and Academic Performance: A Systematic Review
            """.trimIndent(),
            authors = """
                D. Roberts and S. Ibrahim
            """.trimIndent(),
            publication = """
                Review of Educational Research
            """.trimIndent(),
            publishedYear = 2026,
            excerpt = """
                The authors systematically searched four academic databases and reviewed 18 controlled
                studies. They assessed study quality and found a small but generally consistent
                improvement in academic performance.
            """.trimIndent(),
            methodSummary = """
                Systematic review of controlled studies
            """.trimIndent(),
            sampleSummary = """
                18 controlled studies from multiple institutions
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        intermediate_evidence_strength_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Why may this source provide stronger evidence than one small survey?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                multiple_studies
                            """.trimIndent(),
                            text = """
                                It combines multiple controlled studies and evaluates their quality
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                long_title
                            """.trimIndent(),
                            text = """
                                It has a longer title than a survey article
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                always_correct
                            """.trimIndent(),
                            text = """
                                Systematic reviews are always correct and have no limitations
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        multiple_studies
                    """.trimIndent(),
                    explanation = """
                        A systematic review can compare results across several studies and assess their quality.
                        It may therefore provide broader evidence, although its conclusions still depend on the
                        included studies.
                    """.trimIndent(),
                    learningTip = """
                        Evidence strength depends on design quality, consistency, sample coverage and the
                        reliability of the underlying studies.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_evidence_strength_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Why is assessing the quality of the included studies important?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                weight_reliability
                            """.trimIndent(),
                            text = """
                                Weak studies should not be treated as equally reliable as well-designed studies
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                increase_study_count
                            """.trimIndent(),
                            text = """
                                Quality assessment automatically increases the number of included studies
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                remove_all_uncertainty
                            """.trimIndent(),
                            text = """
                                Quality assessment removes every possible source of uncertainty
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        weight_reliability
                    """.trimIndent(),
                    explanation = """
                        A review is only as trustworthy as the evidence it includes and the way differences in
                        quality are handled.
                    """.trimIndent(),
                    learningTip = """
                        Do not judge a review only by how many studies it contains.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_evidence_strength_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which limitation can still affect this review?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                included_study_limits
                            """.trimIndent(),
                            text = """
                                Its conclusion depends on the methods, reporting and availability of the included
                                studies
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                systematic_label
                            """.trimIndent(),
                            text = """
                                Calling it a systematic review makes limitations impossible
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                multiple_institutions
                            """.trimIndent(),
                            text = """
                                Including multiple institutions automatically weakens evidence
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        included_study_limits
                    """.trimIndent(),
                    explanation = """
                        Publication bias, inconsistent measures and weaknesses in the underlying studies can
                        still affect the review's conclusion.
                    """.trimIndent(),
                    learningTip = """
                        Synthesised evidence does not escape the limitations of its evidence base.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_evidence_strength_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        Which overall conclusion best matches the evidence?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                small_consistent_benefit
                            """.trimIndent(),
                            text = """
                                Controlled studies suggest a small, generally consistent improvement from structured
                                study-skills programmes
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                guaranteed_success
                            """.trimIndent(),
                            text = """
                                Every student will achieve substantially higher grades after any study-skills programme
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                no_evidence
                            """.trimIndent(),
                            text = """
                                A small effect means the review found no evidence at all
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        small_consistent_benefit
                    """.trimIndent(),
                    explanation = """
                        The conclusion should preserve both the generally consistent direction and the small
                        size of the reported improvement.
                    """.trimIndent(),
                    learningTip = """
                        Evidence strength and effect size are related questions, but they are not the same
                        question.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                intermediate_evidence_strength
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            title = """
                Evidence Strength
            """.trimIndent(),
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
            id = """
                intermediate_bias_conflicts_case
            """.trimIndent(),
            researchQuestion = """
                Do energy drinks improve university students' concentration?
            """.trimIndent(),
            title = """
                Energy Drinks and Short-Term Concentration
            """.trimIndent(),
            authors = """
                P. Adams and C. Liu
            """.trimIndent(),
            publication = """
                Journal of Performance Nutrition
            """.trimIndent(),
            publishedYear = 2026,
            excerpt = """
                A controlled study reported improved concentration scores after participants consumed an
                energy drink. The study was funded by the drink manufacturer, and one author worked as a
                paid consultant for the company.
            """.trimIndent(),
            methodSummary = """
                Controlled short-term performance study
            """.trimIndent(),
            sampleSummary = """
                100 university student volunteers
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        intermediate_bias_conflicts_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        How should the funding and consultancy relationship affect the evaluation?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                scrutinise_conflict
                            """.trimIndent(),
                            text = """
                                Treat them as potential conflicts and examine the methods, reporting and disclosure
                                carefully
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                automatic_rejection
                            """.trimIndent(),
                            text = """
                                Automatically reject the study without examining its methods
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                no_relevance
                            """.trimIndent(),
                            text = """
                                Ignore them because funding can never influence research
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        scrutinise_conflict
                    """.trimIndent(),
                    explanation = """
                        Industry funding does not automatically make a study false, but it creates a possible
                        conflict of interest. Readers should examine the study design, analysis, reporting and
                        transparency more carefully.
                    """.trimIndent(),
                    learningTip = """
                        Look for funding sources, author relationships, selective reporting and whether
                        conflicts are clearly disclosed.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_bias_conflicts_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which research decision could be influenced by a conflict of interest?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                selective_outcomes
                            """.trimIndent(),
                            text = """
                                Choosing favourable outcomes or emphasising positive analyses while downplaying negative
                                results
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                student_sample
                            """.trimIndent(),
                            text = """
                                Including university students in a study about university students
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                reporting_year
                            """.trimIndent(),
                            text = """
                                Writing the publication year in the article
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        selective_outcomes
                    """.trimIndent(),
                    explanation = """
                        Financial incentives may influence design, outcome selection, analysis choices or the
                        way results are presented.
                    """.trimIndent(),
                    learningTip = """
                        Bias can enter at several stages, not only when the final conclusion is written.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_bias_conflicts_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which practice would most reduce concern about selective reporting?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                preregister_and_share
                            """.trimIndent(),
                            text = """
                                Preregister outcomes and analysis plans, disclose funding and report all planned results
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                hide_funding
                            """.trimIndent(),
                            text = """
                                Remove the funding statement from the article
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                stronger_headline
                            """.trimIndent(),
                            text = """
                                Use a more confident headline about the drink's benefits
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        preregister_and_share
                    """.trimIndent(),
                    explanation = """
                        Preregistration and transparent reporting make it harder to change outcomes or analyses
                        after seeing the results.
                    """.trimIndent(),
                    learningTip = """
                        Transparency does not remove every conflict, but it makes research decisions easier to
                        examine.
                    """.trimIndent()
                ),
                question(
                    id = """
                        intermediate_bias_conflicts_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        Which conclusion is most responsible?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                cautious_interpretation
                            """.trimIndent(),
                            text = """
                                The study may provide useful evidence, but the short-term design and disclosed conflicts
                                require cautious interpretation
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                manufacturer_proof
                            """.trimIndent(),
                            text = """
                                Manufacturer funding proves that the reported effect is false
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                ignore_conflict
                            """.trimIndent(),
                            text = """
                                The finding should be accepted without qualification because the study was controlled
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        cautious_interpretation
                    """.trimIndent(),
                    explanation = """
                        The methods and conflicts should both be considered. Neither automatic acceptance nor
                        automatic rejection is justified.
                    """.trimIndent(),
                    learningTip = """
                        Evaluate the evidence and the incentives together.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                intermediate_bias_conflicts
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.INTERMEDIATE,
            title = """
                Bias and Conflicts
            """.trimIndent(),
            description = """
                Identify possible bias, funding influence and conflicts of interest.
            """.trimIndent(),
            learningFocus = """
                Consider how incentives and research decisions may influence reported findings.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }



    // Advanced modules

    private fun createConfoundingVariablesModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = """
                advanced_confounding_variables_case
            """.trimIndent(),
            researchQuestion = """
                Does attending optional tutorials cause higher examination scores?
            """.trimIndent(),
            title = """
                Tutorial Attendance and Examination Performance
            """.trimIndent(),
            authors = """
                K. Morgan and H. Zhang
            """.trimIndent(),
            publication = """
                Journal of University Learning
            """.trimIndent(),
            publishedYear = 2026,
            excerpt = """
                Researchers compared 420 students who attended optional tutorials with students who did
                not attend. Tutorial attendees achieved higher average examination scores. However, they
                also reported more weekly study hours and higher previous-semester grades.
            """.trimIndent(),
            methodSummary = """
                Observational comparison using student records and survey data
            """.trimIndent(),
            sampleSummary = """
                420 university students who chose whether to attend tutorials
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        advanced_confounding_variables_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        Why can this study not confidently conclude that tutorial attendance caused the higher
                        scores?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                confounding_factors
                            """.trimIndent(),
                            text = """
                                Tutorial attendees may already have stronger study habits and previous academic
                                performance
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                large_sample
                            """.trimIndent(),
                            text = """
                                The sample included too many students to support a conclusion
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                exam_measurement
                            """.trimIndent(),
                            text = """
                                Examination scores cannot be used as an academic outcome
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        confounding_factors
                    """.trimIndent(),
                    explanation = """
                        Study hours and previous grades are possible confounding variables. They may influence
                        both tutorial attendance and examination results, creating an apparent effect even if
                        tutorials are not the sole cause.
                    """.trimIndent(),
                    learningTip = """
                        Look for variables that may influence both the proposed cause and the measured outcome.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_confounding_variables_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        Which variable is the clearest potential confounder?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                previous_grades
                            """.trimIndent(),
                            text = """
                                Previous-semester grades
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                publication_title
                            """.trimIndent(),
                            text = """
                                The title of the article
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                sample_size
                            """.trimIndent(),
                            text = """
                                The number 420 by itself
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        previous_grades
                    """.trimIndent(),
                    explanation = """
                        Previous grades may predict both a student's likelihood of attending optional tutorials
                        and their later examination performance.
                    """.trimIndent(),
                    learningTip = """
                        A confounder must be plausibly connected to both exposure and outcome.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_confounding_variables_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        Why would statistically adjusting for study hours and previous grades still not prove
                        causation?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                unmeasured_confounding
                            """.trimIndent(),
                            text = """
                                Other unmeasured differences and self-selection may remain
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                adjustment_never_useful
                            """.trimIndent(),
                            text = """
                                Statistical adjustment is never useful in observational research
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                grades_invalid
                            """.trimIndent(),
                            text = """
                                Previous grades cannot be included in any analysis
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        unmeasured_confounding
                    """.trimIndent(),
                    explanation = """
                        Adjustment can reduce measured confounding, but it cannot guarantee that every relevant
                        difference was measured accurately and modelled correctly.
                    """.trimIndent(),
                    learningTip = """
                        Observational adjustment strengthens an analysis without turning it automatically into a
                        randomised experiment.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_confounding_variables_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.CAUSATION,
                    prompt = """
                        Which design would most directly reduce self-selection into tutorial attendance?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                random_assignment_tutorial
                            """.trimIndent(),
                            text = """
                                Randomly assign eligible students to tutorial access or a comparison condition
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                more_observational_students
                            """.trimIndent(),
                            text = """
                                Observe a larger group who still choose whether to attend
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                ask_motivation_after_exam
                            """.trimIndent(),
                            text = """
                                Ask students after the examination whether they felt motivated
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        random_assignment_tutorial
                    """.trimIndent(),
                    explanation = """
                        Random assignment reduces systematic pre-existing differences between attendance groups
                        and strengthens causal inference.
                    """.trimIndent(),
                    learningTip = """
                        When feasible and ethical, randomisation is a strong response to confounding by self-
                        selection.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                advanced_confounding_variables
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.ADVANCED,
            title = """
                Confounding Variables
            """.trimIndent(),
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
            id = """
                advanced_statistical_interpretation_case
            """.trimIndent(),
            researchQuestion = """
                Does a study reminder system meaningfully improve student grades?
            """.trimIndent(),
            title = """
                Automated Study Reminders and Academic Results
            """.trimIndent(),
            authors = """
                S. Ali and M. Turner
            """.trimIndent(),
            publication = """
                Educational Data Science Review
            """.trimIndent(),
            publishedYear = 2026,
            excerpt = """
                A study involving 4,800 students found that users of an automated reminder system
                achieved examination scores that were 0.4 percentage points higher on average. The
                difference was statistically significant, with p less than 0.01.
            """.trimIndent(),
            methodSummary = """
                Large controlled comparison
            """.trimIndent(),
            sampleSummary = """
                4,800 university students
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        advanced_statistical_interpretation_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        What is the most accurate interpretation of this result?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                small_effect
                            """.trimIndent(),
                            text = """
                                The difference is unlikely to be due to random sampling alone, but its practical effect
                                is very small
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                major_improvement
                            """.trimIndent(),
                            text = """
                                Statistical significance proves that the reminder system produced a major educational
                                improvement
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                no_relationship
                            """.trimIndent(),
                            text = """
                                A small percentage difference means that no relationship exists
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        small_effect
                    """.trimIndent(),
                    explanation = """
                        Statistical significance concerns whether an observed difference is unlikely under a
                        specified statistical model. It does not show that the effect is large, important or
                        educationally meaningful.
                    """.trimIndent(),
                    learningTip = """
                        Interpret statistical significance together with effect size, confidence, study quality
                        and practical importance.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_statistical_interpretation_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        What does p less than 0.01 most directly indicate?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                model_based_probability
                            """.trimIndent(),
                            text = """
                                Under the null model and its assumptions, results at least this extreme would be
                                uncommon
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                effect_probability
                            """.trimIndent(),
                            text = """
                                There is a 99 percent probability that the reminder system works
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                importance_proof
                            """.trimIndent(),
                            text = """
                                The result is educationally important
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        model_based_probability
                    """.trimIndent(),
                    explanation = """
                        A p-value is calculated under a statistical model. It is not the probability that the
                        research hypothesis is true and does not measure practical importance.
                    """.trimIndent(),
                    learningTip = """
                        Separate evidence against a null model from the size and value of an effect.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_statistical_interpretation_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Why might the 0.4 percentage-point difference have limited practical importance?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                small_magnitude
                            """.trimIndent(),
                            text = """
                                The average change may be too small to affect grades, progression or student decisions
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                large_sample_invalid
                            """.trimIndent(),
                            text = """
                                Large samples make all effects meaningless
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                significance_false
                            """.trimIndent(),
                            text = """
                                Any statistically significant result must be false
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        small_magnitude
                    """.trimIndent(),
                    explanation = """
                        An effect can be precisely estimated and statistically significant while still being too
                        small to matter in practice.
                    """.trimIndent(),
                    learningTip = """
                        Ask what the numerical difference changes in real educational terms.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_statistical_interpretation_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which additional information would most improve interpretation?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                interval_cost_context
                            """.trimIndent(),
                            text = """
                                A confidence interval, effect-size context, implementation cost and outcome distribution
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                longer_title
                            """.trimIndent(),
                            text = """
                                A longer article title
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                smaller_sample_only
                            """.trimIndent(),
                            text = """
                                A smaller sample with no additional analysis
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        interval_cost_context
                    """.trimIndent(),
                    explanation = """
                        Precision, variation, practical thresholds and implementation costs help determine
                        whether a statistically detectable effect is worthwhile.
                    """.trimIndent(),
                    learningTip = """
                        Good statistical interpretation combines uncertainty, magnitude and decision context.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                advanced_statistical_interpretation
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.ADVANCED,
            title = """
                Statistical Interpretation
            """.trimIndent(),
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
            id = """
                advanced_conflicting_sources_case
            """.trimIndent(),
            researchQuestion = """
                Does laptop note-taking reduce university students' learning outcomes?
            """.trimIndent(),
            title = """
                Comparing Conflicting Studies of Digital Note-Taking
            """.trimIndent(),
            authors = """
                F. Wilson and Y. Park
            """.trimIndent(),
            publication = """
                Evidence in Higher Education
            """.trimIndent(),
            publishedYear = 2026,
            excerpt = """
                Study A surveyed 90 volunteers after one lecture and reported that students preferred
                laptops. Study B randomly assigned 360 students to laptop or handwritten note-taking
                across six lectures and found slightly better delayed test performance in the
                handwritten group.
            """.trimIndent(),
            methodSummary = """
                Comparison of a small voluntary survey and a larger randomised study
            """.trimIndent(),
            sampleSummary = """
                Study A: 90 volunteers; Study B: 360 randomly assigned students
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        advanced_conflicting_sources_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which approach is most appropriate when evaluating these apparently conflicting
                        findings?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                compare_methods
                            """.trimIndent(),
                            text = """
                                Compare the questions, outcomes, samples and study designs before deciding whether the
                                findings truly conflict
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                choose_preference
                            """.trimIndent(),
                            text = """
                                Accept Study A because student preference is the most important evidence of learning
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                newest_source
                            """.trimIndent(),
                            text = """
                                Accept whichever study was published more recently without examining its methods
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        compare_methods
                    """.trimIndent(),
                    explanation = """
                        The studies measured different outcomes. Study A examined preference, while Study B
                        measured delayed performance. Their methods and samples also differed, so the findings
                        are not directly interchangeable.
                    """.trimIndent(),
                    learningTip = """
                        When sources disagree, compare what they measured, who they studied, how the evidence
                        was produced and how precisely the conclusions were stated.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_conflicting_sources_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Why are the findings not necessarily a direct contradiction?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                different_outcomes
                            """.trimIndent(),
                            text = """
                                Students can prefer laptops even if handwriting produces slightly better delayed test
                                performance
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                same_question
                            """.trimIndent(),
                            text = """
                                Both studies measured exactly the same outcome in the same way
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                preference_equals_learning
                            """.trimIndent(),
                            text = """
                                Preference and learning performance are identical concepts
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        different_outcomes
                    """.trimIndent(),
                    explanation = """
                        Preference and delayed learning performance are different outcomes, so both findings
                        could be true at the same time.
                    """.trimIndent(),
                    learningTip = """
                        Before resolving a disagreement, confirm that the studies are answering the same
                        question.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_conflicting_sources_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which study provides stronger evidence about the effect of note-taking method on delayed
                        performance?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                study_b
                            """.trimIndent(),
                            text = """
                                Study B because it randomly assigned more students and measured delayed test performance
                                across several lectures
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                study_a
                            """.trimIndent(),
                            text = """
                                Study A because volunteers said they preferred laptops
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                equal_strength
                            """.trimIndent(),
                            text = """
                                Both studies are equally strong for every possible conclusion
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        study_b
                    """.trimIndent(),
                    explanation = """
                        Study B directly measured the relevant learning outcome and used random assignment
                        across repeated lectures.
                    """.trimIndent(),
                    learningTip = """
                        Evidence strength is claim-specific. A study may be strong for one outcome and weak for
                        another.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_conflicting_sources_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.CITATION_SUPPORT,
                    prompt = """
                        Which synthesis best represents both studies?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                balanced_synthesis
                            """.trimIndent(),
                            text = """
                                Students in one survey preferred laptops, while a larger randomised study found slightly
                                better delayed performance with handwritten notes
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                laptops_always_bad
                            """.trimIndent(),
                            text = """
                                Laptop note-taking always harms learning
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                preference_proves_effect
                            """.trimIndent(),
                            text = """
                                Laptop preference proves that laptops improve learning
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        balanced_synthesis
                    """.trimIndent(),
                    explanation = """
                        The balanced statement preserves the distinct outcome, method and strength of each
                        source without forcing them into one oversimplified conclusion.
                    """.trimIndent(),
                    learningTip = """
                        Synthesis should explain differences rather than hide them.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                advanced_conflicting_sources
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.ADVANCED,
            title = """
                Comparing Conflicting Sources
            """.trimIndent(),
            description = """
                Evaluate why credible-looking studies may reach different conclusions.
            """.trimIndent(),
            learningFocus = """
                Compare research questions, outcomes, samples and methods instead of choosing a source
                by appearance.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }


    private fun createSystematicReviewQualityModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = """
                advanced_systematic_review_quality_case
            """.trimIndent(),
            researchQuestion = """
                Do mindfulness programmes reduce stress among university students?
            """.trimIndent(),
            title = """
                Mindfulness and Student Stress: A Review of Published Evidence
            """.trimIndent(),
            authors = """
                N. Evans and J. Rahman
            """.trimIndent(),
            publication = """
                Student Mental Health Research
            """.trimIndent(),
            publishedYear = 2026,
            excerpt = """
                The review included 12 studies found through one academic database. The authors did not
                publish their search terms, inclusion criteria or assessment of study quality. Studies
                reporting no benefit were discussed briefly, while positive studies received more
                detailed attention.
            """.trimIndent(),
            methodSummary = """
                Narrative review described as a systematic review
            """.trimIndent(),
            sampleSummary = """
                12 published studies identified through one database
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        advanced_systematic_review_quality_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which issue most seriously weakens confidence in this review?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                untransparent_process
                            """.trimIndent(),
                            text = """
                                The search, selection and quality-assessment process is not transparent or reproducible
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                study_count
                            """.trimIndent(),
                            text = """
                                A review must always contain more than 100 studies to be valid
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                topic_popularity
                            """.trimIndent(),
                            text = """
                                Mindfulness is a popular topic, so research about it cannot be evaluated
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        untransparent_process
                    """.trimIndent(),
                    explanation = """
                        Without clear search terms, inclusion rules and quality assessment, readers cannot
                        determine whether important studies were missed or whether weak and strong evidence were
                        treated appropriately.
                    """.trimIndent(),
                    learningTip = """
                        A strong systematic review should report a reproducible search, explicit eligibility
                        criteria and a structured assessment of study quality.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_systematic_review_quality_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        What risk is created by searching only one database?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                incomplete_coverage
                            """.trimIndent(),
                            text = """
                                Relevant studies indexed elsewhere may be missed, producing an incomplete evidence base
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                too_many_results
                            """.trimIndent(),
                            text = """
                                One database always returns too many studies to review
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                automatic_bias_free
                            """.trimIndent(),
                            text = """
                                Using one database guarantees that publication bias is removed
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        incomplete_coverage
                    """.trimIndent(),
                    explanation = """
                        Database coverage differs by discipline, region and publication type. A single database
                        may omit important evidence.
                    """.trimIndent(),
                    learningTip = """
                        Search breadth matters because missing studies can change a review's conclusion.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_systematic_review_quality_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        What concern is raised by giving positive studies more detailed attention?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                selective_emphasis
                            """.trimIndent(),
                            text = """
                                The narrative may selectively emphasise findings that support benefit
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                balanced_reporting
                            """.trimIndent(),
                            text = """
                                It proves that positive and null findings were treated equally
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                sample_randomisation
                            """.trimIndent(),
                            text = """
                                It randomly assigns the included studies
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        selective_emphasis
                    """.trimIndent(),
                    explanation = """
                        Unequal discussion can distort the apparent balance of evidence even when null studies
                        are technically mentioned.
                    """.trimIndent(),
                    learningTip = """
                        Evaluate not only which studies are included, but also how their findings are
                        represented.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_systematic_review_quality_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which revision would most improve the review?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                protocol_multiple_databases
                            """.trimIndent(),
                            text = """
                                Publish a protocol, search several databases, report search strings and criteria, and
                                assess risk of bias
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                remove_null_studies
                            """.trimIndent(),
                            text = """
                                Exclude every study that reports no benefit
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                rename_review
                            """.trimIndent(),
                            text = """
                                Keep the same process but use a more authoritative title
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        protocol_multiple_databases
                    """.trimIndent(),
                    explanation = """
                        A predefined and reproducible process improves coverage, reduces selective decision-
                        making and allows readers to audit the review.
                    """.trimIndent(),
                    learningTip = """
                        Methodological labels matter less than transparent and reproducible practice.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                advanced_systematic_review_quality
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.ADVANCED,
            title = """
                Systematic Review Quality
            """.trimIndent(),
            description = """
                Evaluate the transparency and reliability of evidence-review methods.
            """.trimIndent(),
            learningFocus = """
                Check database coverage, search reporting, inclusion criteria and study-quality
                assessment.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }


    private fun createResearchEthicsModule(): LearningModule {
        val evidenceCase = EvidenceCase(
            id = """
                advanced_research_ethics_case
            """.trimIndent(),
            researchQuestion = """
                How do financial difficulties affect university students' mental wellbeing?
            """.trimIndent(),
            title = """
                Financial Stress and Student Wellbeing
            """.trimIndent(),
            authors = """
                B. Carter and L. Hassan
            """.trimIndent(),
            publication = """
                Journal of Student Support
            """.trimIndent(),
            publishedYear = 2026,
            excerpt = """
                Researchers collected identifiable financial and mental-health information through an
                online survey. Participants were not clearly told how long their data would be stored.
                The research team shared the raw dataset with another organisation without describing a
                separate consent process.
            """.trimIndent(),
            methodSummary = """
                Online survey collecting sensitive identifiable information
            """.trimIndent(),
            sampleSummary = """
                650 university students
            """.trimIndent(),
            sourceType = SourceType.PEER_REVIEWED_ARTICLE,
            sourceNote = practiceSourceNote,
            questions = listOf(
                question(
                    id = """
                        advanced_research_ethics_question_1
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        What is the most important ethical concern in this case?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                consent_privacy
                            """.trimIndent(),
                            text = """
                                Participants may not have given sufficiently informed consent for storing and sharing
                                sensitive identifiable data
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                large_sample
                            """.trimIndent(),
                            text = """
                                The study included too many participants for ethical approval
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                online_method
                            """.trimIndent(),
                            text = """
                                Online surveys are always unethical regardless of their safeguards
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        consent_privacy
                    """.trimIndent(),
                    explanation = """
                        Sensitive information requires clear consent, strong privacy protections and transparent
                        limits on storage and sharing. Data collection methods are not automatically unethical,
                        but their safeguards must match the risks faced by participants.
                    """.trimIndent(),
                    learningTip = """
                        Examine informed consent, data minimisation, confidentiality, participant risk and
                        transparency about secondary data use.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_research_ethics_question_2
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Why does keeping the data identifiable increase participant risk?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                reidentification_harm
                            """.trimIndent(),
                            text = """
                                A breach or inappropriate access could connect sensitive financial and mental-health
                                details to specific students
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                analysis_impossible
                            """.trimIndent(),
                            text = """
                                Identifiable data can never be analysed
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                sample_too_large
                            """.trimIndent(),
                            text = """
                                Identification risk exists only because the sample contains 650 students
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        reidentification_harm
                    """.trimIndent(),
                    explanation = """
                        Identifiable sensitive data can expose participants to stigma, discrimination,
                        embarrassment or other harm if confidentiality fails.
                    """.trimIndent(),
                    learningTip = """
                        The sensitivity and identifiability of data should determine the strength of privacy
                        safeguards.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_research_ethics_question_3
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        What should normally happen before sharing the raw dataset with another organisation?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                clear_authorisation
                            """.trimIndent(),
                            text = """
                                Confirm that consent and ethics approval cover the sharing, minimise or de-identify the
                                data, and establish secure access rules
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                informal_transfer
                            """.trimIndent(),
                            text = """
                                Send the full dataset if the receiving organisation promises to be careful
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                remove_storage_notice
                            """.trimIndent(),
                            text = """
                                Avoid telling participants how long the data will be stored
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        clear_authorisation
                    """.trimIndent(),
                    explanation = """
                        Secondary sharing requires a lawful and ethically approved purpose, appropriate consent,
                        data minimisation and secure governance.
                    """.trimIndent(),
                    learningTip = """
                        Data sharing is a separate decision that must be justified and controlled.
                    """.trimIndent()
                ),
                question(
                    id = """
                        advanced_research_ethics_question_4
                    """.trimIndent(),
                    dimension = EvaluationDimension.EVIDENCE_STRENGTH,
                    prompt = """
                        Which redesign would best reduce ethical risk?
                    """.trimIndent(),
                    options = listOf(
                        option(
                            id = """
                                privacy_by_design
                            """.trimIndent(),
                            text = """
                                Collect only necessary data, separate identifiers, explain retention and sharing
                                clearly, and obtain explicit consent
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                collect_more_data
                            """.trimIndent(),
                            text = """
                                Collect additional identifiable information in case it becomes useful later
                            """.trimIndent()
                        ),
                        option(
                            id = """
                                no_information
                            """.trimIndent(),
                            text = """
                                Give participants less information so they are not worried
                            """.trimIndent()
                        )
                    ),
                    correctOptionId = """
                        privacy_by_design
                    """.trimIndent(),
                    explanation = """
                        Privacy by design reduces unnecessary exposure while improving the quality of informed
                        consent and data governance.
                    """.trimIndent(),
                    learningTip = """
                        Ethical research should minimise risk before collection rather than relying only on
                        promises after collection.
                    """.trimIndent()
                )
            )
        )

        return LearningModule(
            id = """
                advanced_research_ethics
            """.trimIndent(),
            difficultyLevel = DifficultyLevel.ADVANCED,
            title = """
                Research Ethics and Transparency
            """.trimIndent(),
            description = """
                Evaluate consent, privacy, participant risk and transparent data practices.
            """.trimIndent(),
            learningFocus = """
                Consider whether research protects participants and clearly explains how sensitive data
                will be used.
            """.trimIndent(),
            evidenceCase = evidenceCase
        )
    }



    // Build questions consistently across all learning modules
    private fun question(
        id: String,
        dimension: EvaluationDimension,
        prompt: String,
        options: List<AnswerOption>,
        correctOptionId: String,
        explanation: String,
        learningTip: String
    ): EvaluationQuestion {
        return EvaluationQuestion(
            id = id,
            dimension = dimension,
            prompt = prompt,
            options = options,
            correctOptionId = correctOptionId,
            explanation = explanation,
            learningTip = learningTip
        )
    }

    // Build answer options with a stable identifier and visible label
    private fun option(
        id: String,
        text: String
    ): AnswerOption {
        return AnswerOption(
            id = id,
            text = text
        )
    }
}

