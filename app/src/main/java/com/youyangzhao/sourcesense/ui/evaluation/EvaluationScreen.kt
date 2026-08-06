package com.youyangzhao.sourcesense.ui.evaluation

import android.view.SoundEffectConstants
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.domain.model.AnswerOption
import com.youyangzhao.sourcesense.domain.model.EvaluationQuestion
import com.youyangzhao.sourcesense.domain.model.EvidenceCase

// Define the overall evaluation page palette
private val EvaluationBackground =
    Color(0xFFF8F5FA)

private val EvaluationAccent =
    Color(0xFFB56286)

private val EvaluationAccentDark =
    Color(0xFF934F6C)

private val EvaluationAccentSoft =
    Color(0xFFF4E6EE)

private val EvaluationAccentLight =
    Color(0xFFFFF8FB)

private val EvaluationBorder =
    Color(0xFFE8DCE4)

private val EvaluationTextPrimary =
    Color(0xFF322B33)

private val EvaluationTextSecondary =
    Color(0xFF5C5560)

// Define answer-state colors
private val OptionSelectedColor =
    Color(0xFFF5E7EE)

private val OptionCorrectColor =
    Color(0xFFE4F3EC)

private val OptionIncorrectColor =
    Color(0xFFF9E2E4)

private val OptionDefaultColor =
    Color(0xFFFFFFFF)

@Composable
fun EvaluationRoute(
    viewModel: EvaluationViewModel,
    onEvaluationComplete: () -> Unit,
    reduceAnimations: Boolean = false,
    soundFeedbackEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Observe the latest evaluation state from the ViewModel
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
    // Read nullable values once to simplify the state branches
    val evidenceCase = uiState.evidenceCase
    val currentQuestion = uiState.currentQuestion
    val errorMessage = uiState.errorMessage
    val saveErrorMessage =
        uiState.saveErrorMessage

    Surface(
        modifier = modifier.fillMaxSize(),
        color = EvaluationBackground
    ) {
        when {
            uiState.isLoading -> {
                LoadingContent()
            }

            errorMessage != null -> {
                ErrorContent(
                    message = errorMessage,
                    onRetry = onRetry
                )
            }

            saveErrorMessage != null -> {
                SaveErrorContent(
                    message = saveErrorMessage,
                    onRetrySave = onRetrySave
                )
            }

            evidenceCase == null ||
                    currentQuestion == null -> {
                ErrorContent(
                    message =
                        "The evaluation content is unavailable.",
                    onRetry = onRetry
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
                        onContinue
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
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
            color = EvaluationAccent
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Loading evidence case...",
            style =
                MaterialTheme.typography.bodyLarge,
            color = EvaluationTextSecondary
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement =
            Arrangement.Center,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(
                width = 1.dp,
                color = EvaluationBorder
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Unable to load evaluation",
                    style =
                        MaterialTheme.typography
                            .headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = EvaluationTextPrimary
                )

                Text(
                    text = message,
                    style =
                        MaterialTheme.typography
                            .bodyLarge,
                    color = EvaluationTextSecondary
                )

                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(18.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                EvaluationAccent
                        )
                ) {
                    Text(text = "Try Again")
                }
            }
        }
    }
}

@Composable
private fun SaveErrorContent(
    message: String,
    onRetrySave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement =
            Arrangement.Center,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(
                width = 1.dp,
                color = EvaluationBorder
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Unable to save result",
                    style =
                        MaterialTheme.typography
                            .headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = EvaluationTextPrimary
                )

                Text(
                    text = message,
                    style =
                        MaterialTheme.typography
                            .bodyLarge,
                    color = EvaluationTextSecondary
                )

                Button(
                    onClick = onRetrySave,
                    shape = RoundedCornerShape(18.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                EvaluationAccent
                        )
                ) {
                    Text(text = "Try Saving Again")
                }
            }
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
    onContinue: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(
        questionNumber,
        reduceAnimations
    ) {
        // Return to the top for each new question
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
            QuestionProgressHeader(
                questionNumber = questionNumber,
                totalQuestions = totalQuestions,
                progress = progress
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
                // Use the same expandable feedback pattern for every question
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled =
                        !isFirstQuestion &&
                                !isSaving,
                    modifier =
                        Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(text = "Previous")
                }

                Button(
                    onClick = {
                        if (isCurrentAnswerSubmitted) {
                            onContinue()
                        } else {
                            onSubmitAnswer()
                        }
                    },
                    enabled =
                        primaryButtonEnabled,
                    modifier =
                        Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                EvaluationAccent
                        )
                ) {
                    Text(text = primaryButtonText)
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
private fun QuestionProgressHeader(
    questionNumber: Int,
    totalQuestions: Int,
    progress: Float
) {
    val safeProgress =
        progress.coerceIn(
            minimumValue = 0f,
            maximumValue = 1f
        )

    Column(
        verticalArrangement =
            Arrangement.spacedBy(8.dp)
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
                    "Question $questionNumber of $totalQuestions",
                style =
                    MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = EvaluationTextPrimary
            )

            Surface(
                shape = RoundedCornerShape(100.dp),
                color = EvaluationAccentSoft
            ) {
                Text(
                    text =
                        "${(safeProgress * 100).toInt()}%",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    style =
                        MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = EvaluationAccentDark
                )
            }
        }

        LinearProgressIndicator(
            progress = { safeProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(
                    RoundedCornerShape(100.dp)
                ),
            color = EvaluationAccentDark,
            trackColor = EvaluationAccentSoft
        )
    }
}

