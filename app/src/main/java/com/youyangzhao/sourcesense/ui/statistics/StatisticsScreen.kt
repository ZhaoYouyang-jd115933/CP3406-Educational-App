package com.youyangzhao.sourcesense.ui.statistics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.domain.model.DifficultyProgress
import com.youyangzhao.sourcesense.domain.model.LearningActivityType
import com.youyangzhao.sourcesense.domain.model.LearningStatistics
import com.youyangzhao.sourcesense.domain.model.RecentLearningActivity
import com.youyangzhao.sourcesense.domain.model.RecommendedFocus
import com.youyangzhao.sourcesense.domain.model.SkillAccuracy
import com.youyangzhao.sourcesense.domain.model.SkillProgressStatus
import com.youyangzhao.sourcesense.domain.model.SourceCitationDecision
import com.youyangzhao.sourcesense.domain.model.SourceReviewDepth
import com.youyangzhao.sourcesense.domain.model.SourceReviewStatistics
import com.youyangzhao.sourcesense.domain.model.SourceVerificationItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Define the overall statistics page palette
private val StatisticsBackground =
    Color(0xFFF8F5FA)

private val StatisticsTextPrimary =
    Color(0xFF302A32)

private val StatisticsTextSecondary =
    Color(0xFF625A65)

private val StatisticsBorder =
    Color(0xFFE8DDE5)

// Define the main pink and purple accents
private val StatisticsPink =
    Color(0xFFB86186)

private val StatisticsPinkDark =
    Color(0xFF934D6D)

private val StatisticsPinkSoft =
    Color(0xFFF6E7EE)

private val StatisticsPurple =
    Color(0xFF6F6BB0)

private val StatisticsPurpleSoft =
    Color(0xFFEDEBFA)

// Define status colors for skill feedback
private val StrongGreen =
    Color(0xFF357A5C)

private val StrongGreenSoft =
    Color(0xFFE4F3EC)

private val DevelopingGold =
    Color(0xFF8C6A24)

private val DevelopingGoldSoft =
    Color(0xFFF8EFCF)

private val PracticeRed =
    Color(0xFFA8505D)

private val PracticeRedSoft =
    Color(0xFFF9E4E6)

private val NeutralBlue =
    Color(0xFF4E678F)

private val NeutralBlueSoft =
    Color(0xFFE8EFF9)

@Composable
fun StatisticsRoute(
    viewModel: StatisticsViewModel,
    onPracticeModule: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    StatisticsScreen(
        uiState = uiState,
        onPracticeModule = onPracticeModule,
        onClearError = viewModel::clearError,
        modifier = modifier
    )
}

@Composable
fun StatisticsScreen(
    uiState: StatisticsUiState,
    onPracticeModule: (String) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = StatisticsBackground
    ) {
        if (uiState.isLoading) {
            StatisticsLoadingContent()
        } else {
            StatisticsContent(
                uiState = uiState,
                onPracticeModule = onPracticeModule,
                onClearError = onClearError
            )
        }
    }
}

@Composable
private fun StatisticsLoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement =
            Arrangement.Center,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = StatisticsPink
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Loading learning statistics...",
            style =
                MaterialTheme.typography.bodyLarge,
            color = StatisticsTextSecondary
        )
    }
}

