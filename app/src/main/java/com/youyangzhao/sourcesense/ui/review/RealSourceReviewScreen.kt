package com.youyangzhao.sourcesense.ui.review

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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

// Define the page palette
private val ReviewBackground =
    Color(0xFFF9F6FA)

private val ReviewCardBackground =
    Color(0xFFFFFFFF)

private val ReviewBorder =
    Color(0xFFE9DDE5)

private val ReviewTextPrimary =
    Color(0xFF302A32)

private val ReviewTextSecondary =
    Color(0xFF655C66)

// Define the main pink accent palette
private val ReviewPink =
    Color(0xFFB85F84)

private val ReviewPinkDark =
    Color(0xFF914966)

private val ReviewPinkSoft =
    Color(0xFFF7E7EF)

private val ReviewPinkLight =
    Color(0xFFFFF8FB)

// Define success and error states
private val ReviewSuccess =
    Color(0xFF357A5C)

private val ReviewSuccessSoft =
    Color(0xFFE4F3EC)

private val ReviewError =
    Color(0xFFA84F5B)

private val ReviewErrorSoft =
    Color(0xFFFBE6E8)

@Composable
fun RealSourceReviewRoute(
    viewModel: RealSourceReviewViewModel,
    onBackToExplore: () -> Unit,
    onOpenPaperPage: (AcademicSource) -> Unit,
    modifier: Modifier = Modifier
) {
    // Observe the source review form state
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

    Surface(
        modifier = modifier.fillMaxSize(),
        color = ReviewBackground
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

                OutlinedButton(
                    onClick = onBackToExplore,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = ReviewBorder
                    )
                ) {
                    Text(
                        text = "Back to Explore",
                        fontWeight = FontWeight.SemiBold,
                        color = ReviewTextPrimary
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
                SingleChoiceSection(
                    number = 1,
                    title = "Relevance",
                    prompt = "How well does this source match your topic?",
                    options =
                        SourceRelevanceAssessment.entries,
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
                    number = 2,
                    title = "Publication Information",
                    prompt = "Can you identify and trace this source?",
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
                    number = 3,
                    title = "Currency",
                    prompt = "Is the publication date suitable for this topic?",
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
                    number = 4,
                    title = "Review Depth",
                    prompt = "How much of the source did you review?",
                    options = SourceReviewDepth.entries,
                    selectedOption =
                        uiState.reviewDepth,
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
                    number = 5,
                    title = "Current Decision",
                    prompt = "What is your most defensible decision now?",
                    options =
                        SourceCitationDecision.entries,
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
                        onRetrySaving =
                            onRetrySaving
                    )
                }
            }

            if (uiState.isSaved) {
                item {
                    ReviewSavedCard()
                }

                item {
                    SavedReviewActions(
                        onReadSource = {
                            onOpenPaperPage(source)
                        },
                        onBackToExplore =
                            onBackToExplore
                    )
                }
            } else {
                item {
                    SaveReviewButton(
                        isSaving = uiState.isSaving,
                        enabled = uiState.canSave,
                        onSaveReview = onSaveReview
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
}

@Composable
private fun ReviewHeader() {
    // Keep the page heading clear without extra explanatory copy
    Text(
        text = "Review a Real Source",
        modifier = Modifier.fillMaxWidth(),
        style =
            MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        color = ReviewTextPrimary
    )
}

@Composable
private fun SourceSummaryCard(
    source: AcademicSource,
    searchTopic: String,
    onOpenPaperPage: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ReviewBorder
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
                            ReviewCardBackground,
                            ReviewPinkLight
                        )
                    )
                )
        ) {
            // Add a subtle decorative highlight behind the title
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = 34.dp,
                        y = (-34).dp
                    )
                    .size(108.dp)
                    .clip(CircleShape)
                    .background(
                        ReviewPinkSoft.copy(
                            alpha = 0.8f
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = ReviewPinkSoft
                ) {
                    Text(
                        text = "TOPIC  $searchTopic",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        style =
                            MaterialTheme.typography
                                .labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = ReviewPinkDark
                    )
                }

                // Make the publication title the strongest element
                Text(
                    text = source.title,
                    style =
                        MaterialTheme.typography
                            .headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = ReviewTextPrimary
                )

                SourceDetailCard(
                    label = "Authors",
                    value = source.authorsDisplay
                )

                SourceDetailCard(
                    label = "Publication",
                    value = source.publicationDisplay
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {
                    SourceDetailCard(
                        label = "Year",
                        value = source.yearDisplay,
                        modifier = Modifier.weight(1f)
                    )

                    SourceDetailCard(
                        label = "Source Type",
                        value = source.typeDisplay,
                        modifier = Modifier.weight(1f)
                    )
                }

                SourceDetailCard(
                    label = "DOI",
                    value = source.doi
                )

                Button(
                    onClick = onOpenPaperPage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = ReviewPink
                        )
                ) {
                    Text(
                        text = "Read Source",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceDetailCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.82f),
        border = BorderStroke(
            width = 1.dp,
            color = ReviewBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement =
                Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style =
                    MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = ReviewPinkDark
            )

            Text(
                text = value,
                style =
                    MaterialTheme.typography.bodyMedium,
                color = ReviewTextPrimary
            )
        }
    }
}

