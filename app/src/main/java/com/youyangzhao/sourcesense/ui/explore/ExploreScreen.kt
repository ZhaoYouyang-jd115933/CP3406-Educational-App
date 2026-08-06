package com.youyangzhao.sourcesense.ui.explore

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.domain.model.AcademicSource
import com.youyangzhao.sourcesense.domain.model.RealSourceReview

// Define the overall explore page palette
private val ExploreBackground =
    Color(0xFFF8F5FA)

private val ExploreBorder =
    Color(0xFFE9DEE5)

private val ExploreTextPrimary =
    Color(0xFF2F2A32)

private val ExploreTextSecondary =
    Color(0xFF625A65)

// Define the brand pink palette
private val ExplorePink =
    Color(0xFFBC6388)

private val ExplorePinkDark =
    Color(0xFF964E6E)

private val ExplorePinkSoft =
    Color(0xFFF7EAF0)

private val ExplorePinkLight =
    Color(0xFFFFF8FB)

// Define a soft blue accent for source cards
private val ExploreBlueSoft =
    Color(0xFFEAF0FA)

private val ExploreBlueText =
    Color(0xFF48648F)

// Define success and error states
private val ReviewGreen =
    Color(0xFF357A5C)

private val ReviewGreenSoft =
    Color(0xFFE5F3EC)

private val ReviewErrorSoft =
    Color(0xFFFBE7E8)

@Composable
fun ExploreRoute(
    viewModel: ExploreViewModel,
    onOpenSource: (AcademicSource) -> Unit,
    onEvaluateSource:
        (AcademicSource, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    ExploreScreen(
        uiState = uiState,
        onQueryChange = viewModel::updateQuery,
        onSearch = viewModel::searchSources,
        onRetry = viewModel::retrySearch,
        onClear = viewModel::clearSearch,
        onOpenSource = onOpenSource,
        onEvaluateSource = { source ->
            onEvaluateSource(
                source,
                uiState.query.trim()
            )
        },
        modifier = modifier
    )
}

@Composable
fun ExploreScreen(
    uiState: ExploreUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onClear: () -> Unit,
    onOpenSource: (AcademicSource) -> Unit,
    onEvaluateSource: (AcademicSource) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = ExploreBackground
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
                ExploreHeader()
            }

            item {
                SourceSearchSection(
                    query = uiState.query,
                    canSearch = uiState.canSearch,
                    hasSearchContent =
                        uiState.hasSearched ||
                                uiState.query.isNotBlank(),
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    onClear = onClear
                )
            }

            if (uiState.hasSavedReviews) {
                item {
                    SavedReviewsCard(
                        reviews =
                            uiState.savedReviews.take(3),
                        totalReviewCount =
                            uiState.savedReviews.size
                    )
                }
            } else if (
                uiState.reviewHistoryErrorMessage != null
            ) {
                item {
                    ReviewHistoryErrorCard(
                        message =
                            uiState.reviewHistoryErrorMessage
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    item {
                        LoadingContent()
                    }
                }

                uiState.errorMessage != null -> {
                    item {
                        SearchErrorContent(
                            message =
                                uiState.errorMessage,
                            onRetry = onRetry
                        )
                    }
                }

                uiState.showEmptyResults -> {
                    item {
                        EmptyResultsContent(
                            query = uiState.query
                        )
                    }
                }

                uiState.hasResults -> {
                    item {
                        ResultsHeader(
                            resultCount =
                                uiState.sources.size
                        )
                    }

                    items(
                        items = uiState.sources,
                        key = { source ->
                            source.doi
                        }
                    ) { source ->
                        AcademicSourceCard(
                            source = source,
                            onOpenSource = {
                                onOpenSource(source)
                            },
                            onEvaluateSource = {
                                onEvaluateSource(source)
                            }
                        )
                    }
                }

                else -> {
                    item {
                        ExploreIntroductionCard()
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
private fun ExploreHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {
        // Keep the page title strong and uncluttered
        Text(
            text = "Explore Real Sources",
            style =
                MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = ExploreTextPrimary
        )
    }
}

@Composable
private fun SourceSearchSection(
    query: String,
    canSearch: Boolean,
    hasSearchContent: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ExploreBorder
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
                            Color.White,
                            ExplorePinkLight
                        )
                    )
                )
        ) {
            // Add a soft decorative circle behind the card
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = 34.dp,
                        y = (-30).dp
                    )
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        ExplorePinkSoft.copy(
                            alpha = 0.8f
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {
                    SectionChip(
                        text = "STEP 1"
                    )

                    Text(
                        text = "Search Academic Sources",
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color = ExploreTextPrimary
                    )
                }

                Text(
                    text =
                        "Find a real article by topic, title, author or DOI.",
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color = ExploreTextSecondary
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    HintChip(text = "Topic")
                    HintChip(text = "Article title")
                    HintChip(text = "DOI")
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = "Topic, title or DOI"
                        )
                    },
                    placeholder = {
                        Text(
                            text =
                                "e.g. artificial intelligence education"
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor =
                                ExplorePink,
                            focusedLabelColor =
                                ExplorePinkDark,
                            cursorColor =
                                ExplorePinkDark
                        ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (canSearch) {
                                onSearch()
                            }
                        }
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSearch,
                        enabled = canSearch,
                        modifier = Modifier.weight(1f),
                        shape =
                            RoundedCornerShape(18.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    ExplorePink
                            )
                    ) {
                        Text(
                            text = "Search",
                            fontWeight =
                                FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onClear,
                        enabled = hasSearchContent,
                        modifier = Modifier.weight(1f),
                        shape =
                            RoundedCornerShape(18.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = ExploreBorder
                        )
                    ) {
                        Text(
                            text = "Clear",
                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
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
        color = ExplorePinkSoft
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            style =
                MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = ExplorePinkDark
        )
    }
}

@Composable
private fun HintChip(
    text: String
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = Color.White,
        border = BorderStroke(
            width = 1.dp,
            color = ExploreBorder
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            style =
                MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = ExploreTextSecondary
        )
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            color = ExplorePink
        )

        Text(
            text = "Searching Crossref...",
            style =
                MaterialTheme.typography.bodyLarge,
            color = ExploreTextSecondary
        )
    }
}

