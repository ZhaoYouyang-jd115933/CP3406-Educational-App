package com.youyangzhao.sourcesense.ui.settings

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.domain.model.DifficultyLevel

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onDifficultySelected = viewModel::updateDifficultyLevel,
        onLargerTextChanged = viewModel::updateUseLargerText,
        onReduceAnimationsChanged =
            viewModel::updateReduceAnimations,
        onSoundFeedbackChanged =
            viewModel::updateSoundFeedback,
        onRequestReset = viewModel::requestResetSettings,
        onDismissReset = viewModel::dismissResetConfirmation,
        onConfirmReset = viewModel::confirmResetSettings,
        onClearError = viewModel::clearError,
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
    onRequestReset: () -> Unit = {},
    onDismissReset: () -> Unit = {},
    onConfirmReset: () -> Unit = {},
    onClearError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (uiState.showResetConfirmation) {
        ResetSettingsDialog(
            onDismiss = onDismissReset,
            onConfirm = onConfirmReset
        )
    }

    if (uiState.isLoading) {
        SettingsLoadingContent(
            modifier = modifier
        )
    } else {
        SettingsContent(
            uiState = uiState,
            onDifficultySelected = onDifficultySelected,
            onLargerTextChanged = onLargerTextChanged,
            onReduceAnimationsChanged =
                onReduceAnimationsChanged,
            onSoundFeedbackChanged =
                onSoundFeedbackChanged,
            onRequestReset = onRequestReset,
            onClearError = onClearError,
            modifier = modifier
        )
    }
}

@Composable
private fun SettingsLoadingContent(
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
            text = "Loading settings...",
            style = MaterialTheme.typography.bodyLarge
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
    onRequestReset: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings = uiState.userSettings

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
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Adjust your learning experience and accessibility preferences.",
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                description = "Choose the level of guidance and evidence complexity."
            )
        }

        item {
            DifficultyCard(
                selectedDifficulty = settings.difficultyLevel,
                enabled = uiState.canChangeSettings,
                onDifficultySelected = onDifficultySelected
            )
        }

        item {
            SectionHeading(
                title = "Accessibility",
                description = "Adjust how information and movement are presented."
            )
        }

        item {
            PreferenceCard {
                PreferenceSwitchRow(
                    title = "Larger Text",
                    description = "Use larger text throughout the app.",
                    checked = settings.useLargerText,
                    enabled = uiState.canChangeSettings,
                    onCheckedChange = onLargerTextChanged
                )

                HorizontalDivider()

                PreferenceSwitchRow(
                    title = "Reduce Animations",
                    description = "Limit non-essential movement and transitions.",
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
                description = "Control optional learning feedback."
            )
        }

        item {
            PreferenceCard {
                PreferenceSwitchRow(
                    title = "Sound Feedback",
                    description = "Play optional sounds after learning activities.",
                    checked = settings.soundFeedbackEnabled,
                    enabled = uiState.canChangeSettings,
                    onCheckedChange =
                        onSoundFeedbackChanged
                )
            }
        }

        item {
            PrivacyCard()
        }

        item {
            OutlinedButton(
                onClick = onRequestReset,
                enabled = uiState.canResetSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (uiState.isResettingSettings) {
                        "Restoring Defaults..."
                    } else {
                        "Restore Default Settings"
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
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
private fun DifficultyCard(
    selectedDifficulty: DifficultyLevel,
    enabled: Boolean,
    onDifficultySelected: (DifficultyLevel) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
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

                if (
                    index <
                    DifficultyLevel.entries.lastIndex
                ) {
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
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = difficultyLevel.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = difficultyLevel.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PreferenceCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
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
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun PrivacyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Privacy and Control",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Your settings are stored locally on this device. They can be changed or restored at any time.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            Text(text = "Restore Default Settings?")
        },
        text = {
            Text(
                text = "Difficulty and accessibility preferences will return to their original values."
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
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