@Composable
private fun <T> SingleChoiceSection(
    number: Int,
    title: String,
    prompt: String,
    options: List<T>,
    selectedOption: T?,
    enabled: Boolean,
    optionTitle: (T) -> String,
    optionDescription: (T) -> String,
    onOptionSelected: (T) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = ReviewCardBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ReviewBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                number = number,
                title = title,
                isComplete = selectedOption != null
            )

            Text(
                text = prompt,
                style =
                    MaterialTheme.typography.bodyMedium,
                color = ReviewTextSecondary
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
private fun SectionHeader(
    number: Int,
    title: String,
    isComplete: Boolean,
    optional: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(10.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(12.dp),
            color = ReviewPinkSoft
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ReviewPinkDark
                )
            }
        }

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = ReviewTextPrimary
        )

        when {
            isComplete -> {
                StatusChip(
                    text = "SELECTED",
                    containerColor = ReviewSuccessSoft,
                    contentColor = ReviewSuccess
                )
            }

            optional -> {
                StatusChip(
                    text = "OPTIONAL",
                    containerColor = ReviewPinkSoft,
                    contentColor = ReviewPinkDark
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 5.dp
            ),
            style =
                MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = contentColor
        )
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (selected) {
                    ReviewPinkSoft
                } else {
                    ReviewPinkLight
                }
        ),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color =
                if (selected) {
                    ReviewPink
                } else {
                    ReviewBorder
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
                onClick = null,
                colors =
                    RadioButtonDefaults.colors(
                        selectedColor = ReviewPink,
                        unselectedColor =
                            ReviewTextSecondary
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style =
                        MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = ReviewTextPrimary
                )

                Text(
                    text = description,
                    style =
                        MaterialTheme.typography.bodySmall,
                    color = ReviewTextSecondary
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = ReviewCardBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ReviewBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                number = 6,
                title = "What Must Be Verified?",
                isComplete = selectedItems.isNotEmpty()
            )

            Text(
                text = "Choose at least one item.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color = ReviewTextSecondary
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = {
                    onToggle()
                }
            ),
        shape = RoundedCornerShape(16.dp),
        color =
            if (checked) {
                ReviewPinkSoft
            } else {
                ReviewPinkLight
            },
        border = BorderStroke(
            width = if (checked) 1.5.dp else 1.dp,
            color =
                if (checked) {
                    ReviewPink
                } else {
                    ReviewBorder
                }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                enabled = enabled,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = ReviewPink,
                    uncheckedColor =
                        ReviewTextSecondary,
                    checkmarkColor = Color.White
                )
            )

            Text(
                text = item.displayName,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                style =
                    MaterialTheme.typography.bodyLarge,
                fontWeight =
                    if (checked) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    },
                color = ReviewTextPrimary
            )
        }
    }
}

@Composable
private fun ReflectionSection(
    reflectionNote: String,
    enabled: Boolean,
    onReflectionNoteChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = ReviewCardBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ReviewBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                number = 7,
                title = "Reflection",
                isComplete = reflectionNote.isNotBlank(),
                optional = reflectionNote.isBlank()
            )

            OutlinedTextField(
                value = reflectionNote,
                onValueChange =
                    onReflectionNoteChange,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text =
                            "Why might you use, reject or investigate this source?"
                    )
                },
                minLines = 3,
                maxLines = 6,
                shape = RoundedCornerShape(18.dp),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ReviewPink,
                        focusedLabelColor = ReviewPinkDark,
                        cursorColor = ReviewPinkDark
                    )
            )
        }
    }
}

@Composable
private fun SaveReviewButton(
    isSaving: Boolean,
    enabled: Boolean,
    onSaveReview: () -> Unit
) {
    Button(
        onClick = onSaveReview,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ReviewPink,
            contentColor = Color.White,
            disabledContainerColor =
                Color(0xFFE4DFE4),
            disabledContentColor =
                Color(0xFFAAA3AA)
        )
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )

            Text(
                text = "Saving...",
                modifier = Modifier.padding(
                    start = 10.dp
                ),
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = "Save Source Review",
                fontWeight = FontWeight.Bold
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ReviewErrorSoft
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ReviewError.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Unable to Save Review",
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ReviewError
            )

            Text(
                text = message,
                style =
                    MaterialTheme.typography.bodyMedium,
                color = ReviewTextPrimary
            )

            if (canRetry) {
                OutlinedButton(
                    onClick = onRetrySaving,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = ReviewError
                    )
                ) {
                    Text(
                        text = "Try Again",
                        color = ReviewError,
                        fontWeight = FontWeight.Bold
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
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = ReviewSuccessSoft
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ReviewSuccess.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Review Saved",
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = ReviewSuccess
            )

            // Keep this confirmation text stable for UI tests
            Text(
                text =
                    "Your structured review has been stored locally on this device.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color = ReviewTextPrimary
            )
        }
    }
}

@Composable
private fun SavedReviewActions(
    onReadSource: () -> Unit,
    onBackToExplore: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onReadSource,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(
                width = 1.dp,
                color = ReviewPink
            )
        ) {
            Text(
                text = "Read Source",
                fontWeight = FontWeight.SemiBold,
                color = ReviewPinkDark
            )
        }

        Button(
            onClick = onBackToExplore,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ReviewPink
            )
        ) {
            Text(
                text = "Back to Explore",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MissingSourceContent(
    onBackToExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = ReviewBackground
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
                    containerColor = ReviewCardBackground
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = ReviewBorder
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "No Source Selected",
                        style =
                            MaterialTheme.typography
                                .headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        color = ReviewTextPrimary
                    )

                    Text(
                        text =
                            "Return to Explore and choose a source to evaluate.",
                        style =
                            MaterialTheme.typography.bodyLarge,
                        color = ReviewTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = onBackToExplore,
                        shape = RoundedCornerShape(18.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = ReviewPink
                            )
                    ) {
                        Text(
                            text = "Back to Explore",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

