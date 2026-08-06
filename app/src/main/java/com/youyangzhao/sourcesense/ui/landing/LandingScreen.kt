package com.youyangzhao.sourcesense.ui.landing

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.roundToInt

// Define the main page background
private val LandingBackground =
    Color(0xFFF9F6FA)

// Define the level card palette
private val HeroPinkLight =
    Color(0xFFFFF4F8)

private val HeroPink =
    Color(0xFFF5D8E5)

private val HeroPinkBorder =
    Color(0xFFE9BFD1)

private val HeroPinkDark =
    Color(0xFFA34F75)

private val HeroText =
    Color(0xFF3A2530)

private val HeroBodyText =
    Color(0xFF654D59)

// Define the learning module card palette
private val ModuleCardLight =
    Color(0xFFFFFFFF)

private val ModuleCardPink =
    Color(0xFFFFF7FA)

private val ModuleCardBorder =
    Color(0xFFEADDE4)

private val ModuleTitleColor =
    Color(0xFF302831)

private val ModuleBodyColor =
    Color(0xFF57505A)

// Define the learning focus section palette
private val FocusBackground =
    Color(0xFFF9EEF3)

private val FocusBorder =
    Color(0xFFEFD9E3)

// Define the primary action colors
private val ButtonPink =
    Color(0xFFBC6388)

private val ButtonPinkDark =
    Color(0xFF95506E)

// Define module completion status colors
private val CompletedGreen =
    Color(0xFF347A5D)

private val CompletedGreenSoft =
    Color(0xFFE4F3EC)

private val NotStartedGray =
    Color(0xFF6F6873)

private val NotStartedGraySoft =
    Color(0xFFF0ECF1)

@Composable
fun LandingRoute(
    viewModel: LandingViewModel,
    onStartModule: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Observe changes to modules, progress and difficulty
    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    LandingScreen(
        uiState = uiState,
        onStartModule = onStartModule,
        onRetry = viewModel::retryLoading,
        modifier = modifier
    )
}

@Composable
fun LandingScreen(
    uiState: LandingUiState,
    onStartModule: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Keep all landing states on the same background
    Surface(
        modifier = modifier.fillMaxSize(),
        color = LandingBackground
    ) {
        when {
            uiState.isLoading -> {
                LandingLoadingContent()
            }

            uiState.errorMessage != null -> {
                LandingErrorContent(
                    message = uiState.errorMessage,
                    onRetry = onRetry
                )
            }

            !uiState.hasModules -> {
                EmptyModulesContent(
                    levelName =
                        uiState.difficultyLevel.displayName
                )
            }

            else -> {
                LandingModuleContent(
                    uiState = uiState,
                    onStartModule = onStartModule
                )
            }
        }
    }
}

@Composable
private fun LandingLoadingContent() {
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
            color = ButtonPink
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Loading learning modules...",
            style =
                MaterialTheme.typography.bodyLarge,
            color =
                MaterialTheme.colorScheme
                    .onSurfaceVariant
        )
    }
}

