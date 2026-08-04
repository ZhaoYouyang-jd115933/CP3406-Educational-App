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

    companion object {
        // Define the main navigation destinations
        val topLevelDestinations = listOf(
            Landing,
            Evaluation,
            Statistics,
            Settings
        )
    }
}