@Composable
private fun SearchErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = ReviewErrorSoft
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFE3B0B5)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Search Unavailable",
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = ExploreTextPrimary
            )

            Text(
                text = message,
                style =
                    MaterialTheme.typography.bodyMedium,
                color = ExploreTextSecondary
            )

            OutlinedButton(
                onClick = onRetry,
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "Try Again",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyResultsContent(
    query: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ExploreBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "No Sources Found",
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = ExploreTextPrimary
            )

            Text(
                text =
                    "No usable source was found for \"$query\". Try broader keywords or a different title.",
                style =
                    MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = ExploreTextSecondary
            )
        }
    }
}

@Composable
private fun ResultsHeader(
    resultCount: Int
) {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Search Results",
            style =
                MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = ExploreTextPrimary
        )

        Text(
            text = when (resultCount) {
                1 -> {
                    "1 academic source found"
                }

                else -> {
                    "$resultCount academic sources found"
                }
            },
            style =
                MaterialTheme.typography.bodyMedium,
            color = ExploreTextSecondary
        )
    }
}

@Composable
private fun AcademicSourceCard(
    source: AcademicSource,
    onOpenSource: () -> Unit,
    onEvaluateSource: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ExploreBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = source.title,
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = ExploreTextPrimary
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                SourceMetaChip(
                    text = source.typeDisplay,
                    containerColor = ExplorePinkSoft,
                    textColor = ExplorePinkDark
                )

                SourceMetaChip(
                    text = source.yearDisplay,
                    containerColor = ExploreBlueSoft,
                    textColor = ExploreBlueText
                )
            }

            SourceInformationRow(
                label = "Authors",
                value = source.authorsDisplay
            )

            SourceInformationRow(
                label = "Publication",
                value = source.publicationDisplay
            )

            SourceInformationRow(
                label = "DOI",
                value = source.doi
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                SourceMetaChip(
                    text = if (source.hasAbstract) {
                        "Abstract available"
                    } else {
                        "Abstract unavailable"
                    },
                    containerColor = if (source.hasAbstract) {
                        ReviewGreenSoft
                    } else {
                        ExplorePinkSoft
                    },
                    textColor = if (source.hasAbstract) {
                        ReviewGreen
                    } else {
                        ExplorePinkDark
                    }
                )

                SourceMetaChip(
                    text = "Crossref metadata",
                    containerColor = ExploreBlueSoft,
                    textColor = ExploreBlueText
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onEvaluateSource,
                    modifier = Modifier.weight(1f),
                    shape =
                        RoundedCornerShape(18.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                ExplorePink
                        )
                ) {
                    Text(
                        text = "Evaluate",
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onOpenSource,
                    modifier = Modifier.weight(1f),
                    shape =
                        RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "Read Source",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceMetaChip(
    text: String,
    containerColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            style =
                MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun SourceInformationRow(
    label: String,
    value: String
) {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            style =
                MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = ExploreTextSecondary
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.bodyMedium,
            color = ExploreTextPrimary
        )
    }
}

@Composable
private fun SavedReviewsCard(
    reviews: List<RealSourceReview>,
    totalReviewCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ExploreBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                SectionChip(text = "SAVED")

                Text(
                    text = "Your Source Reviews",
                    style =
                        MaterialTheme.typography
                            .titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = ExploreTextPrimary
                )
            }

            Text(
                text = when (totalReviewCount) {
                    1 -> "1 structured review saved locally"
                    else ->
                        "$totalReviewCount structured reviews saved locally"
                },
                style =
                    MaterialTheme.typography.bodyMedium,
                color = ExploreTextSecondary
            )

            reviews.forEach { review ->
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = ExplorePinkLight,
                    border = BorderStroke(
                        width = 1.dp,
                        color = ExploreBorder
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = review.title,
                            style =
                                MaterialTheme.typography
                                    .bodyLarge,
                            fontWeight =
                                FontWeight.SemiBold,
                            color = ExploreTextPrimary
                        )

                        Text(
                            text =
                                "${review.citationDecision.displayName} · ${review.reviewDepth.displayName}",
                            style =
                                MaterialTheme.typography
                                    .labelMedium,
                            color = ExplorePinkDark,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "Topic: ${review.searchTopic}",
                            style =
                                MaterialTheme.typography
                                    .bodySmall,
                            color = ExploreTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewHistoryErrorCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = ReviewErrorSoft
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFE3B0B5)
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(18.dp),
            style =
                MaterialTheme.typography.bodyMedium,
            color = ExploreTextPrimary
        )
    }
}

@Composable
private fun ExploreIntroductionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ExploreBorder
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White,
                            ExplorePinkLight
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {
                    SectionChip(text = "STEP 2")

                    Text(
                        text = "Apply Your Skills",
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color = ExploreTextPrimary
                    )
                }

                Text(
                    text =
                        "Use a real publication to practise the same evaluation skills from your learning modules.",
                    style =
                        MaterialTheme.typography.bodyLarge,
                    color = ExploreTextPrimary
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(
                        width = 1.dp,
                        color = ExploreBorder
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        WorkflowStep(
                            number = "1",
                            text = "Search for a real academic source"
                        )

                        WorkflowStep(
                            number = "2",
                            text = "Read the official source page"
                        )

                        WorkflowStep(
                            number = "3",
                            text = "Evaluate and save your review"
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = ExploreBlueSoft
                ) {
                    Text(
                        text =
                            "Important: Crossref gives publication metadata. It does not prove study quality by itself.",
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 12.dp
                        ),
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color = ExploreBlueText,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkflowStep(
    number: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = ExplorePinkSoft
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    style =
                        MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = ExplorePinkDark
                )
            }
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = text,
            style =
                MaterialTheme.typography.bodyMedium,
            color = ExploreTextPrimary
        )
    }
}