@Composable
private fun LandingErrorContent(
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
                containerColor = ModuleCardLight
            ),
            border = BorderStroke(
                width = 1.dp,
                color = ModuleCardBorder
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Unable to load modules",
                    style =
                        MaterialTheme.typography
                            .headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = message,
                    style =
                        MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(18.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = ButtonPink
                        )
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
private fun EmptyModulesContent(
    levelName: String
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
                containerColor = ModuleCardLight
            ),
            border = BorderStroke(
                width = 1.dp,
                color = ModuleCardBorder
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No $levelName Modules Yet",
                    style =
                        MaterialTheme.typography
                            .headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = """
                        Modules for this level have not been added yet. Change your current level in Settings.
                    """.trimIndent(),
                    style =
                        MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LandingModuleContent(
    uiState: LandingUiState,
    onStartModule: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "SourceSense",
                modifier = Modifier.fillMaxWidth(),
                style =
                    MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }

        item {
            // Show progress for the selected difficulty level
            CurrentLevelCard(
                levelName =
                    uiState.difficultyLevel.displayName,
                levelDescription =
                    uiState.difficultyLevel.description,
                completedModuleCount =
                    uiState.completedModuleCount,
                totalModuleCount =
                    uiState.totalModuleCount,
                progress =
                    uiState.overallProgress
            )
        }

        item {
            Text(
                text = "Choose a Learning Module",
                style =
                    MaterialTheme.typography
                        .headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Number modules according to their displayed order
        itemsIndexed(
            items = uiState.modules,
            key = { _, moduleUiModel ->
                moduleUiModel.module.id
            }
        ) { index, moduleUiModel ->
            LearningModuleCard(
                moduleNumber = index + 1,
                moduleUiModel = moduleUiModel,
                onStart = {
                    onStartModule(
                        moduleUiModel.module.id
                    )
                }
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun CurrentLevelCard(
    levelName: String,
    levelDescription: String,
    completedModuleCount: Int,
    totalModuleCount: Int,
    progress: Float
) {
    // Prevent invalid progress values from affecting the UI
    val safeProgress =
        progress.coerceIn(
            minimumValue = 0f,
            maximumValue = 1f
        )

    val progressPercentage =
        (safeProgress * 100)
            .roundToInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = HeroPinkBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            HeroPinkLight,
                            HeroPink
                        )
                    )
                )
        ) {
            // Add a soft highlight in the top-right corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = 34.dp,
                        y = (-34).dp
                    )
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(
                            alpha = 0.35f
                        )
                    )
            )

            // Add a second decorative shape for visual balance
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(
                        x = (-34).dp,
                        y = 42.dp
                    )
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(
                        HeroPinkBorder.copy(
                            alpha = 0.24f
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement =
                    Arrangement.spacedBy(13.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    // Identify the purpose of the card
                    Surface(
                        shape =
                            RoundedCornerShape(100.dp),
                        color =
                            Color.White.copy(
                                alpha = 0.76f
                            ),
                        border = BorderStroke(
                            width = 1.dp,
                            color =
                                Color.White.copy(
                                    alpha = 0.9f
                                )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 7.dp
                            ),
                            horizontalArrangement =
                                Arrangement.spacedBy(7.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        HeroPinkDark
                                    )
                            )

                            Text(
                                text = "CURRENT LEVEL",
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelMedium,
                                fontWeight =
                                    FontWeight.Bold,
                                color = ButtonPinkDark
                            )
                        }
                    }

                    // Display the overall completion percentage
                    Surface(
                        shape =
                            RoundedCornerShape(100.dp),
                        color =
                            HeroPinkDark.copy(
                                alpha = 0.11f
                            )
                    ) {
                        Text(
                            text = "$progressPercentage%",
                            modifier = Modifier.padding(
                                horizontal = 13.dp,
                                vertical = 8.dp
                            ),
                            style =
                                MaterialTheme
                                    .typography
                                    .labelLarge,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color = ButtonPinkDark
                        )
                    }
                }

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = levelName,
                        style =
                            MaterialTheme.typography
                                .headlineMedium,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color = HeroText
                    )

                    Text(
                        text = levelDescription,
                        style =
                            MaterialTheme.typography
                                .bodyLarge,
                        color = HeroBodyText
                    )
                }

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text = "Learning progress",
                        style =
                            MaterialTheme.typography
                                .labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = HeroText
                    )

                    // Keep this wording stable for GUI tests
                    Text(
                        text =
                            "$completedModuleCount of " +
                                    "$totalModuleCount " +
                                    "modules completed",
                        style =
                            MaterialTheme.typography
                                .labelMedium,
                        fontWeight =
                            FontWeight.SemiBold,
                        color = HeroBodyText
                    )
                }

                ModuleProgressSegments(
                    completedModuleCount =
                        completedModuleCount,
                    totalModuleCount =
                        totalModuleCount
                )
            }
        }
    }
}

@Composable
private fun ModuleProgressSegments(
    completedModuleCount: Int,
    totalModuleCount: Int
) {
    if (totalModuleCount <= 0) {
        // Show one neutral bar when no modules are available
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(
                    RoundedCornerShape(100.dp)
                )
                .background(
                    Color.White.copy(
                        alpha = 0.82f
                    )
                )
        )

        return
    }

    val safeCompleted =
        completedModuleCount.coerceIn(
            minimumValue = 0,
            maximumValue = totalModuleCount
        )

    // Use one segment for each learning module
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(7.dp)
    ) {
        repeat(totalModuleCount) { index ->
            val isCompleted =
                index < safeCompleted

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(9.dp)
                    .clip(
                        RoundedCornerShape(100.dp)
                    )
                    .background(
                        if (isCompleted) {
                            HeroPinkDark
                        } else {
                            Color.White.copy(
                                alpha = 0.84f
                            )
                        }
                    )
            )
        }
    }
}

