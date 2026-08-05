package com.youyangzhao.sourcesense.ui.evaluation

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.domain.model.AnswerOption
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvidenceCase

@Composable
fun EvaluationRoute(
    viewModel: EvaluationViewModel,
    onEvaluationComplete: () -> Unit,
    reduceAnimations: Boolean = false,
    soundFeedbackEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    val view = LocalView.current

    LaunchedEffect(uiState.result) {
        if (uiState.result != null) {
            if (soundFeedbackEnabled) {
                view.playSoundEffect(
                    SoundEffectConstants.CLICK
                )
            }

            onEvaluationComplete()
        }
    }

    EvaluationScreen(
        uiState = uiState,
        reduceAnimations = reduceAnimations,
        onOptionSelected = { optionId ->
            if (soundFeedbackEnabled) {
                view.playSoundEffect(
                    SoundEffectConstants.CLICK
                )
            }

            viewModel.selectAnswer(optionId)
        },
        onSubmitAnswer =
            viewModel::submitCurrentAnswer,
        onPrevious =
            viewModel::moveToPreviousQuestion,
        onContinue =
            viewModel::moveToNextQuestion,
        onRetry =
            viewModel::retryLoading,
        onRetrySave =
            viewModel::retrySavingResult,
        modifier = modifier
    )
}

@Composable
fun EvaluationScreen(
    uiState: EvaluationUiState,
    reduceAnimations: Boolean = false,
    onOptionSelected: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    onPrevious: () -> Unit,
    onContinue: () -> Unit,
    onRetry: () -> Unit,
    onRetrySave: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Store nullable values once for safe state handling
    val evidenceCase = uiState.evidenceCase
    val currentQuestion = uiState.currentQuestion
    val errorMessage = uiState.errorMessage
    val saveErrorMessage =
        uiState.saveErrorMessage

    when {
        uiState.isLoading -> {
            LoadingContent(
                modifier = modifier
            )
        }

        errorMessage != null -> {
            ErrorContent(
                message = errorMessage,
                onRetry = onRetry,
                modifier = modifier
            )
        }

        saveErrorMessage != null -> {
            SaveErrorContent(
                message = saveErrorMessage,
                onRetrySave = onRetrySave,
                modifier = modifier
            )
        }

        evidenceCase == null ||
                currentQuestion == null -> {
            ErrorContent(
                message =
                    "The evaluation content is unavailable.",
                onRetry = onRetry,
                modifier = modifier
            )
        }

        else -> {
            EvaluationContent(
                evidenceCase = evidenceCase,
                question = currentQuestion,
                questionNumber =
                    uiState.questionNumber,
                totalQuestions =
                    uiState.totalQuestions,
                progress = uiState.progress,
                selectedOptionId =
                    uiState.selectedOptionId,
                isFirstQuestion =
                    uiState.isFirstQuestion,
                isLastQuestion =
                    uiState.isLastQuestion,
                isCurrentAnswerSubmitted =
                    uiState.isCurrentAnswerSubmitted,
                isCurrentAnswerCorrect =
                    uiState.isCurrentAnswerCorrect,
                isSaving =
                    uiState.isSaving,
                canSubmitAnswer =
                    uiState.canSubmitAnswer,
                canContinue =
                    uiState.canContinue,
                reduceAnimations =
                    reduceAnimations,
                onOptionSelected =
                    onOptionSelected,
                onSubmitAnswer =
                    onSubmitAnswer,
                onPrevious =
                    onPrevious,
                onContinue =
                    onContinue,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement =
            Arrangement.Center,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Loading evidence case...",
            style =
                MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement =
            Arrangement.Center,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = "Unable to load evaluation",
            style =
                MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = message,
            style =
                MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onRetry
        ) {
            Text(
                text = "Try Again"
            )
        }
    }
}

@Composable
private fun SaveErrorContent(
    message: String,
    onRetrySave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement =
            Arrangement.Center,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = "Unable to save result",
            style =
                MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = message,
            style =
                MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onRetrySave
        ) {
            Text(
                text = "Try Saving Again"
            )
        }
    }
}