@Composable
private fun EvidenceCaseCard(
    evidenceCase: EvidenceCase
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = EvaluationBorder
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White,
                            EvaluationAccentLight
                        )
                    )
                )
        ) {
            // Add a soft decorative highlight
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = 28.dp,
                        y = (-28).dp
                    )
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        EvaluationAccentSoft.copy(
                            alpha = 0.8f
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                SectionChip(
                    text = "Research Question"
                )

                // Put the main research question first
                Text(
                    text =
                        evidenceCase.researchQuestion,
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EvaluationTextPrimary
                )

                Text(
                    text = evidenceCase.title,
                    style =
                        MaterialTheme.typography
                            .headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = EvaluationTextPrimary
                )

                Text(
                    text = evidenceCase.authors,
                    style =
                        MaterialTheme.typography
                            .bodyMedium,
                    color = EvaluationTextSecondary
                )

                Text(
                    text =
                        "${evidenceCase.publication}, " +
                                "${evidenceCase.publishedYear}",
                    style =
                        MaterialTheme.typography
                            .bodyMedium,
                    color = EvaluationTextSecondary
                )

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(
                        alpha = 0.92f
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = EvaluationBorder
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Study Summary",
                            style =
                                MaterialTheme.typography
                                    .labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = EvaluationAccentDark
                        )

                        Text(
                            text = evidenceCase.excerpt,
                            style =
                                MaterialTheme.typography
                                    .bodyLarge,
                            color = EvaluationTextPrimary
                        )
                    }
                }

                EvidenceHighlightCard(
                    label = "Method",
                    value =
                        evidenceCase.methodSummary
                )

                EvidenceHighlightCard(
                    label = "Sample",
                    value =
                        evidenceCase.sampleSummary
                )
            }
        }
    }
}

@Composable
private fun SectionChip(
    text: String
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = EvaluationAccentSoft
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            style =
                MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = EvaluationAccentDark
        )
    }
}

