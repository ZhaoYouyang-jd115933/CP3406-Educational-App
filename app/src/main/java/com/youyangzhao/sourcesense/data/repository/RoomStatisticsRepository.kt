package com.youyangzhao.sourcesense.data.repository

import com.youyangzhao.sourcesense.data.local.dao.DimensionAccuracy
import com.youyangzhao.sourcesense.data.local.dao.EvaluationAttemptDao
import com.youyangzhao.sourcesense.data.local.dao.SourceReviewDao
import com.youyangzhao.sourcesense.data.local.entity.EvaluationAttemptEntity
import com.youyangzhao.sourcesense.data.local.entity.SourceReviewEntity
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel
import com.youyangzhao.sourcesense.domain.model.DifficultyProgress
import com.youyangzhao.sourcesense.domain.model.EvaluationDimension
import com.youyangzhao.sourcesense.domain.model.LearningActivityType
import com.youyangzhao.sourcesense.domain.model.LearningModule
import com.youyangzhao.sourcesense.domain.model.LearningStatistics
import com.youyangzhao.sourcesense.domain.model.RecentAttempt
import com.youyangzhao.sourcesense.domain.model.RecentLearningActivity
import com.youyangzhao.sourcesense.domain.model.RecommendedFocus
import com.youyangzhao.sourcesense.domain.model.SkillAccuracy
import com.youyangzhao.sourcesense.domain.model.SourceCitationDecision
import com.youyangzhao.sourcesense.domain.model.SourceReviewDepth
import com.youyangzhao.sourcesense.domain.model.SourceReviewStatistics
import com.youyangzhao.sourcesense.domain.model.SourceVerificationItem
import com.youyangzhao.sourcesense.domain.repository.LearningModuleRepository
import com.youyangzhao.sourcesense.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt

class RoomStatisticsRepository(
    private val evaluationAttemptDao: EvaluationAttemptDao,
    private val sourceReviewDao: SourceReviewDao,
    private val learningModuleRepository:
    LearningModuleRepository
) : StatisticsRepository {

    override fun observeLearningStatistics():
            Flow<LearningStatistics> {
        val evaluationStatisticsFlow =
            observeEvaluationSnapshot()

        val learningModulesFlow = flow {
            // The local module catalogue is static during one app session
            val modules = DifficultyLevel.values()
                .flatMap { difficultyLevel ->
                    learningModuleRepository
                        .getModulesForDifficulty(
                            difficultyLevel =
                                difficultyLevel
                        )
                }

            emit(modules)
        }

        return combine(
            evaluationStatisticsFlow,
            sourceReviewDao.observeSourceReviews(),
            learningModulesFlow
        ) {
                evaluationSnapshot,
                sourceReviews,
                learningModules ->

            buildLearningStatistics(
                evaluationSnapshot =
                    evaluationSnapshot,
                sourceReviews = sourceReviews,
                learningModules = learningModules
            )
        }
    }

    override suspend fun clearLearningHistory() {
        // Delete attempts and their linked answers through Room cascade rules
        evaluationAttemptDao.clearAllAttempts()
    }

    override suspend fun clearSourceReviews() {
        sourceReviewDao.clearAllSourceReviews()
    }

    override suspend fun clearAllLearningData() {
        // Keep both histories independent while clearing them together
        evaluationAttemptDao.clearAllAttempts()
        sourceReviewDao.clearAllSourceReviews()
    }

    private fun observeEvaluationSnapshot():
            Flow<EvaluationStatisticsSnapshot> {
        return combine(
            evaluationAttemptDao.observeAttempts(),
            evaluationAttemptDao.observeAttemptCount(),
            evaluationAttemptDao.observeAveragePercentage(),
            evaluationAttemptDao.observeBestPercentage(),
            evaluationAttemptDao.observeDimensionAccuracy()
        ) {
                attempts,
                attemptCount,
                averagePercentage,
                bestPercentage,
                dimensionAccuracy ->

            EvaluationStatisticsSnapshot(
                attempts = attempts,
                attemptCount = attemptCount,
                averagePercentage =
                    averagePercentage
                        ?.roundToInt()
                        ?: 0,
                bestPercentage =
                    bestPercentage ?: 0,
                dimensionAccuracy =
                    dimensionAccuracy
            )
        }
    }

    private fun buildLearningStatistics(
        evaluationSnapshot:
        EvaluationStatisticsSnapshot,
        sourceReviews: List<SourceReviewEntity>,
        learningModules: List<LearningModule>
    ): LearningStatistics {
        val moduleByEvidenceCaseId =
            learningModules.associateBy { module ->
                module.evidenceCase.id
            }

        val recentAttempts =
            evaluationSnapshot.attempts
                .take(5)
                .map { attempt ->
                    val module =
                        moduleByEvidenceCaseId[
                            attempt.evidenceCaseId
                        ]

                    RecentAttempt(
                        id = attempt.id,
                        evidenceCaseId =
                            attempt.evidenceCaseId,
                        score = attempt.score,
                        totalQuestions =
                            attempt.totalQuestions,
                        percentage =
                            attempt.percentage,
                        completedAt =
                            attempt.completedAt,
                        moduleId = module?.id,
                        moduleTitle =
                            module?.title
                                ?: formatEvidenceCaseName(
                                    attempt.evidenceCaseId
                                ),
                        difficultyLevel =
                            module?.difficultyLevel
                    )
                }

        val skillAccuracies =
            evaluationSnapshot.dimensionAccuracy
                .mapNotNull { accuracy ->
                    accuracy.toSkillAccuracyOrNull()
                }
                .sortedWith(
                    compareBy<SkillAccuracy> { skill ->
                        skill.percentage
                    }.thenBy { skill ->
                        skill.dimension.ordinal
                    }
                )

        val progressByDifficulty =
            buildDifficultyProgress(
                attempts =
                    evaluationSnapshot.attempts,
                learningModules = learningModules
            )

        val sourceReviewStatistics =
            buildSourceReviewStatistics(
                sourceReviews = sourceReviews
            )

        val recommendedFocus =
            buildRecommendedFocus(
                skillAccuracies = skillAccuracies,
                learningModules = learningModules
            )

        val recentActivities =
            buildRecentActivities(
                attempts =
                    evaluationSnapshot.attempts,
                sourceReviews = sourceReviews,
                moduleByEvidenceCaseId =
                    moduleByEvidenceCaseId
            )

        return LearningStatistics(
            completedEvaluations =
                evaluationSnapshot.attemptCount,
            averagePercentage =
                evaluationSnapshot.averagePercentage,
            bestPercentage =
                evaluationSnapshot.bestPercentage,
            recentAttempts = recentAttempts,
            skillAccuracies = skillAccuracies,
            progressByDifficulty =
                progressByDifficulty,
            recommendedFocus =
                recommendedFocus,
            sourceReviewStatistics =
                sourceReviewStatistics,
            recentActivities = recentActivities
        )
    }

    private fun buildDifficultyProgress(
        attempts: List<EvaluationAttemptEntity>,
        learningModules: List<LearningModule>
    ): List<DifficultyProgress> {
        val attemptedEvidenceCaseIds =
            attempts
                .map { attempt ->
                    attempt.evidenceCaseId
                }
                .toSet()

        return DifficultyLevel.values()
            .map { difficultyLevel ->
                val modulesForLevel =
                    learningModules.filter { module ->
                        module.difficultyLevel ==
                                difficultyLevel
                    }

                val completedModules =
                    modulesForLevel.count { module ->
                        module.evidenceCase.id in
                                attemptedEvidenceCaseIds
                    }

                DifficultyProgress(
                    difficultyLevel =
                        difficultyLevel,
                    completedModules =
                        completedModules,
                    totalModules =
                        modulesForLevel.size
                )
            }
    }

    private fun buildRecommendedFocus(
        skillAccuracies: List<SkillAccuracy>,
        learningModules: List<LearningModule>
    ): RecommendedFocus? {
        // Require at least three answers before recommending a weak skill
        val weakestReliableSkill =
            skillAccuracies
                .filter { skill ->
                    skill.totalAnswers >= 3L
                }
                .minWithOrNull(
                    compareBy<SkillAccuracy> { skill ->
                        skill.percentage
                    }.thenBy { skill ->
                        skill.dimension.ordinal
                    }
                )
                ?: return null

        val recommendedModule =
            learningModules
                .filter { module ->
                    module.evidenceCase.questions.any { question ->
                        question.dimension ==
                                weakestReliableSkill.dimension
                    }
                }
                .sortedWith(
                    compareBy<LearningModule> { module ->
                        module.difficultyLevel.ordinal
                    }.thenBy { module ->
                        module.title
                    }
                )
                .firstOrNull()
                ?: return null

        return RecommendedFocus(
            dimension =
                weakestReliableSkill.dimension,
            percentage =
                weakestReliableSkill.percentage,
            totalAnswers =
                weakestReliableSkill.totalAnswers,
            moduleId = recommendedModule.id,
            moduleTitle =
                recommendedModule.title,
            reason =
                "This is your lowest reliable skill score so far."
        )
    }

    private fun buildSourceReviewStatistics(
        sourceReviews: List<SourceReviewEntity>
    ): SourceReviewStatistics {
        val reviewDepthCounts =
            SourceReviewDepth.values()
                .associateWith { depth ->
                    sourceReviews.count { review ->
                        review.reviewDepth == depth.name
                    }
                }

        val citationDecisionCounts =
            SourceCitationDecision.values()
                .associateWith { decision ->
                    sourceReviews.count { review ->
                        review.citationDecision ==
                                decision.name
                    }
                }

        val verificationItemCounts =
            SourceVerificationItem.values()
                .associateWith { verificationItem ->
                    sourceReviews.count { review ->
                        review.verificationItems
                            .split("|")
                            .any { storedItem ->
                                storedItem ==
                                        verificationItem.name
                            }
                    }
                }

        return SourceReviewStatistics(
            totalReviews = sourceReviews.size,
            reviewDepthCounts =
                reviewDepthCounts,
            citationDecisionCounts =
                citationDecisionCounts,
            verificationItemCounts =
                verificationItemCounts
        )
    }

    private fun buildRecentActivities(
        attempts: List<EvaluationAttemptEntity>,
        sourceReviews: List<SourceReviewEntity>,
        moduleByEvidenceCaseId:
        Map<String, LearningModule>
    ): List<RecentLearningActivity> {
        val evaluationActivities =
            attempts.map { attempt ->
                val module =
                    moduleByEvidenceCaseId[
                        attempt.evidenceCaseId
                    ]

                val moduleTitle =
                    module?.title
                        ?: formatEvidenceCaseName(
                            attempt.evidenceCaseId
                        )

                val difficultyLabel =
                    module?.difficultyLevel
                        ?.displayName
                        ?: "Learning module"

                RecentLearningActivity(
                    id = "evaluation-${attempt.id}",
                    activityType =
                        LearningActivityType.EVALUATION,
                    title = moduleTitle,
                    subtitle =
                        "$difficultyLabel evaluation",
                    outcome =
                        "${attempt.score} / ${attempt.totalQuestions} · ${attempt.percentage}%",
                    completedAt =
                        attempt.completedAt
                )
            }

        val sourceReviewActivities =
            sourceReviews.map { review ->
                val decisionLabel =
                    runCatching {
                        SourceCitationDecision
                            .valueOf(
                                review.citationDecision
                            )
                            .displayName
                    }.getOrElse {
                        "Review saved"
                    }

                val depthLabel =
                    runCatching {
                        SourceReviewDepth
                            .valueOf(
                                review.reviewDepth
                            )
                            .displayName
                    }.getOrElse {
                        "Source review"
                    }

                RecentLearningActivity(
                    id = "source-review-${review.id}",
                    activityType =
                        LearningActivityType.SOURCE_REVIEW,
                    title = review.title,
                    subtitle = depthLabel,
                    outcome = decisionLabel,
                    completedAt = review.reviewedAt
                )
            }

        return (
                evaluationActivities +
                        sourceReviewActivities
                )
            .sortedByDescending { activity ->
                activity.completedAt
            }
            .take(8)
    }

    private fun DimensionAccuracy
            .toSkillAccuracyOrNull(): SkillAccuracy? {
        val evaluationDimension = runCatching {
            EvaluationDimension.valueOf(
                dimension
            )
        }.getOrNull() ?: return null

        return SkillAccuracy(
            dimension = evaluationDimension,
            correctAnswers = correctAnswers,
            totalAnswers = totalAnswers
        )
    }

    private fun formatEvidenceCaseName(
        evidenceCaseId: String
    ): String {
        return evidenceCaseId
            .split("_")
            .joinToString(" ") { word ->
                word.replaceFirstChar { character ->
                    character.uppercase()
                }
            }
    }

    private data class EvaluationStatisticsSnapshot(
        val attempts: List<EvaluationAttemptEntity>,
        val attemptCount: Int,
        val averagePercentage: Int,
        val bestPercentage: Int,
        val dimensionAccuracy:
        List<DimensionAccuracy>
    )
}

