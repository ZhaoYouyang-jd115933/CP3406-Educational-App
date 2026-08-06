package com.youyangzhao.sourcesense.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel

// Define the main settings palette
private val SettingsBackground =
    Color(0xFFFAF7FA)

private val SettingsCardBackground =
    Color(0xFFFFFFFF)

private val SettingsBorder =
    Color(0xFFEADFE6)

private val SettingsDivider =
    Color(0xFFF0E7EC)

private val SettingsPink =
    Color(0xFFB85F84)

private val SettingsPinkDark =
    Color(0xFF904662)

private val SettingsPinkSoft =
    Color(0xFFF7E7EF)

private val SettingsPinkLight =
    Color(0xFFFFF8FB)

private val SettingsTextPrimary =
    Color(0xFF302A32)

private val SettingsTextSecondary =
    Color(0xFF6A606A)

private val SettingsSwitchOff =
    Color(0xFFE5E1E6)

private val SettingsDanger =
    Color(0xFFA84E5A)

private val SettingsDangerSoft =
    Color(0xFFF9E5E7)

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    // Observe settings and locally stored learning data
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
                onRequestReset =
                    onRequestReset,
                onRequestClearEvaluationHistory =
                    onRequestClearEvaluationHistory,
                onRequestClearSourceReviews =
                    onRequestClearSourceReviews,
                onRequestClearAllLearningData =
                    onRequestClearAllLearningData,
                onClearError =
                    onClearError
            )
        }
    }
}

@Composable
private fun SettingsLoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = SettingsPink
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
    val settings =
        uiState.userSettings

    val statistics =
        uiState.learningStatistics

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

            SettingsHeader()
        }

        if (uiState.errorMessage != null) {
            item {
                SettingsErrorCard(
                    message =
                        uiState.errorMessage,
                    onDismiss =
                        onClearError
                )
            }
        }

        item {
            SectionHeading(
                title = "Learning Difficulty"
            )
        }

        item {
            DifficultyCard(
                selectedDifficulty =
                    settings.difficultyLevel,
                enabled =
                    uiState.canChangeSettings,
                onDifficultySelected =
                    onDifficultySelected
            )
        }

        item {
            SectionHeading(
                title = "Statistics Display"
            )
        }

        item {
            SettingsSectionCard {
                PreferenceSwitchRow(
                    title =
                        "Learning Recommendation",
                    checked =
                        settings
                            .showStatisticsRecommendation,
                    enabled =
                        uiState.canChangeSettings,
                    onCheckedChange =
                        onShowRecommendationChanged
                )

                SettingsDivider()

                PreferenceSwitchRow(
                    title = "Skill Accuracy",
                    checked =
                        settings
                            .showStatisticsSkillAccuracy,
                    enabled =
                        uiState.canChangeSettings,
                    onCheckedChange =
                        onShowSkillAccuracyChanged
                )

                SettingsDivider()

                PreferenceSwitchRow(
                    title =
                        "Real Source Practice",
                    checked =
                        settings
                            .showStatisticsSourcePractice,
                    enabled =
                        uiState.canChangeSettings,
                    onCheckedChange =
                        onShowSourcePracticeChanged
                )

                SettingsDivider()

                PreferenceSwitchRow(
                    title = "Recent Activity",
                    checked =
                        settings
                            .showStatisticsRecentActivity,
                    enabled =
                        uiState.canChangeSettings,
                    onCheckedChange =
                        onShowRecentActivityChanged
                )

                SettingsDivider()

                PreferenceSwitchRow(
                    title =
                        "Section Descriptions",
                    checked =
                        settings
                            .showStatisticsSectionDescriptions,
                    enabled =
                        uiState.canChangeSettings,
                    onCheckedChange =
                        onShowSectionDescriptionsChanged
                )
            }
        }

        item {
            SectionHeading(
                title = "Accessibility"
            )
        }

        item {
            SettingsSectionCard {
                PreferenceSwitchRow(
                    title = "Larger Text",
                    checked =
                        settings.useLargerText,
                    enabled =
                        uiState.canChangeSettings,
                    onCheckedChange =
                        onLargerTextChanged
                )

                SettingsDivider()

                PreferenceSwitchRow(
                    title = "Reduce Animations",
                    checked =
                        settings.reduceAnimations,
                    enabled =
                        uiState.canChangeSettings,
                    onCheckedChange =
                        onReduceAnimationsChanged
                )
            }
        }

        item {
            SectionHeading(
                title = "Feedback"
            )
        }

        item {
            SettingsSectionCard {
                PreferenceSwitchRow(
                    title = "Sound Feedback",
                    checked =
                        settings.soundFeedbackEnabled,
                    enabled =
                        uiState.canChangeSettings,
                    onCheckedChange =
                        onSoundFeedbackChanged
                )
            }
        }

        item {
            SectionHeading(
                title = "Data & Privacy"
            )
        }

        item {
            DataManagementCard(
                evaluationCount =
                    statistics.completedEvaluations,
                sourceReviewCount =
                    statistics.sourceReviewCount,
                canClearEvaluationHistory =
                    uiState
                        .canClearEvaluationHistory,
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
                enabled =
                    uiState.canResetSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape =
                    RoundedCornerShape(18.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = SettingsPink
                ),
                colors =
                    ButtonDefaults
                        .outlinedButtonColors(
                            contentColor =
                                SettingsPinkDark
                        )
            ) {
                Text(
                    text =
                        if (
                            uiState.isResettingSettings
                        ) {
                            "Restoring Defaults..."
                        } else {
                            "Restore Default Settings"
                        },
                    fontWeight =
                        FontWeight.Bold
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
private fun SettingsHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SettingsBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush =
                        Brush.linearGradient(
                            colors = listOf(
                                SettingsPinkLight,
                                SettingsPinkSoft
                            )
                        )
                )
        ) {
            // Add a quiet decorative shape behind the heading
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = 30.dp,
                        y = (-30).dp
                    )
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(
                            alpha = 0.45f
                        )
                    )
            )

            Text(
                text = "Settings",
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 22.dp
                ),
                style =
                    MaterialTheme.typography
                        .headlineLarge,
                fontWeight =
                    FontWeight.ExtraBold,
                color =
                    SettingsTextPrimary
            )
        }
    }
}