@Composable
private fun EvaluationContent(
    evidenceCase: EvidenceCase,
    question: EvaluationQuestion,
    questionNumber: Int,
    totalQuestions: Int,
    progress: Float,
    selectedOptionId: String?,
    isFirstQuestion: Boolean,
    isLastQuestion: Boolean,
    isCurrentAnswerSubmitted: Boolean,
    isCurrentAnswerCorrect: Boolean?,
    isSaving: Boolean,
    canSubmitAnswer: Boolean,
    canContinue: Boolean,
    reduceAnimations: Boolean,
    onOptionSelected: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    onPrevious: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(
        questionNumber,
        reduceAnimations
    ) {
        // Respect the reduced motion preference
        if (reduceAnimations) {
            listState.scrollToItem(0)
        } else {
            listState.animateScrollToItem(0)
        }
    }

    val correctAnswerText =
        question.options
            .firstOrNull { option ->
                option.id ==
                        question.correctOptionId
            }
            ?.text

    val primaryButtonText =
        when {
            isSaving -> {
                "Saving..."
            }

            !isCurrentAnswerSubmitted -> {
                "Check Answer"
            }

            isLastQuestion -> {
                "View Result"
            }

            else -> {
                "Next Question"
            }
        }

    val primaryButtonEnabled =
        if (isCurrentAnswerSubmitted) {
            canContinue
        } else {
            canSubmitAnswer
        }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        item {
            Text(
                text =
                    "Question $questionNumber of $totalQuestions",
                style =
                    MaterialTheme.typography.labelLarge,
                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier =
                    Modifier.fillMaxWidth()
            )
        }

        item {
            EvidenceCaseCard(
                evidenceCase = evidenceCase
            )
        }

        item {
            QuestionCard(
                question = question,
                selectedOptionId =
                    selectedOptionId,
                isAnswerSubmitted =
                    isCurrentAnswerSubmitted,
                onOptionSelected =
                    onOptionSelected
            )
        }

        if (isCurrentAnswerSubmitted) {
            item {
                // Show feedback before moving to another question
                AnswerFeedbackCard(
                    isCorrect =
                        isCurrentAnswerCorrect == true,
                    correctAnswerText =
                        correctAnswerText,
                    explanation =
                        question.explanation,
                    learningTip =
                        question.learningTip
                )
            }
        }

        item {
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled =
                        !isFirstQuestion &&
                                !isSaving,
                    modifier =
                        Modifier.weight(1f)
                ) {
                    Text(
                        text = "Previous"
                    )
                }

                Button(
                    onClick = {
                        if (
                            isCurrentAnswerSubmitted
                        ) {
                            onContinue()
                        } else {
                            onSubmitAnswer()
                        }
                    },
                    enabled =
                        primaryButtonEnabled,
                    modifier =
                        Modifier.weight(1f)
                ) {
                    Text(
                        text = primaryButtonText
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
private fun EvidenceCaseCard(
    evidenceCase: EvidenceCase
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme
                    .surfaceVariant
        )
    ) {
        Column(
            modifier =
                Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Research Question",
                style =
                    MaterialTheme.typography.labelLarge,
                color =
                    MaterialTheme.colorScheme.primary,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    evidenceCase.researchQuestion,
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = evidenceCase.title,
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = evidenceCase.authors,
                style =
                    MaterialTheme.typography.bodyMedium
            )

            Text(
                text =
                    "${evidenceCase.publication}, " +
                            "${evidenceCase.publishedYear}",
                style =
                    MaterialTheme.typography.bodyMedium
            )

            Text(
                text = evidenceCase.excerpt,
                style =
                    MaterialTheme.typography.bodyLarge
            )

            SourceDetail(
                label = "Method",
                value =
                    evidenceCase.methodSummary
            )

            SourceDetail(
                label = "Sample",
                value =
                    evidenceCase.sampleSummary
            )

            Text(
                text =
                    evidenceCase.sourceNote,
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SourceDetail(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style =
                MaterialTheme.typography.labelMedium,
            fontWeight =
                FontWeight.Bold
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun QuestionCard(
    question: EvaluationQuestion,
    selectedOptionId: String?,
    isAnswerSubmitted: Boolean,
    onOptionSelected: (String) -> Unit
) {
    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {
        Column(
            modifier =
                Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text =
                    question.dimension.displayName,
                style =
                    MaterialTheme.typography.labelLarge,
                color =
                    MaterialTheme.colorScheme.primary,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = question.prompt,
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.SemiBold
            )

            question.options.forEach { option ->
                AnswerOptionRow(
                    option = option,
                    selected =
                        option.id ==
                                selectedOptionId,
                    isCorrectOption =
                        option.id ==
                                question.correctOptionId,
                    isAnswerSubmitted =
                        isAnswerSubmitted,
                    onSelected = {
                        onOptionSelected(option.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun AnswerOptionRow(
    option: AnswerOption,
    selected: Boolean,
    isCorrectOption: Boolean,
    isAnswerSubmitted: Boolean,
    onSelected: () -> Unit
) {
    val containerColor =
        when {
            isAnswerSubmitted &&
                    isCorrectOption -> {
                MaterialTheme.colorScheme
                    .secondaryContainer
            }

            isAnswerSubmitted &&
                    selected -> {
                MaterialTheme.colorScheme
                    .errorContainer
            }

            selected -> {
                MaterialTheme.colorScheme
                    .primaryContainer
            }

            else -> {
                MaterialTheme.colorScheme.surface
            }
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                enabled =
                    !isAnswerSubmitted,
                onClick = onSelected,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                enabled =
                    !isAnswerSubmitted
            )

            Text(
                text = option.text,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                style =
                    MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun AnswerFeedbackCard(
    isCorrect: Boolean,
    correctAnswerText: String?,
    explanation: String,
    learningTip: String
) {
    val containerColor =
        if (isCorrect) {
            MaterialTheme.colorScheme
                .secondaryContainer
        } else {
            MaterialTheme.colorScheme
                .errorContainer
        }

    val contentColor =
        if (isCorrect) {
            MaterialTheme.colorScheme
                .onSecondaryContainer
        } else {
            MaterialTheme.colorScheme
                .onErrorContainer
        }

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier =
                Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (isCorrect) {
                    "Correct"
                } else {
                    "Not Quite"
                },
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.Bold
            )

            if (
                !isCorrect &&
                correctAnswerText != null
            ) {
                Text(
                    text = "Correct Answer",
                    style =
                        MaterialTheme.typography.labelLarge,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text = correctAnswerText,
                    style =
                        MaterialTheme.typography.bodyLarge
                )
            }

            Text(
                text = "Explanation",
                style =
                    MaterialTheme.typography.labelLarge,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = explanation,
                style =
                    MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Learning Tip",
                style =
                    MaterialTheme.typography.labelLarge,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = learningTip,
                style =
                    MaterialTheme.typography.bodyLarge
            )
        }
    }
}

