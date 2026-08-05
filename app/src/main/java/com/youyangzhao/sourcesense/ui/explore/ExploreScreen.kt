package com.youyangzhao.sourcesense.ui.explore

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youyangzhao.sourcesense.domain.model.AcademicSource

@Composable
fun ExploreRoute(
    viewModel: ExploreViewModel,
    onSourceSelected: (AcademicSource) -> Unit,
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
        onSourceSelected = onSourceSelected,
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
    onSourceSelected: (AcademicSource) -> Unit,
    modifier: Modifier = Modifier
) {
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
                        onOpenPaperPage = {
                            onSourceSelected(source)
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

@Composable
private fun ExploreHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Explore Real Sources",
            style =
                MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = """
                Search Crossref for real academic publications and inspect their publication information.
            """.trimIndent(),
            style = MaterialTheme.typography.bodyLarge,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Search Academic Sources",
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = """
                    Enter a topic, article title, author or DOI.
                """.trimIndent(),
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

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
                        text = "e.g. artificial intelligence education"
                    )
                },
                singleLine = true,
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
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Search")
                }

                OutlinedButton(
                    onClick = onClear,
                    enabled = hasSearchContent,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Clear")
                }
            }
        }
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
        CircularProgressIndicator()

        Text(
            text = "Searching Crossref...",
            style =
                MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SearchErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Search Unavailable",
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = message,
                style =
                    MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onRetry
            ) {
                Text(text = "Try Again")
            }
        }
    }
}

@Composable
private fun EmptyResultsContent(
    query: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
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
                fontWeight = FontWeight.Bold
            )

            Text(
                text = """
                    Crossref did not return usable academic sources for "$query". Try broader or different keywords.
                """.trimIndent(),
                style =
                    MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
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
            fontWeight = FontWeight.Bold
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
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AcademicSourceCard(
    source: AcademicSource,
    onOpenPaperPage: () -> Unit
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
                text = source.title,
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = buildString {
                    append(source.typeDisplay)
                    append(" · ")
                    append(source.yearDisplay)
                },
                style =
                    MaterialTheme.typography.labelLarge,
                color =
                    MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

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

            if (source.hasAbstract) {
                Text(
                    text = "Abstract available",
                    style =
                        MaterialTheme.typography.labelMedium,
                    color =
                        MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "Abstract unavailable",
                    style =
                        MaterialTheme.typography.labelMedium,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "Real publication record from Crossref",
                style =
                    MaterialTheme.typography.labelMedium,
                color =
                    MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = onOpenPaperPage,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Open Paper Page"
                )
            }
        }
    }
}

@Composable
private fun SourceInformationRow(
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
            fontWeight = FontWeight.Bold,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ExploreIntroductionCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Apply Your Skills",
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = """
                    Search for a real academic publication, inspect its metadata and open its official DOI page.
                """.trimIndent(),
                style =
                    MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Crossref may provide:",
                style =
                    MaterialTheme.typography.labelLarge,
                color =
                    MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = """
                    • Article title and authors
                    • Publication year
                    • Journal or publication name
                    • Publisher and source type
                    • DOI and official publication page
                    • Abstract for some records
                """.trimIndent(),
                style =
                    MaterialTheme.typography.bodyMedium
            )

            Text(
                text = """
                    The official publication page may provide an abstract, full text, PDF access or institutional access options. Availability is controlled by the publisher.
                """.trimIndent(),
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

