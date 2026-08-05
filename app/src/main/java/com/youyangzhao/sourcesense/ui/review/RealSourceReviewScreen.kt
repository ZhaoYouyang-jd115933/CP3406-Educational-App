package com.youyangzhao.sourcesense.ui.review

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.domain.model.AcademicSource
import com.youyangzhao.sourcesense.domain.model.PublicationInformationAssessment
import com.youyangzhao.sourcesense.domain.model.SourceCitationDecision
import com.youyangzhao.sourcesense.domain.model.SourceCurrencyAssessment
import com.youyangzhao.sourcesense.domain.model.SourceRelevanceAssessment
import com.youyangzhao.sourcesense.domain.model.SourceReviewDepth
import com.youyangzhao.sourcesense.domain.model.SourceVerificationItem

@Composable
fun RealSourceReviewRoute(
    viewModel: RealSourceReviewViewModel,
    onBackToExplore: () -> Unit,
    onOpenPaperPage: (AcademicSource) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    RealSourceReviewScreen(
        uiState = uiState,
        onBackToExplore = onBackToExplore,
        onOpenPaperPage = onOpenPaperPage,
        onRelevanceSelected =
            viewModel::selectRelevance,
        onPublicationInformationSelected =
            viewModel::selectPublicationInformation,
        onCurrencySelected =
            viewModel::selectCurrency,
        onReviewDepthSelected =
            viewModel::selectReviewDepth,
        onCitationDecisionSelected =
            viewModel::selectCitationDecision,
        onVerificationItemToggled =
            viewModel::toggleVerificationItem,
        onReflectionNoteChange =
            viewModel::updateReflectionNote,
        onSaveReview = viewModel::saveReview,
        onRetrySaving = viewModel::retrySaving,
        modifier = modifier
    )
}