@Composable
private fun StatisticsContent(
    uiState: StatisticsUiState,
    onPracticeModule: (String) -> Unit,
    onClearError: () -> Unit
) {
    val statistics = uiState.statistics

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Learning Statistics",
                style =
                    MaterialTheme.typography
                        .headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = StatisticsTextPrimary
            )

            if (uiState.showSectionDescriptions) {
                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text =
                        "See your progress, strongest skills and next learning priority.",
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color = StatisticsTextSecondary
                )
            }
        }

        if (uiState.errorMessage != null) {
            item {
                StatisticsErrorCard(
                    message = uiState.errorMessage,
                    onDismiss = onClearError
                )
            }
        }

        item {
            OverviewCard(
                statistics = statistics
            )
        }

        item {
            SectionHeading(
                title = "Progress by Level",
                description =
                    if (uiState.showSectionDescriptions) {
                        "Each level contains five learning modules."
                    } else {
                        null
                    }
            )
        }

        items(
            items = statistics.progressByDifficulty,
            key = { progress ->
                progress.difficultyLevel.name
            }
        ) { progress ->
            DifficultyProgressCard(
                progress = progress
            )
        }

        if (uiState.showLearningRecommendation) {
            statistics.recommendedFocus?.let { focus ->
                item {
                    RecommendedFocusCard(
                        focus = focus,
                        onPracticeModule =
                            onPracticeModule
                    )
                }
            }
        }

        if (uiState.showSkillAccuracy) {
            item {
                SectionHeading(
                    title = "Skill Accuracy",
                    description =
                        if (uiState.showSectionDescriptions) {
                            "Skills with at least three answers receive a progress rating."
                        } else {
                            null
                        }
                )
            }

            if (statistics.skillAccuracies.isEmpty()) {
                item {
                    EmptySectionCard(
                        title = "No skill data yet",
                        message =
                            "Complete a learning module to start measuring skill accuracy."
                    )
                }
            } else {
                items(
                    items = statistics.skillAccuracies,
                    key = { skillAccuracy ->
                        skillAccuracy.dimension.name
                    }
                ) { skillAccuracy ->
                    SkillAccuracyCard(
                        skillAccuracy =
                            skillAccuracy
                    )
                }
            }
        }

        if (uiState.showRealSourcePractice) {
            item {
                SectionHeading(
                    title = "Real Source Practice",
                    description =
                        if (uiState.showSectionDescriptions) {
                            "Track how deeply you reviewed real academic sources."
                        } else {
                            null
                        }
                )
            }

            item {
                SourcePracticeCard(
                    statistics =
                        statistics.sourceReviewStatistics
                )
            }
        }

        if (uiState.showRecentActivity) {
            item {
                SectionHeading(
                    title = "Recent Activity",
                    description =
                        if (uiState.showSectionDescriptions) {
                            "Your latest evaluations and saved source reviews."
                        } else {
                            null
                        }
                )
            }

            if (statistics.recentActivities.isEmpty()) {
                item {
                    EmptySectionCard(
                        title = "No recent activity",
                        message =
                            "Your completed evaluations and source reviews will appear here."
                    )
                }
            } else {
                items(
                    items = statistics.recentActivities,
                    key = { activity ->
                        activity.id
                    }
                ) { activity ->
                    RecentActivityCard(
                        activity = activity
                    )
                }
            }
        }

        item {
            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun OverviewCard(
    statistics: LearningStatistics
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = StatisticsPink.copy(
                alpha = 0.24f
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFF5F9),
                            Color(0xFFF3DCE8)
                        )
                    )
                )
        ) {
            // Add soft decoration without covering statistics
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = 30.dp,
                        y = (-30).dp
                    )
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(
                            alpha = 0.35f
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Progress Overview",
                    style =
                        MaterialTheme.typography
                            .titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = StatisticsTextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {
                    OverviewMetric(
                        value =
                            "${statistics.totalModulesCompleted} / ${statistics.totalModules}",
                        label = "Modules",
                        modifier = Modifier.weight(1f)
                    )

                    OverviewMetric(
                        value =
                            statistics.completedEvaluations
                                .toString(),
                        label = "Attempts",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {
                    OverviewMetric(
                        value =
                            "${statistics.averagePercentage}%",
                        label = "Average",
                        modifier = Modifier.weight(1f)
                    )

                    OverviewMetric(
                        value =
                            statistics.sourceReviewCount
                                .toString(),
                        label = "Source Reviews",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text =
                        "Best evaluation score: ${statistics.bestPercentage}%",
                    style =
                        MaterialTheme.typography
                            .bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = StatisticsPinkDark
                )
            }
        }
    }
}

