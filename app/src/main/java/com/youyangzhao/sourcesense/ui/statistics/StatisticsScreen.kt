package com.youyangzhao.sourcesense.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.domain.model.RecentAttempt
import com.youyangzhao.sourcesense.domain.model.SkillAccuracy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatisticsRoute(
    viewModel: StatisticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StatisticsScreen(
        uiState = uiState,
        onRequestClearHistory = viewModel::requestClearHistory,
        onDismissClearConfirmation =
            viewModel::dismissClearConfirmation,
        onConfirmClearHistory = viewModel::confirmClearHistory,
        onClearError = viewModel::clearError,
        modifier = modifier
    )
}

@Composable
fun StatisticsScreen(
    uiState: StatisticsUiState = StatisticsUiState(
        isLoading = false
    ),
    onRequestClearHistory: () -> Unit = {},
    onDismissClearConfirmation: () -> Unit = {},
    onConfirmClearHistory: () -> Unit = {},
    onClearError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (uiState.showClearConfirmation) {
        ClearHistoryDialog(
            onDismiss = onDismissClearConfirmation,
            onConfirm = onConfirmClearHistory
        )
    }

    when {
        uiState.isLoading -> {
            StatisticsLoadingContent(
                modifier = modifier
            )
        }

        uiState.errorMessage != null && !uiState.hasData -> {
            StatisticsErrorContent(
                message = uiState.errorMessage,
                onDismiss = onClearError,
                modifier = modifier
            )
        }

        !uiState.hasData -> {
            EmptyStatisticsContent(
                modifier = modifier
            )
        }

        else -> {
            StatisticsContent(
                uiState = uiState,
                onRequestClearHistory =
                    onRequestClearHistory,
                onClearError = onClearError,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun StatisticsLoadingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Loading learning statistics...",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun StatisticsErrorContent(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Unable to load statistics",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onDismiss) {
            Text(text = "Dismiss")
        }
    }
}

@Composable
private fun EmptyStatisticsContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Learning History Yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = """
                Complete an evidence evaluation to see your scores, recent attempts and skill accuracy.
            """.trimIndent(),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatisticsContent(
    uiState: StatisticsUiState,
    onRequestClearHistory: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statistics = uiState.statistics

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Text(
                text = "Learning Statistics",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Track your evidence evaluation progress and identify skills that need more practice.",
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
            SummaryCard(
                completedEvaluations =
                    statistics.completedEvaluations,
                averagePercentage =
                    statistics.averagePercentage,
                bestPercentage =
                    statistics.bestPercentage
            )
        }

        item {
            SectionHeading(
                title = "Skill Accuracy",
                description = """
                    Accuracy is calculated from every question you have answered in each evaluation dimension.
                """.trimIndent()
            )
        }

        if (statistics.skillAccuracies.isEmpty()) {
            item {
                Text(
                    text = "No skill data is available yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    skillAccuracy = skillAccuracy
                )
            }
        }

        item {
            SectionHeading(
                title = "Recent Attempts",
                description = "Your five most recent completed evaluations."
            )
        }

        items(
            items = statistics.recentAttempts,
            key = { attempt ->
                attempt.id
            }
        ) { attempt ->
            RecentAttemptCard(
                attempt = attempt
            )
        }

        item {
            HorizontalDivider()
        }

        item {
            OutlinedButton(
                onClick = onRequestClearHistory,
                enabled = uiState.canClearHistory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (uiState.isClearingHistory) {
                        "Clearing History..."
                    } else {
                        "Clear Learning History"
                    }
                )
            }
        }

        item {
            Text(
                text = """
                    Learning history is stored locally on this device and can be deleted at any time.
                """.trimIndent(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SummaryCard(
    completedEvaluations: Int,
    averagePercentage: Int,
    bestPercentage: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Progress Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                SummaryMetric(
                    value = completedEvaluations.toString(),
                    label = "Completed",
                    modifier = Modifier.weight(1f)
                )

                SummaryMetric(
                    value = "$averagePercentage%",
                    label = "Average",
                    modifier = Modifier.weight(1f)
                )

                SummaryMetric(
                    value = "$bestPercentage%",
                    label = "Best",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SectionHeading(
    title: String,
    description: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SkillAccuracyCard(
    skillAccuracy: SkillAccuracy
) {
    val percentageProgress =
        skillAccuracy.percentage.coerceIn(
            minimumValue = 0,
            maximumValue = 100
        ) / 100f

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =
                        skillAccuracy.dimension.displayName,
                    modifier = Modifier.weight(1f),
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${skillAccuracy.percentage}%",
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = { percentageProgress },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = """
                    ${skillAccuracy.correctAnswers} correct out of ${skillAccuracy.totalAnswers} answers
                """.trimIndent(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RecentAttemptCard(
    attempt: RecentAttempt
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatEvidenceCaseName(
                        attempt.evidenceCaseId
                    ),
                    modifier = Modifier.weight(1f),
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${attempt.percentage}%",
                    style =
                        MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = """
                    Score: ${attempt.score} / ${attempt.totalQuestions}
                """.trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = formatCompletedTime(
                    attempt.completedAt
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
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

@Composable
private fun ClearHistoryDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Clear Learning History?")
        },
        text = {
            Text(
                text = """
                    This will permanently delete all evaluation attempts and skill statistics stored on this device.
                """.trimIndent()
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "Clear History")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
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