@Composable
fun RealSourceReviewScreen(
    uiState: RealSourceReviewUiState,
    onBackToExplore: () -> Unit,
    onOpenPaperPage: (AcademicSource) -> Unit,
    onRelevanceSelected:
        (SourceRelevanceAssessment) -> Unit,
    onPublicationInformationSelected:
        (PublicationInformationAssessment) -> Unit,
    onCurrencySelected:
        (SourceCurrencyAssessment) -> Unit,
    onReviewDepthSelected:
        (SourceReviewDepth) -> Unit,
    onCitationDecisionSelected:
        (SourceCitationDecision) -> Unit,
    onVerificationItemToggled:
        (SourceVerificationItem) -> Unit,
    onReflectionNoteChange: (String) -> Unit,
    onSaveReview: () -> Unit,
    onRetrySaving: () -> Unit,
    modifier: Modifier = Modifier
) {
    val source = uiState.source

    if (source == null) {
        MissingSourceContent(
            onBackToExplore = onBackToExplore,
            modifier = modifier
        )

        return
    }

    LazyColumn(
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

            OutlinedButton(
                onClick = onBackToExplore
            ) {
                Text(
                    text = "Back to Explore"
                )
            }
        }

        item {
            ReviewHeader()
        }

        item {
            SourceSummaryCard(
                source = source,
                searchTopic = uiState.searchTopic,
                onOpenPaperPage = {
                    onOpenPaperPage(source)
                }
            )
        }

        item {
            LearningConnectionCard()
        }

        item {
            MetadataLimitCard()
        }

        item {
            SingleChoiceSection(
                title = "1. Relevance",
                supportingText =
                    "How closely does this source match your search topic?",
                options = SourceRelevanceAssessment.entries,
                selectedOption =
                    uiState.relevanceAssessment,
                enabled = uiState.canEdit,
                optionTitle = { option ->
                    option.displayName
                },
                optionDescription = { option ->
                    option.description
                },
                onOptionSelected =
                    onRelevanceSelected
            )
        }

        item {
            SingleChoiceSection(
                title = "2. Publication Information",
                supportingText =
                    "Is the publication record clear enough to identify and trace the source?",
                options =
                    PublicationInformationAssessment.entries,
                selectedOption =
                    uiState
                        .publicationInformationAssessment,
                enabled = uiState.canEdit,
                optionTitle = { option ->
                    option.displayName
                },
                optionDescription = { option ->
                    option.description
                },
                onOptionSelected =
                    onPublicationInformationSelected
            )
        }

        item {
            SingleChoiceSection(
                title = "3. Currency",
                supportingText =
                    "Is the publication date appropriate for this topic?",
                options =
                    SourceCurrencyAssessment.entries,
                selectedOption =
                    uiState.currencyAssessment,
                enabled = uiState.canEdit,
                optionTitle = { option ->
                    option.displayName
                },
                optionDescription = { option ->
                    option.description
                },
                onOptionSelected =
                    onCurrencySelected
            )
        }

        item {
            SingleChoiceSection(
                title = "4. Review Depth",
                supportingText =
                    "Be precise about how much of the source you have actually reviewed.",
                options = SourceReviewDepth.entries,
                selectedOption = uiState.reviewDepth,
                enabled = uiState.canEdit,
                optionTitle = { option ->
                    option.displayName
                },
                optionDescription = { option ->
                    option.description
                },
                onOptionSelected =
                    onReviewDepthSelected
            )
        }

        item {
            SingleChoiceSection(
                title = "5. Current Decision",
                supportingText =
                    "What is the most defensible decision based on the information reviewed so far?",
                options = SourceCitationDecision.entries,
                selectedOption =
                    uiState.citationDecision,
                enabled = uiState.canEdit,
                optionTitle = { option ->
                    option.displayName
                },
                optionDescription = { option ->
                    option.description
                },
                onOptionSelected =
                    onCitationDecisionSelected
            )
        }

        item {
            VerificationSection(
                selectedItems =
                    uiState.verificationItems,
                enabled = uiState.canEdit,
                onItemToggled =
                    onVerificationItemToggled
            )
        }

        item {
            ReflectionSection(
                reflectionNote =
                    uiState.reflectionNote,
                enabled = uiState.canEdit,
                onReflectionNoteChange =
                    onReflectionNoteChange
            )
        }

        uiState.errorMessage?.let { errorMessage ->
            item {
                SaveErrorCard(
                    message = errorMessage,
                    canRetry =
                        !uiState.isSaving &&
                                !uiState.isSaved,
                    onRetrySaving = onRetrySaving
                )
            }
        }

        if (uiState.isSaved) {
            item {
                ReviewSavedCard()
            }
        }

        item {
            Button(
                onClick = onSaveReview,
                enabled = uiState.canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(20.dp),
                        strokeWidth = 2.dp
                    )

                    Text(
                        text = "Saving...",
                        modifier = Modifier.padding(
                            start = 10.dp
                        )
                    )
                } else {
                    Text(
                        text = if (uiState.isSaved) {
                            "Review Saved"
                        } else {
                            "Save Source Review"
                        }
                    )
                }
            }
        }

        if (uiState.isSaved) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onOpenPaperPage(source)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Read Source"
                        )
                    }

                    Button(
                        onClick = onBackToExplore,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Back to Explore"
                        )
                    }
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
private fun ReviewHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Review a Real Source",
            style =
                MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text =
                "Apply the evaluation skills from the learning modules to a real Crossref publication.",
            style = MaterialTheme.typography.bodyLarge,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SourceSummaryCard(
    source: AcademicSource,
    searchTopic: String,
    onOpenPaperPage: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Search Topic",
                style =
                    MaterialTheme.typography.labelLarge,
                color =
                    MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = searchTopic,
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = source.title,
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            ReviewSourceDetail(
                label = "Authors",
                value = source.authorsDisplay
            )

            ReviewSourceDetail(
                label = "Publication",
                value = source.publicationDisplay
            )

            ReviewSourceDetail(
                label = "Year",
                value = source.yearDisplay
            )

            ReviewSourceDetail(
                label = "Source Type",
                value = source.typeDisplay
            )

            ReviewSourceDetail(
                label = "DOI",
                value = source.doi
            )

            source.abstractText
                ?.takeIf { abstractText ->
                    abstractText.isNotBlank()
                }
                ?.let { abstractText ->
                    ReviewSourceDetail(
                        label = "Abstract",
                        value = abstractText
                    )
                }

            OutlinedButton(
                onClick = onOpenPaperPage,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Read Source"
                )
            }
        }
    }
}

