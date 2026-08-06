package com.youyangzhao.sourcesense.ui.result

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.domain.model.EvaluationResult
import com.youyangzhao.sourcesense.domain.model.QuestionResult
import com.youyangzhao.sourcesense.ui.evaluation.EvaluationViewModel

// Define the overall page palette
private val ResultBackground =
    Color(0xFFF8F5FA)

private val ResultBorder =
    Color(0xFFE7DCE5)

private val ResultTextPrimary =
    Color(0xFF302A32)

private val ResultTextSecondary =
    Color(0xFF5F5762)

// Define the summary card palette
private val SummaryBlueDark =
    Color(0xFF657CB6)

private val SummaryBlueLight =
    Color(0xFF7C92C9)

private val SummaryBlueSoft =
    Color(0xFFE8EEFB)

// Define correct-state colors
private val CorrectCardBackground =
    Color(0xFFE6F3EC)

private val CorrectCardBorder =
    Color(0xFF9AC3AD)

private val CorrectBadgeBackground =
    Color(0xFFD1E8DB)

private val CorrectAccent =
    Color(0xFF357A5C)

// Define review-state colors
private val ReviewCardBackground =
    Color(0xFFFBE6E7)

private val ReviewCardBorder =
    Color(0xFFE4A5AD)

private val ReviewBadgeBackground =
    Color(0xFFF3D6DA)

private val ReviewAccent =
    Color(0xFFAA4F5D)

// Define the action button colors
private val ActionBlue =
    Color(0xFF334F8F)

private val ActionBlueDark =
    Color(0xFF2A4279)

@Composable
fun ResultRoute(
    viewModel: EvaluationViewModel,
    onTryAgain: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    // Prevent returning to a completed evaluation screen
    BackHandler {
        onBackHome()
    }

    val result = uiState.result

    if (result == null) {
        MissingResultContent(
            onBackHome = onBackHome,
            modifier = modifier
        )
    } else {
        ResultScreen(
            result = result,
            onTryAgain = onTryAgain,
            onBackHome = onBackHome,
            modifier = modifier
        )
    }
}

@Composable
fun ResultScreen(
    result: EvaluationResult,
    onTryAgain: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ResultBackground)
    ) {
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
            }

            item {
                ResultSummaryCard(
                    result = result
                )
            }

            item {
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Question Feedback",
                        style =
                            MaterialTheme.typography
                                .headlineSmall,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color = ResultTextPrimary
                    )

                    Text(
                        text = "Review the key reason for each answer.",
                        style =
                            MaterialTheme.typography
                                .bodyMedium,
                        color = ResultTextSecondary
                    )
                }
            }

            items(
                items = result.questionResults,
                key = { questionResult ->
                    questionResult.questionId
                }
            ) { questionResult ->
                QuestionFeedbackCard(
                    questionResult = questionResult
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackHome,
                        modifier =
                            Modifier.weight(1f),
                        shape =
                            RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = ActionBlueDark
                        )
                    ) {
                        Text(
                            text = "Home",
                            fontWeight =
                                FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onTryAgain,
                        modifier =
                            Modifier.weight(1f),
                        shape =
                            RoundedCornerShape(20.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    ActionBlue
                            )
                    ) {
                        Text(
                            text = "Try Again",
                            fontWeight =
                                FontWeight.Bold
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
}

@Composable
private fun ResultSummaryCard(
    result: EvaluationResult
) {
    val scoreLabel =
        "${result.score} / ${result.totalQuestions}"

    val percentageLabel =
        "${result.percentage}% correct"

    val performanceLabel =
        performanceLabel(result.percentage)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SummaryBlueDark.copy(
                alpha = 0.22f
            )
        ),
        shape = RoundedCornerShape(24.dp),
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
                            SummaryBlueDark,
                            SummaryBlueLight
                        )
                    )
                )
        ) {
            // Add soft decoration without reducing readability
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = 28.dp,
                        y = (-24).dp
                    )
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(
                            alpha = 0.12f
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Evaluation Complete",
                    style =
                        MaterialTheme.typography
                            .headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    ScoreCircle(
                        score = scoreLabel
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = percentageLabel,
                            style =
                                MaterialTheme.typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.Bold,
                            color = Color.White
                        )

                        Surface(
                            shape =
                                RoundedCornerShape(100.dp),
                            color =
                                Color.White.copy(
                                    alpha = 0.18f
                                )
                        ) {
                            Text(
                                text = performanceLabel,
                                modifier =
                                    Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    ),
                                style =
                                    MaterialTheme.typography
                                        .labelLarge,
                                fontWeight =
                                    FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color =
                        Color.White.copy(
                            alpha = 0.14f
                        )
                ) {
                    Text(
                        text = performanceMessage(result.percentage),
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 12.dp
                        ),
                        style =
                            MaterialTheme.typography
                                .bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreCircle(
    score: String
) {
    Surface(
        modifier = Modifier.size(92.dp),
        shape = CircleShape,
        color = SummaryBlueSoft.copy(
            alpha = 0.18f
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(
                alpha = 0.25f
            )
        )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = score,
                style =
                    MaterialTheme.typography
                        .headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun QuestionFeedbackCard(
    questionResult: QuestionResult
) {
    val cardBackground =
        if (questionResult.isCorrect) {
            CorrectCardBackground
        } else {
            ReviewCardBackground
        }

    val cardBorder =
        if (questionResult.isCorrect) {
            CorrectCardBorder
        } else {
            ReviewCardBorder
        }

    val badgeBackground =
        if (questionResult.isCorrect) {
            CorrectBadgeBackground
        } else {
            ReviewBadgeBackground
        }

    val badgeTextColor =
        if (questionResult.isCorrect) {
            CorrectAccent
        } else {
            ReviewAccent
        }

    val statusText =
        if (questionResult.isCorrect) {
            "Correct"
        } else {
            "Review needed"
        }

    val keyPointSummary =
        questionResult.explanation
            .normaliseFeedbackText()
            .toSingleSentenceSummary()

    val learningTipSummary =
        questionResult.learningTip
            .normaliseFeedbackText()
            .toSingleSentenceSummary()

    val hasExtraDetail =
        keyPointSummary !=
                questionResult.explanation
                    .normaliseFeedbackText() ||
                learningTipSummary !=
                questionResult.learningTip
                    .normaliseFeedbackText()

    var showFullFeedback by rememberSaveable(
        questionResult.questionId
    ) {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = cardBorder
        ),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
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
                        questionResult.dimension.displayName,
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ResultTextPrimary
                )

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = badgeBackground
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        style =
                            MaterialTheme.typography
                                .labelMedium,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color = badgeTextColor
                    )
                }
            }

            ResultInfoRow(
                label = "Your answer",
                value =
                    questionResult.selectedOptionText
                        ?: "No answer"
            )

            if (!questionResult.isCorrect) {
                ResultInfoRow(
                    label = "Recommended answer",
                    value =
                        questionResult.correctOptionText,
                    highlight = true
                )
            }

            FeedbackMiniCard(
                title = "Key point",
                supportingLabel = "WHY",
                text = if (showFullFeedback) {
                    questionResult.explanation
                        .normaliseFeedbackText()
                } else {
                    keyPointSummary
                },
                accentColor = badgeTextColor
            )

            FeedbackMiniCard(
                title = "Learning tip",
                supportingLabel = "TIP",
                text = if (showFullFeedback) {
                    questionResult.learningTip
                        .normaliseFeedbackText()
                } else {
                    learningTipSummary
                },
                accentColor = badgeTextColor
            )

            if (hasExtraDetail) {
                TextButton(
                    onClick = {
                        showFullFeedback =
                            !showFullFeedback
                    },
                    modifier =
                        Modifier.align(
                            Alignment.End
                        )
                ) {
                    Text(
                        text = if (showFullFeedback) {
                            "Show less"
                        } else {
                            "Show more"
                        },
                        fontWeight =
                            FontWeight.Bold,
                        color = badgeTextColor
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultInfoRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style =
                MaterialTheme.typography
                    .labelLarge,
            fontWeight = FontWeight.Bold,
            color = ResultTextPrimary
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography
                    .bodyLarge,
            fontWeight =
                if (highlight) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                },
            color = ResultTextSecondary
        )
    }
}