@Composable
private fun EvidenceHighlightCard(
    label: String,
    value: String
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.9f),
        border = BorderStroke(
            width = 1.dp,
            color = EvaluationBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement =
                Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style =
                    MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = EvaluationAccentDark
            )

            Text(
                text = value,
                style =
                    MaterialTheme.typography.bodyMedium,
                color = EvaluationTextPrimary
            )
        }
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = EvaluationBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {
            SectionChip(
                text =
                    question.dimension.displayName
            )

            // Make the question prompt the main focal point
            Text(
                text = question.prompt,
                style =
                    MaterialTheme.typography
                        .titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = EvaluationTextPrimary
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
                OptionCorrectColor
            }

            isAnswerSubmitted &&
                    selected -> {
                OptionIncorrectColor
            }

            selected -> {
                OptionSelectedColor
            }

            else -> {
                OptionDefaultColor
            }
        }

    val borderColor =
        when {
            isAnswerSubmitted &&
                    isCorrectOption -> {
                Color(0xFF8BB79F)
            }

            isAnswerSubmitted &&
                    selected -> {
                Color(0xFFE0A2A8)
            }

            selected -> {
                EvaluationAccent
            }

            else -> {
                EvaluationBorder
            }
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                enabled = !isAnswerSubmitted,
                onClick = onSelected,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
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
                enabled = !isAnswerSubmitted,
                colors =
                    RadioButtonDefaults.colors(
                        selectedColor =
                            EvaluationAccentDark
                    )
            )

            Text(
                text = option.text,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                style =
                    MaterialTheme.typography.bodyLarge,
                color = EvaluationTextPrimary
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
    // Use one shared expand state so both sections expand together
    var showFullFeedback by rememberSaveable(
        isCorrect,
        explanation,
        learningTip
    ) {
        mutableStateOf(false)
    }

    val normalisedExplanation =
        explanation.normaliseFeedbackText()

    val normalisedLearningTip =
        learningTip.normaliseFeedbackText()

    val compactExplanation =
        normalisedExplanation.toCompactFeedback()

    val compactLearningTip =
        normalisedLearningTip.toCompactFeedback()

    val hasAdditionalDetail =
        compactExplanation !=
                normalisedExplanation ||
                compactLearningTip !=
                normalisedLearningTip

    val containerColor =
        if (isCorrect) {
            OptionCorrectColor
        } else {
            OptionIncorrectColor
        }

    val borderColor =
        if (isCorrect) {
            Color(0xFF8BB79F)
        } else {
            Color(0xFFE0A2A8)
        }

    val statusColor =
        if (isCorrect) {
            Color(0xFF357A5C)
        } else {
            Color(0xFFA64F59)
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {
            // Keep the main result clear and immediate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = if (isCorrect) {
                        "Correct"
                    } else {
                        "Not Quite"
                    },
                    style =
                        MaterialTheme.typography
                            .titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = EvaluationTextPrimary
                )

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color =
                        statusColor.copy(
                            alpha = 0.12f
                        )
                ) {
                    Text(
                        text = if (isCorrect) {
                            "GOOD CHOICE"
                        } else {
                            "REVIEW"
                        },
                        modifier = Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 6.dp
                        ),
                        style =
                            MaterialTheme.typography
                                .labelSmall,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color = statusColor
                    )
                }
            }

            if (
                !isCorrect &&
                correctAnswerText != null
            ) {
                // Keep the correct answer separate and always fully visible
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(
                        alpha = 0.82f
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = borderColor
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Correct Answer",
                            style =
                                MaterialTheme.typography
                                    .labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )

                        Text(
                            text = correctAnswerText,
                            style =
                                MaterialTheme.typography
                                    .bodyLarge,
                            fontWeight =
                                FontWeight.SemiBold,
                            color =
                                EvaluationTextPrimary
                        )
                    }
                }
            }

            // Show the reasoning section with shared expand/collapse behaviour
            FeedbackSection(
                title = "Explanation",
                supportingLabel = "WHY",
                text = if (showFullFeedback) {
                    normalisedExplanation
                } else {
                    compactExplanation
                },
                accentColor = statusColor
            )

            // Show the reusable takeaway in the same expandable pattern
            FeedbackSection(
                title = "Learning Tip",
                supportingLabel = "REMEMBER",
                text = if (showFullFeedback) {
                    normalisedLearningTip
                } else {
                    compactLearningTip
                },
                accentColor =
                    EvaluationAccentDark
            )

            if (hasAdditionalDetail) {
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
                        fontWeight = FontWeight.Bold,
                        color = EvaluationAccentDark
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackSection(
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
            color = EvaluationBorder.copy(
                alpha = 0.9f
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp),
            verticalAlignment =
                Alignment.Top
        ) {
            // Use a small visual label to clarify the section purpose
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
                // Keep these titles stable for GUI tests
                Text(
                    text = title,
                    style =
                        MaterialTheme.typography
                            .labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = EvaluationTextPrimary
                )

                Text(
                    text = text,
                    style =
                        MaterialTheme.typography
                            .bodyLarge,
                    color = EvaluationTextSecondary
                )
            }
        }
    }
}

// Return only the first complete sentence for the compact view
private fun String.toCompactFeedback(): String {
    return split(
        Regex(
            "(?<=[.!?])\\s+"
        )
    )
        .firstOrNull()
        ?.takeIf { sentence ->
            sentence.isNotBlank()
        }
        ?: this
}

// Remove repeated spaces and unnecessary line breaks
private fun String.normaliseFeedbackText(): String {
    return trim()
        .replace(
            Regex("\\s+"),
            " "
        )
}