@Composable
private fun OverviewMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(
            alpha = 0.72f
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(
                alpha = 0.9f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 12.dp
            ),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = value,
                style =
                    MaterialTheme.typography
                        .titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = StatisticsTextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = label,
                style =
                    MaterialTheme.typography
                        .labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = StatisticsTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DifficultyProgressCard(
    progress: DifficultyProgress
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = StatisticsBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(11.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text =
                        progress.difficultyLevel
                            .displayName,
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = StatisticsTextPrimary
                )

                Text(
                    text =
                        "${progress.completedModules} / ${progress.totalModules}",
                    style =
                        MaterialTheme.typography
                            .labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = StatisticsPinkDark
                )
            }

            ModuleProgressSegments(
                completedModules =
                    progress.completedModules,
                totalModules =
                    progress.totalModules
            )
        }
    }
}

@Composable
private fun ModuleProgressSegments(
    completedModules: Int,
    totalModules: Int
) {
    if (totalModules <= 0) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(StatisticsPinkSoft)
        )
        return
    }

    val safeCompleted =
        completedModules.coerceIn(
            minimumValue = 0,
            maximumValue = totalModules
        )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(7.dp)
    ) {
        repeat(totalModules) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(9.dp)
                    .clip(
                        RoundedCornerShape(100.dp)
                    )
                    .background(
                        if (index < safeCompleted) {
                            StatisticsPink
                        } else {
                            StatisticsPinkSoft
                        }
                    )
            )
        }
    }
}

@Composable
private fun RecommendedFocusCard(
    focus: RecommendedFocus,
    onPracticeModule: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = StatisticsPurpleSoft
        ),
        border = BorderStroke(
            width = 1.dp,
            color = StatisticsPurple.copy(
                alpha = 0.30f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(11.dp)
        ) {
            Text(
                text = "Recommended Next Focus",
                style =
                    MaterialTheme.typography
                        .titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = StatisticsTextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement =
                        Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = focus.dimension.displayName,
                        style =
                            MaterialTheme.typography
                                .titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatisticsPurple
                    )

                    Text(
                        text =
                            "${focus.percentage}% across ${focus.totalAnswers} answers",
                        style =
                            MaterialTheme.typography
                                .bodyMedium,
                        color = StatisticsTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color.White.copy(
                        alpha = 0.70f
                    )
                ) {
                    Text(
                        text = "NEXT",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        style =
                            MaterialTheme.typography
                                .labelSmall,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color = StatisticsPurple
                    )
                }
            }

            Text(
                text = focus.reason,
                style =
                    MaterialTheme.typography.bodyMedium,
                color = StatisticsTextSecondary
            )

            Text(
                text =
                    "Recommended module: ${focus.moduleTitle}",
                style =
                    MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = StatisticsTextPrimary
            )

            Button(
                onClick = {
                    onPracticeModule(focus.moduleId)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StatisticsPurple
                )
            ) {
                Text(
                    text = "Practice This Skill",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SkillAccuracyCard(
    skillAccuracy: SkillAccuracy
) {
    val progress =
        skillAccuracy.percentage
            .coerceIn(
                minimumValue = 0,
                maximumValue = 100
            ) / 100f

    val statusColors =
        skillStatusColors(
            status = skillAccuracy.status
        )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = StatisticsBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text =
                        skillAccuracy.dimension
                            .displayName,
                    modifier = Modifier.weight(1f),
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = StatisticsTextPrimary
                )

                Text(
                    text =
                        "${skillAccuracy.percentage}%",
                    style =
                        MaterialTheme.typography
                            .titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = statusColors.accent
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = statusColors.accent,
                trackColor = statusColors.background
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text =
                        "${skillAccuracy.correctAnswers} / ${skillAccuracy.totalAnswers} correct",
                    style =
                        MaterialTheme.typography
                            .bodySmall,
                    color = StatisticsTextSecondary
                )

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = statusColors.background
                ) {
                    Text(
                        text =
                            skillAccuracy.status
                                .displayName,
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        ),
                        style =
                            MaterialTheme.typography
                                .labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColors.accent
                    )
                }
            }
        }
    }
}

