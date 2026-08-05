package com.youyangzhao.sourcesense.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Landing : AppDestination(
        route = "landing",
        label = "Home",
        icon = Icons.Default.Home
    )

    object Explore : AppDestination(
        route = "explore",
        label = "Explore",
        icon = Icons.Default.Search
    )

    object SourceDetails : AppDestination(
        route = "source_details",
        label = "Source Details",
        icon = Icons.Default.Search
    )

    object Evaluation : AppDestination(
        route = "evaluation",
        label = "Evaluate",
        icon = Icons.Default.Search
    )

    object Statistics : AppDestination(
        route = "statistics",
        label = "Statistics",
        icon = Icons.Default.Star
    )

    object Settings : AppDestination(
        route = "settings",
        label = "Settings",
        icon = Icons.Default.Settings
    )

    object Result : AppDestination(
        route = "result",
        label = "Result",
        icon = Icons.Default.Star
    )

    companion object {
        // Define destinations shown in the bottom navigation
        val topLevelDestinations = listOf(
            Landing,
            Explore,
            Statistics,
            Settings
        )
    }
}