@Composable
private fun FeedbackMiniCard(
    title: String,
    supportingLabel: String,
    text: String,
    accentColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(
            alpha = 0.78f
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ResultBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp),
            verticalAlignment =
                Alignment.Top
        ) {
            // Use a compact badge to label the section
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(
                    alpha = 0.12f
                )
            ) {
                Text(
                    text = supportingLabel,
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 6.dp
                    ),
                    style =
                        MaterialTheme.typography
                            .labelSmall,
                    fontWeight =
                        FontWeight.ExtraBold,
                    color = accentColor
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = title,
                    style =
                        MaterialTheme.typography
                            .labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = ResultTextPrimary
                )

                Text(
                    text = text,
                    style =
                        MaterialTheme.typography
                            .bodyLarge,
                    color = ResultTextSecondary
                )
            }
        }
    }
}

@Composable
private fun MissingResultContent(
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ResultBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement =
                Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = ResultBorder
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "No completed evaluation was found.",
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color = ResultTextPrimary
                    )

                    Text(
                        text = "Return to the home screen and start a new module.",
                        style =
                            MaterialTheme.typography
                                .bodyLarge,
                        color = ResultTextSecondary
                    )

                    Button(
                        onClick = onBackHome,
                        shape =
                            RoundedCornerShape(18.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    ActionBlue
                            )
                    ) {
                        Text(
                            text = "Return Home",
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun performanceLabel(
    percentage: Int
): String {
    return when {
        percentage >= 80 -> {
            "Strong work"
        }

        percentage >= 60 -> {
            "Good progress"
        }

        else -> {
            "Needs review"
        }
    }
}

private fun performanceMessage(
    percentage: Int
): String {
    return when {
        percentage >= 80 -> {
            "You identified most of the important strengths and limitations."
        }

        percentage >= 60 -> {
            "Review the missed points and try again to strengthen your judgement."
        }

        else -> {
            "This case exposed some important gaps. Use the feedback before trying again."
        }
    }
}

// Clean up spacing before generating short summaries
private fun String.normaliseFeedbackText(): String {
    return trim()
        .replace(
            Regex("\\s+"),
            " "
        )
}

// Use the first full sentence as a concise summary
private fun String.toSingleSentenceSummary(): String {
    return split(
        Regex("(?<=[.!?])\\s+")
    )
        .firstOrNull()
        ?.trim()
        ?.takeIf { sentence ->
            sentence.isNotBlank()
        }
        ?: this
}

