package com.youyangzhao.sourcesense.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel

private val SettingsBackground =
    Color(0xFFF8F5FA)

private val SettingsCardBackground =
    Color(0xFFFFFFFF)

private val SettingsBorder =
    Color(0xFFE8DDE5)

private val SettingsPink =
    Color(0xFFB86186)

private val SettingsPinkSoft =
    Color(0xFFF6E7EE)

private val SettingsTextPrimary =
    Color(0xFF302A32)

private val SettingsTextSecondary =
    Color(0xFF625A65)

private val SettingsDanger =
    Color(0xFFA8505D)

private val SettingsDangerSoft =
    Color(0xFFF9E4E6)

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onDifficultySelected =
            viewModel::updateDifficultyLevel,
        onLargerTextChanged =
            viewModel::updateUseLargerText,
        onReduceAnimationsChanged =
            viewModel::updateReduceAnimations,
        onSoundFeedbackChanged =
            viewModel::updateSoundFeedback,
        onShowRecommendationChanged =
            viewModel::updateShowStatisticsRecommendation,
        onShowSkillAccuracyChanged =
            viewModel::updateShowStatisticsSkillAccuracy,
        onShowSourcePracticeChanged =
            viewModel::updateShowStatisticsSourcePractice,
        onShowRecentActivityChanged =
            viewModel::updateShowStatisticsRecentActivity,
        onShowSectionDescriptionsChanged =
            viewModel::updateShowStatisticsSectionDescriptions,
        onRequestReset =
            viewModel::requestResetSettings,
        onDismissReset =
            viewModel::dismissResetConfirmation,
        onConfirmReset =
            viewModel::confirmResetSettings,
        onRequestClearEvaluationHistory =
            viewModel::requestClearEvaluationHistory,
        onRequestClearSourceReviews =
            viewModel::requestClearSourceReviews,
        onRequestClearAllLearningData =
            viewModel::requestClearAllLearningData,
        onDismissDataClear =
            viewModel::dismissDataClearConfirmation,
        onConfirmDataClear =
            viewModel::confirmDataClear,
        onClearError =
            viewModel::clearError,
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState = SettingsUiState(
        isLoading = false
    ),
    onDifficultySelected: (DifficultyLevel) -> Unit = {},
    onLargerTextChanged: (Boolean) -> Unit = {},
    onReduceAnimationsChanged: (Boolean) -> Unit = {},
    onSoundFeedbackChanged: (Boolean) -> Unit = {},
    onShowRecommendationChanged: (Boolean) -> Unit = {},
    onShowSkillAccuracyChanged: (Boolean) -> Unit = {},
    onShowSourcePracticeChanged: (Boolean) -> Unit = {},
    onShowRecentActivityChanged: (Boolean) -> Unit = {},
    onShowSectionDescriptionsChanged: (Boolean) -> Unit = {},
    onRequestReset: () -> Unit = {},
    onDismissReset: () -> Unit = {},
    onConfirmReset: () -> Unit = {},
    onRequestClearEvaluationHistory: () -> Unit = {},
    onRequestClearSourceReviews: () -> Unit = {},
    onRequestClearAllLearningData: () -> Unit = {},
    onDismissDataClear: () -> Unit = {},
    onConfirmDataClear: () -> Unit = {},
    onClearError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (uiState.showResetConfirmation) {
        ResetSettingsDialog(
            onDismiss = onDismissReset,
            onConfirm = onConfirmReset
        )
    }

    if (uiState.showDataClearConfirmation) {
        ClearLearningDataDialog(
            target = uiState.dataClearTarget,
            onDismiss = onDismissDataClear,
            onConfirm = onConfirmDataClear
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = SettingsBackground
    ) {
        if (uiState.isLoading) {
            SettingsLoadingContent()
        } else {
            SettingsContent(
                uiState = uiState,
                onDifficultySelected =
                    onDifficultySelected,
                onLargerTextChanged =
                    onLargerTextChanged,
                onReduceAnimationsChanged =
                    onReduceAnimationsChanged,
                onSoundFeedbackChanged =
                    onSoundFeedbackChanged,
                onShowRecommendationChanged =
                    onShowRecommendationChanged,
                onShowSkillAccuracyChanged =
                    onShowSkillAccuracyChanged,
                onShowSourcePracticeChanged =
                    onShowSourcePracticeChanged,
                onShowRecentActivityChanged =
                    onShowRecentActivityChanged,
                onShowSectionDescriptionsChanged =
                    onShowSectionDescriptionsChanged,
                onRequestReset = onRequestReset,
                onRequestClearEvaluationHistory =
                    onRequestClearEvaluationHistory,
                onRequestClearSourceReviews =
                    onRequestClearSourceReviews,
                onRequestClearAllLearningData =
                    onRequestClearAllLearningData,
                onClearError = onClearError
            )
        }
    }
}