@Composable
private fun SourcePracticeCard(
    statistics: SourceReviewStatistics
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = NeutralBlueSoft
        ),
        border = BorderStroke(
            width = 1.dp,
            color = NeutralBlue.copy(
                alpha = 0.25f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = "Structured Reviews",
                    style =
                        MaterialTheme.typography
                            .titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = StatisticsTextPrimary
                )

                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(
                        alpha = 0.72f
                    )
                ) {
                    Box(
                        modifier = Modifier.size(46.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text =
                                statistics.totalReviews
                                    .toString(),
                            style =
                                MaterialTheme.typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color = NeutralBlue
                        )
                    }
                }
            }

            if (statistics.totalReviews == 0) {
                Text(
                    text =
                        "Search and evaluate a real source in Explore to begin tracking practical review habits.",
                    style =
                        MaterialTheme.typography
                            .bodyLarge,
                    color = StatisticsTextSecondary
                )
            } else {
                Text(
                    text = "Review Depth",
                    style =
                        MaterialTheme.typography
                            .labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeutralBlue
                )

                SourceCountRow(
                    label =
                        SourceReviewDepth
                            .METADATA_ONLY
                            .displayName,
                    count = statistics
                        .reviewDepthCounts[
                        SourceReviewDepth
                            .METADATA_ONLY
                    ] ?: 0
                )

                SourceCountRow(
                    label =
                        SourceReviewDepth
                            .ABSTRACT_REVIEWED
                            .displayName,
                    count = statistics
                        .reviewDepthCounts[
                        SourceReviewDepth
                            .ABSTRACT_REVIEWED
                    ] ?: 0
                )

                SourceCountRow(
                    label =
                        SourceReviewDepth
                            .FULL_TEXT_REVIEWED
                            .displayName,
                    count = statistics
                        .reviewDepthCounts[
                        SourceReviewDepth
                            .FULL_TEXT_REVIEWED
                    ] ?: 0
                )

                HorizontalDivider(
                    color = NeutralBlue.copy(
                        alpha = 0.20f
                    )
                )

                Text(
                    text = "Current Decisions",
                    style =
                        MaterialTheme.typography
                            .labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeutralBlue
                )

                SourceCountRow(
                    label =
                        SourceCitationDecision
                            .READY_TO_CONSIDER
                            .displayName,
                    count = statistics
                        .citationDecisionCounts[
                        SourceCitationDecision
                            .READY_TO_CONSIDER
                    ] ?: 0
                )

                SourceCountRow(
                    label =
                        SourceCitationDecision
                            .NEEDS_FULL_TEXT_REVIEW
                            .displayName,
                    count = statistics
                        .citationDecisionCounts[
                        SourceCitationDecision
                            .NEEDS_FULL_TEXT_REVIEW
                    ] ?: 0
                )

                SourceCountRow(
                    label =
                        SourceCitationDecision
                            .NOT_SUITABLE
                            .displayName,
                    count = statistics
                        .citationDecisionCounts[
                        SourceCitationDecision
                            .NOT_SUITABLE
                    ] ?: 0
                )

                val commonChecks = statistics
                    .verificationItemCounts
                    .filterValues { count ->
                        count > 0
                    }
                    .toList()
                    .sortedByDescending { item ->
                        item.second
                    }
                    .take(3)

                if (commonChecks.isNotEmpty()) {
                    HorizontalDivider(
                        color = NeutralBlue.copy(
                            alpha = 0.20f
                        )
                    )

                    Text(
                        text = "Most Common Checks",
                        style =
                            MaterialTheme.typography
                                .labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = NeutralBlue
                    )

                    commonChecks.forEach { item ->
                        VerificationCountRow(
                            item = item.first,
                            count = item.second
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceCountRow(
    label: String,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style =
                MaterialTheme.typography.bodyMedium,
            color = StatisticsTextPrimary
        )

        Text(
            text = count.toString(),
            style =
                MaterialTheme.typography
                    .labelLarge,
            fontWeight = FontWeight.ExtraBold,
            color = NeutralBlue
        )
    }
}

@Composable
private fun VerificationCountRow(
    item: SourceVerificationItem,
    count: Int
) {
    SourceCountRow(
        label = item.displayName,
        count = count
    )
}

@Composable
private fun RecentActivityCard(
    activity: RecentLearningActivity
) {
    val activityColors =
        if (
            activity.activityType ==
            LearningActivityType.EVALUATION
        ) {
            StatusColors(
                accent = StatisticsPinkDark,
                background = StatisticsPinkSoft
            )
        } else {
            StatusColors(
                accent = NeutralBlue,
                background = NeutralBlueSoft
            )
        }

    val typeLabel =
        if (
            activity.activityType ==
            LearningActivityType.EVALUATION
        ) {
            "EVALUATION"
        } else {
            "SOURCE REVIEW"
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = StatisticsBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(7.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = activityColors.background
                ) {
                    Text(
                        text = typeLabel,
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        ),
                        style =
                            MaterialTheme.typography
                                .labelSmall,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color = activityColors.accent
                    )
                }

                Text(
                    text = formatCompletedTime(
                        completedAt =
                            activity.completedAt
                    ),
                    style =
                        MaterialTheme.typography
                            .bodySmall,
                    color = StatisticsTextSecondary
                )
            }

            Text(
                text = activity.title,
                style =
                    MaterialTheme.typography
                        .titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = StatisticsTextPrimary
            )

            Text(
                text = activity.subtitle,
                style =
                    MaterialTheme.typography
                        .bodyMedium,
                color = StatisticsTextSecondary
            )

            Text(
                text = activity.outcome,
                style =
                    MaterialTheme.typography
                        .bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = activityColors.accent
            )
        }
    }
}