@Composable
private fun LearningModuleCard(
    moduleNumber: Int,
    moduleUiModel: LearningModuleUiModel,
    onStart: () -> Unit
) {
    val module =
        moduleUiModel.module

    // Format module numbers as 01, 02 and 03
    val formattedModuleNumber =
        moduleNumber
            .toString()
            .padStart(
                length = 2,
                padChar = '0'
            )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ModuleCardBorder
        ),
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
                            ModuleCardLight,
                            ModuleCardPink
                        )
                    )
                )
        ) {
            // Add a subtle pink shape behind the status badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = 34.dp,
                        y = (-34).dp
                    )
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        HeroPink.copy(
                            alpha = 0.34f
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    // Help users distinguish modules quickly
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(15.dp),
                        color = FocusBackground,
                        border = BorderStroke(
                            width = 1.dp,
                            color = FocusBorder
                        )
                    ) {
                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Text(
                                text =
                                    formattedModuleNumber,
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium,
                                fontWeight =
                                    FontWeight.ExtraBold,
                                color = ButtonPinkDark
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement =
                            Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = module.title,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color = ModuleTitleColor
                        )

                        // Show workload without repeating the module description
                        Text(
                            text =
                                when (
                                    module.questionCount
                                ) {
                                    1 -> {
                                        "1 practice question"
                                    }

                                    else -> {
                                        "${module.questionCount} " +
                                                "practice questions"
                                    }
                                },
                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium,
                            fontWeight =
                                FontWeight.SemiBold,
                            color = ButtonPinkDark
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    ModuleStatusBadge(
                        isCompleted =
                            moduleUiModel.isCompleted
                    )
                }

                // Present the main learning outcome in one focused section
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = FocusBackground,
                    border = BorderStroke(
                        width = 1.dp,
                        color = FocusBorder
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "LEARNING FOCUS",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelSmall,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color = ButtonPinkDark
                        )

                        Text(
                            text = module.learningFocus,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            color = ModuleBodyColor
                        )
                    }
                }

                // Only completed modules display score statistics
                ModuleProgressContent(
                    moduleUiModel =
                        moduleUiModel
                )

                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = ButtonPink,
                            contentColor = Color.White
                        )
                ) {
                    Text(
                        text =
                            if (
                                moduleUiModel.isCompleted
                            ) {
                                "Try Again"
                            } else {
                                "Start Module"
                            },
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ModuleStatusBadge(
    isCompleted: Boolean
) {
    // Use different colors for completed and pending modules
    val containerColor =
        if (isCompleted) {
            CompletedGreenSoft
        } else {
            NotStartedGraySoft
        }

    val contentColor =
        if (isCompleted) {
            CompletedGreen
        } else {
            NotStartedGray
        }

    val statusText =
        if (isCompleted) {
            "Completed"
        } else {
            "Not Started"
        }

    Surface(
        shape = RoundedCornerShape(100.dp),
        color = containerColor
    ) {
        Text(
            text = statusText,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            style =
                MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
private fun ModuleProgressContent(
    moduleUiModel: LearningModuleUiModel
) {
    // Hide empty statistics for modules that have not started
    if (!moduleUiModel.isCompleted) {
        return
    }

    val bestScore =
        moduleUiModel.bestScore

    val bestTotalQuestions =
        moduleUiModel.bestTotalQuestions

    val bestPercentage =
        moduleUiModel.bestPercentage

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {
        if (
            bestScore != null &&
            bestTotalQuestions != null &&
            bestPercentage != null
        ) {
            ModuleStatisticCard(
                text =
                    "Best score: $bestScore / " +
                            "$bestTotalQuestions " +
                            "($bestPercentage%)",
                modifier = Modifier.weight(1f)
            )
        }

        ModuleStatisticCard(
            text =
                when (
                    moduleUiModel.attemptCount
                ) {
                    1 -> {
                        "Attempts: 1"
                    }

                    else -> {
                        "Attempts: " +
                                moduleUiModel.attemptCount
                    }
                },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ModuleStatisticCard(
    text: String,
    modifier: Modifier = Modifier
) {
    // Keep completed-module statistics compact
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color =
            Color.White.copy(
                alpha = 0.82f
            ),
        border = BorderStroke(
            width = 1.dp,
            color = ModuleCardBorder
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 9.dp
            ),
            style =
                MaterialTheme.typography.labelMedium,
            fontWeight =
                FontWeight.SemiBold,
            color = ModuleBodyColor,
            textAlign = TextAlign.Center
        )
    }
}