@Composable
private fun SettingsLoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = SettingsPink
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Loading settings...",
            style = MaterialTheme.typography.bodyLarge,
            color = SettingsTextSecondary
        )
    }
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onDifficultySelected: (DifficultyLevel) -> Unit,
    onLargerTextChanged: (Boolean) -> Unit,
    onReduceAnimationsChanged: (Boolean) -> Unit,
    onSoundFeedbackChanged: (Boolean) -> Unit,
    onShowRecommendationChanged: (Boolean) -> Unit,
    onShowSkillAccuracyChanged: (Boolean) -> Unit,
    onShowSourcePracticeChanged: (Boolean) -> Unit,
    onShowRecentActivityChanged: (Boolean) -> Unit,
    onShowSectionDescriptionsChanged: (Boolean) -> Unit,
    onRequestReset: () -> Unit,
    onRequestClearEvaluationHistory: () -> Unit,
    onRequestClearSourceReviews: () -> Unit,
    onRequestClearAllLearningData: () -> Unit,
    onClearError: () -> Unit
) {
    val settings = uiState.userSettings
    val statistics = uiState.learningStatistics

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
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = SettingsTextPrimary
            )

            Text(
                text =
                    "Adjust learning, accessibility and statistics preferences.",
                modifier = Modifier.padding(top = 5.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = SettingsTextSecondary
            )
        }

        if (uiState.errorMessage != null) {
            item {
                SettingsErrorCard(
                    message = uiState.errorMessage,
                    onDismiss = onClearError
                )
            }
        }

        item {
            SectionHeading(
                title = "Learning Difficulty",
                description =
                    "Choose the level of guidance and evidence complexity."
            )
        }

        item {
            DifficultyCard(
                selectedDifficulty =
                    settings.difficultyLevel,
                enabled = uiState.canChangeSettings,
                onDifficultySelected =
                    onDifficultySelected
            )
        }

        item {
            SectionHeading(
                title = "Statistics Display",
                description =
                    "Choose which optional sections appear in Learning Statistics."
            )
        }

        item {
            PreferenceCard {
                PreferenceSwitchRow(
                    title = "Learning Recommendation",
                    description =
                        "Show Recommended Next Focus and its practice button.",
                    checked =
                        settings.showStatisticsRecommendation,
                    enabled = uiState.canChangeSettings,
                    onCheckedChange =
                        onShowRecommendationChanged
                )

                HorizontalDivider()

                PreferenceSwitchRow(
                    title = "Skill Accuracy",
                    description =
                        "Show performance by evaluation skill.",
                    checked =
                        settings.showStatisticsSkillAccuracy,
                    enabled = uiState.canChangeSettings,
                    onCheckedChange =
                        onShowSkillAccuracyChanged
                )

                HorizontalDivider()

                PreferenceSwitchRow(
                    title = "Real Source Practice",
                    description =
                        "Show statistics from saved real-source reviews.",
                    checked =
                        settings.showStatisticsSourcePractice,
                    enabled = uiState.canChangeSettings,
                    onCheckedChange =
                        onShowSourcePracticeChanged
                )

                HorizontalDivider()

                PreferenceSwitchRow(
                    title = "Recent Activity",
                    description =
                        "Show recent evaluations and saved source reviews.",
                    checked =
                        settings.showStatisticsRecentActivity,
                    enabled = uiState.canChangeSettings,
                    onCheckedChange =
                        onShowRecentActivityChanged
                )

                HorizontalDivider()

                PreferenceSwitchRow(
                    title = "Section Descriptions",
                    description =
                        "Show short explanations below statistics headings.",
                    checked =
                        settings.showStatisticsSectionDescriptions,
                    enabled = uiState.canChangeSettings,
                    onCheckedChange =
                        onShowSectionDescriptionsChanged
                )
            }
        }

        item {
            SectionHeading(
                title = "Accessibility",
                description =
                    "Adjust how information and movement are presented."
            )
        }

        item {
            PreferenceCard {
                PreferenceSwitchRow(
                    title = "Larger Text",
                    description =
                        "Use larger text throughout the app.",
                    checked = settings.useLargerText,
                    enabled = uiState.canChangeSettings,
                    onCheckedChange =
                        onLargerTextChanged
                )

                HorizontalDivider()

                PreferenceSwitchRow(
                    title = "Reduce Animations",
                    description =
                        "Limit non-essential movement and transitions.",
                    checked = settings.reduceAnimations,
                    enabled = uiState.canChangeSettings,
                    onCheckedChange =
                        onReduceAnimationsChanged
                )
            }
        }

        item {
            SectionHeading(
                title = "Feedback",
                description =
                    "Control optional sound during learning activities."
            )
        }

        item {
            PreferenceCard {
                PreferenceSwitchRow(
                    title = "Sound Feedback",
                    description =
                        "Play optional sounds after learning actions.",
                    checked =
                        settings.soundFeedbackEnabled,
                    enabled = uiState.canChangeSettings,
                    onCheckedChange =
                        onSoundFeedbackChanged
                )
            }
        }

        item {
            SectionHeading(
                title = "Data & Privacy",
                description =
                    "Learning history is stored locally on this device."
            )
        }

        item {
            DataManagementCard(
                evaluationCount =
                    statistics.completedEvaluations,
                sourceReviewCount =
                    statistics.sourceReviewCount,
                canClearEvaluationHistory =
                    uiState.canClearEvaluationHistory,
                canClearSourceReviews =
                    uiState.canClearSourceReviews,
                canClearAllLearningData =
                    uiState.canClearAllLearningData,
                isClearing =
                    uiState.isClearingLearningData,
                onRequestClearEvaluationHistory =
                    onRequestClearEvaluationHistory,
                onRequestClearSourceReviews =
                    onRequestClearSourceReviews,
                onRequestClearAllLearningData =
                    onRequestClearAllLearningData
            )
        }

        item {
            OutlinedButton(
                onClick = onRequestReset,
                enabled = uiState.canResetSettings,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = SettingsPink
                )
            ) {
                Text(
                    text =
                        if (uiState.isResettingSettings) {
                            "Restoring Defaults..."
                        } else {
                            "Restore Default Settings"
                        },
                    fontWeight = FontWeight.SemiBold
                )
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
private fun SectionHeading(
    title: String,
    description: String
) {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = SettingsTextPrimary
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = SettingsTextSecondary
        )
    }
}