@Composable
private fun SectionHeading(
    title: String
) {
    Row(
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {
        // Use a small accent bar to separate each group
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(24.dp)
                .clip(
                    RoundedCornerShape(100.dp)
                )
                .background(SettingsPink)
        )

        Text(
            text = title,
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight =
                FontWeight.ExtraBold,
            color =
                SettingsTextPrimary
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                SettingsCardBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SettingsBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {
            DifficultyLevel.entries.forEach {
                    difficultyLevel ->

                DifficultyOptionRow(
                    difficultyLevel =
                        difficultyLevel,
                    selected =
                        difficultyLevel ==
                                selectedDifficulty,
                    enabled = enabled,
                    onSelected = {
                        onDifficultySelected(
                            difficultyLevel
                        )
                    }
                )
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onSelected
            ),
        shape = RoundedCornerShape(18.dp),
        color =
            if (selected) {
                SettingsPinkSoft
            } else {
                Color.Transparent
            },
        border =
            if (selected) {
                BorderStroke(
                    width = 1.dp,
                    color =
                        SettingsPink.copy(
                            alpha = 0.3f
                        )
                )
            } else {
                null
            }
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 12.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                enabled = enabled,
                colors =
                    RadioButtonDefaults.colors(
                        selectedColor =
                            SettingsPink,
                        unselectedColor =
                            SettingsTextSecondary
                    )
            )

            Text(
                text =
                    difficultyLevel.displayName,
                modifier =
                    Modifier.padding(start = 10.dp),
                style =
                    MaterialTheme.typography
                        .titleMedium,
                fontWeight =
                    if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.SemiBold
                    },
                color =
                    SettingsTextPrimary
            )
        }
    }
}

@Composable
private fun SettingsSectionCard(
    content:
    @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                SettingsCardBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SettingsBorder
        )
    ) {
        Column(
            content = content
        )
    }
}