@Composable
private fun ReviewSourceDetail(
    label: String,
    value: String
) {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style =
                MaterialTheme.typography.labelMedium,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun LearningConnectionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Learn → Apply → Reflect",
                style =
                    MaterialTheme.typography.titleMedium,
                color =
                    MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "The learning modules teach evaluation concepts. This review helps you apply those concepts to a real publication and record what still needs verification.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun MetadataLimitCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Important Evidence Limit",
                style =
                    MaterialTheme.typography.titleMedium,
                color =
                    MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "Crossref metadata cannot confirm the study method, sample quality, statistical interpretation, limitations, funding or research ethics. Read the abstract or full paper before using the source in academic work.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun <T> SingleChoiceSection(
    title: String,
    supportingText: String,
    options: List<T>,
    selectedOption: T?,
    enabled: Boolean,
    optionTitle: (T) -> String,
    optionDescription: (T) -> String,
    onOptionSelected: (T) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = supportingText,
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            options.forEach { option ->
                ReviewChoiceRow(
                    title = optionTitle(option),
                    description =
                        optionDescription(option),
                    selected = option == selectedOption,
                    enabled = enabled,
                    onSelected = {
                        onOptionSelected(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun ReviewChoiceRow(
    title: String,
    description: String,
    selected: Boolean,
    enabled: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onSelected
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme
                    .primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
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
                enabled = enabled,
                onClick = null
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                verticalArrangement =
                    Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style =
                        MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = description,
                    style =
                        MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun VerificationSection(
    selectedItems: Set<SourceVerificationItem>,
    enabled: Boolean,
    onItemToggled: (SourceVerificationItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "6. What Must Be Verified?",
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "Select at least one item that should be checked before relying on this source.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            SourceVerificationItem.entries.forEach { item ->
                VerificationItemRow(
                    item = item,
                    checked = item in selectedItems,
                    enabled = enabled,
                    onToggle = {
                        onItemToggled(item)
                    }
                )
            }
        }
    }
}

@Composable
private fun VerificationItemRow(
    item: SourceVerificationItem,
    checked: Boolean,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = {
                    onToggle()
                }
            )
            .padding(vertical = 6.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            enabled = enabled,
            onCheckedChange = null
        )

        Text(
            text = item.displayName,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            style =
                MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ReflectionSection(
    reflectionNote: String,
    enabled: Boolean,
    onReflectionNoteChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "7. Reflection",
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "Record a short note about why you may use, reject or investigate this source further.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = reflectionNote,
                onValueChange =
                    onReflectionNoteChange,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        text = "Reflection note"
                    )
                },
                placeholder = {
                    Text(
                        text =
                            "Example: The title is relevant, but I still need to verify the sample and limitations."
                    )
                },
                minLines = 3,
                maxLines = 6
            )
        }
    }
}

@Composable
private fun SaveErrorCard(
    message: String,
    canRetry: Boolean,
    onRetrySaving: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Unable to Save Review",
                style =
                    MaterialTheme.typography.titleMedium,
                color =
                    MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = message,
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onErrorContainer
            )

            if (canRetry) {
                OutlinedButton(
                    onClick = onRetrySaving
                ) {
                    Text(
                        text = "Try Again"
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewSavedCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Review Saved",
                style =
                    MaterialTheme.typography.titleMedium,
                color =
                    MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "Your structured review has been stored locally on this device.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun MissingSourceContent(
    onBackToExplore: () -> Unit,
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
            text = "No Source Selected",
            style =
                MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text =
                "Return to Explore and choose Evaluate Source from a search result.",
            style = MaterialTheme.typography.bodyLarge,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onBackToExplore
        ) {
            Text(
                text = "Back to Explore"
            )
        }
    }
}