@Composable
private fun DifficultyCard(
    selectedDifficulty: DifficultyLevel,
    enabled: Boolean,
    onDifficultySelected: (DifficultyLevel) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = SettingsCardBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SettingsBorder
        )
    ) {
        Column {
            DifficultyLevel.entries.forEachIndexed {
                    index,
                    difficultyLevel ->

                DifficultyOptionRow(
                    difficultyLevel = difficultyLevel,
                    selected =
                        difficultyLevel == selectedDifficulty,
                    enabled = enabled,
                    onSelected = {
                        onDifficultySelected(difficultyLevel)
                    }
                )

                if (index < DifficultyLevel.entries.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyOptionRow(
    difficultyLevel: DifficultyLevel,
    selected: Boolean,
    enabled: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onSelected
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            verticalArrangement =
                Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = difficultyLevel.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SettingsTextPrimary
            )

            Text(
                text = difficultyLevel.description,
                style = MaterialTheme.typography.bodyMedium,
                color = SettingsTextSecondary
            )
        }
    }
}

@Composable
private fun PreferenceCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = SettingsCardBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SettingsBorder
        )
    ) {
        Column {
            content()
        }
    }
}

@Composable
private fun PreferenceSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement =
                Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SettingsTextPrimary
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = SettingsTextSecondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled
        )
    }
}

@Composable
private fun DataManagementCard(
    evaluationCount: Int,
    sourceReviewCount: Int,
    canClearEvaluationHistory: Boolean,
    canClearSourceReviews: Boolean,
    canClearAllLearningData: Boolean,
    isClearing: Boolean,
    onRequestClearEvaluationHistory: () -> Unit,
    onRequestClearSourceReviews: () -> Unit,
    onRequestClearAllLearningData: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = SettingsCardBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SettingsBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Stored Learning Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SettingsTextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                DataCountCard(
                    value = evaluationCount.toString(),
                    label = "Evaluations",
                    modifier = Modifier.weight(1f)
                )

                DataCountCard(
                    value = sourceReviewCount.toString(),
                    label = "Source Reviews",
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedButton(
                onClick = onRequestClearEvaluationHistory,
                enabled =
                    canClearEvaluationHistory && !isClearing,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(text = "Clear Evaluation History")
            }

            OutlinedButton(
                onClick = onRequestClearSourceReviews,
                enabled =
                    canClearSourceReviews && !isClearing,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(text = "Clear Source Reviews")
            }

            TextButton(
                onClick = onRequestClearAllLearningData,
                enabled =
                    canClearAllLearningData && !isClearing,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = SettingsDanger
                )
            ) {
                Text(
                    text =
                        if (isClearing) {
                            "Clearing Data..."
                        } else {
                            "Clear All Learning Data"
                        },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DataCountCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = SettingsPinkSoft
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = SettingsTextPrimary
            )

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = SettingsTextSecondary
            )
        }
    }
}

@Composable
private fun SettingsErrorCard(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SettingsDangerSoft
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SettingsDanger.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = SettingsTextPrimary
            )

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Dismiss")
            }
        }
    }
}

@Composable
private fun ResetSettingsDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Restore Default Settings?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text =
                    "Difficulty, accessibility, feedback and statistics display preferences will return to their default values."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SettingsPink
                )
            ) {
                Text(text = "Restore Defaults")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
private fun ClearLearningDataDialog(
    target: SettingsDataClearTarget?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (target == null) {
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = target.dialogTitle,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(text = target.dialogMessage)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SettingsDanger
                )
            ) {
                Text(text = target.confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