@Composable
private fun PreferenceSwitchRow(
    title: String,
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
                onValueChange =
                    onCheckedChange
            )
            .padding(
                horizontal = 16.dp,
                vertical = 14.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style =
                MaterialTheme.typography
                    .titleMedium,
            fontWeight =
                FontWeight.SemiBold,
            color =
                SettingsTextPrimary
        )

        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor =
                    Color.White,
                checkedTrackColor =
                    SettingsPink,
                checkedBorderColor =
                    SettingsPink,
                uncheckedThumbColor =
                    SettingsTextSecondary,
                uncheckedTrackColor =
                    SettingsSwitchOff,
                uncheckedBorderColor =
                    SettingsTextSecondary,
                disabledCheckedTrackColor =
                    SettingsPink.copy(
                        alpha = 0.45f
                    ),
                disabledUncheckedTrackColor =
                    SettingsSwitchOff.copy(
                        alpha = 0.6f
                    )
            )
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(
            horizontal = 16.dp
        ),
        color = SettingsDivider
    )
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                SettingsCardBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SettingsBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Stored Learning Data",
                style =
                    MaterialTheme.typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    SettingsTextPrimary
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                DataCountCard(
                    value =
                        evaluationCount.toString(),
                    label = "Evaluations",
                    modifier =
                        Modifier.weight(1f)
                )

                DataCountCard(
                    value =
                        sourceReviewCount.toString(),
                    label = "Source Reviews",
                    modifier =
                        Modifier.weight(1f)
                )
            }

            OutlinedButton(
                onClick =
                    onRequestClearEvaluationHistory,
                enabled =
                    canClearEvaluationHistory &&
                            !isClearing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape =
                    RoundedCornerShape(18.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color =
                        SettingsTextSecondary
                ),
                colors =
                    ButtonDefaults
                        .outlinedButtonColors(
                            contentColor =
                                SettingsTextPrimary
                        )
            ) {
                Text(
                    text =
                        "Clear Evaluation History",
                    fontWeight =
                        FontWeight.Medium
                )
            }

            OutlinedButton(
                onClick =
                    onRequestClearSourceReviews,
                enabled =
                    canClearSourceReviews &&
                            !isClearing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape =
                    RoundedCornerShape(18.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color =
                        SettingsTextSecondary
                ),
                colors =
                    ButtonDefaults
                        .outlinedButtonColors(
                            contentColor =
                                SettingsTextPrimary
                        )
            ) {
                Text(
                    text =
                        "Clear Source Reviews",
                    fontWeight =
                        FontWeight.Medium
                )
            }

            TextButton(
                onClick =
                    onRequestClearAllLearningData,
                enabled =
                    canClearAllLearningData &&
                            !isClearing,
                modifier =
                    Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults
                        .textButtonColors(
                            contentColor =
                                SettingsDanger
                        )
            ) {
                Text(
                    text =
                        if (isClearing) {
                            "Clearing Data..."
                        } else {
                            "Clear All Learning Data"
                        },
                    fontWeight =
                        FontWeight.Bold
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
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                SettingsPinkSoft
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SettingsPink.copy(
                alpha = 0.12f
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style =
                    MaterialTheme.typography
                        .headlineSmall,
                fontWeight =
                    FontWeight.ExtraBold,
                color =
                    SettingsTextPrimary
            )

            Text(
                text = label,
                style =
                    MaterialTheme.typography
                        .labelMedium,
                fontWeight =
                    FontWeight.Medium,
                color =
                    SettingsTextSecondary
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
            containerColor =
                SettingsDangerSoft
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SettingsDanger.copy(
                alpha = 0.35f
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style =
                    MaterialTheme.typography
                        .bodyMedium,
                color =
                    SettingsTextPrimary
            )

            TextButton(
                onClick = onDismiss
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
                text =
                    "Restore Default Settings?",
                fontWeight =
                    FontWeight.Bold
            )
        },
        text = {
            Text(
                text =
                    "All preferences will return to their default values."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            SettingsPink
                    )
            ) {
                Text(
                    text = "Restore Defaults"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
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
                fontWeight =
                    FontWeight.Bold
            )
        },
        text = {
            Text(
                text = target.dialogMessage
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            SettingsDanger
                    )
            ) {
                Text(
                    text = target.confirmLabel
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