@Composable
private fun SectionHeading(
    title: String,
    description: String?
) {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography
                    .titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = StatisticsTextPrimary
        )

        if (description != null) {
            // Hide helper text when compact statistics display is enabled
            Text(
                text = description,
                style =
                    MaterialTheme.typography.bodyMedium,
                color = StatisticsTextSecondary
            )
        }
    }
}

@Composable
private fun EmptySectionCard(
    title: String,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = StatisticsBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography
                        .titleMedium,
                fontWeight = FontWeight.Bold,
                color = StatisticsTextPrimary
            )

            Text(
                text = message,
                style =
                    MaterialTheme.typography.bodyMedium,
                color = StatisticsTextSecondary
            )
        }
    }
}

@Composable
private fun StatisticsErrorCard(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PracticeRedSoft
        ),
        border = BorderStroke(
            width = 1.dp,
            color = PracticeRed.copy(
                alpha = 0.35f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = message,
                style =
                    MaterialTheme.typography.bodyMedium,
                color = StatisticsTextPrimary
            )

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(
                    Alignment.End
                )
            ) {
                Text(text = "Dismiss")
            }
        }
    }
}

private fun skillStatusColors(
    status: SkillProgressStatus
): StatusColors {
    return when (status) {
        SkillProgressStatus.STRONG -> {
            StatusColors(
                accent = StrongGreen,
                background = StrongGreenSoft
            )
        }

        SkillProgressStatus.DEVELOPING -> {
            StatusColors(
                accent = DevelopingGold,
                background = DevelopingGoldSoft
            )
        }

        SkillProgressStatus.NEEDS_PRACTICE -> {
            StatusColors(
                accent = PracticeRed,
                background = PracticeRedSoft
            )
        }

        SkillProgressStatus.NOT_ENOUGH_DATA -> {
            StatusColors(
                accent = NeutralBlue,
                background = NeutralBlueSoft
            )
        }
    }
}

private fun formatCompletedTime(
    completedAt: Long
): String {
    val formatter = SimpleDateFormat(
        "dd MMM yyyy, HH:mm",
        Locale.getDefault()
    )

    return formatter.format(
        Date(completedAt)
    )
}

private data class StatusColors(
    val accent: Color,
    val background: Color
)