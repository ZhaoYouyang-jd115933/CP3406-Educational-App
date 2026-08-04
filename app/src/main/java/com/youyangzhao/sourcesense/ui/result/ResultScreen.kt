package com.youyangzhao.sourcesense.ui.result

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.domain.model.EvaluationResult
import com.youyangzhao.sourcesense.domain.model.QuestionResult
import com.youyangzhao.sourcesense.ui.evaluation.EvaluationViewModel

@Composable
fun ResultRoute(
    viewModel: EvaluationViewModel,
    onTryAgain: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Prevent returning to a completed evaluation state
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
            ResultSummaryCard(result = result)
        }

        item {
            Text(
                text = "Question Feedback",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBackHome,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Home")
                }

                Button(
                    onClick = onTryAgain,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Try Again")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResultSummaryCard(
    result: EvaluationResult
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Evaluation Complete",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${result.score} / ${result.totalQuestions}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${result.percentage}% correct",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = performanceMessage(result.percentage),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun QuestionFeedbackCard(
    questionResult: QuestionResult
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (questionResult.isCorrect) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = questionResult.dimension.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (questionResult.isCorrect) {
                    "Correct"
                } else {
                    "Review needed"
                },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Your answer: ${questionResult.selectedOptionText ?: "No answer"}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (!questionResult.isCorrect) {
                Text(
                    text = "Recommended answer: ${questionResult.correctOptionText}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = questionResult.explanation,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Learning tip: ${questionResult.learningTip}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MissingResultContent(
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No completed evaluation was found.",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onBackHome) {
            Text(text = "Return Home")
        }
    }
}

private fun performanceMessage(
    percentage: Int
): String {
    return when {
        percentage >= 80 -> {
            "Strong work. You identified most of the source's important strengths and limitations."
        }

        percentage >= 60 -> {
            "Good progress. Review the explanations to strengthen the areas you missed."
        }

        else -> {
            "This case exposed several important gaps. Use the feedback before trying again."
        }
    }
}

